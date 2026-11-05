package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.EditorialContent
import com.aladdin.dailyplate.domain.policy.*
import com.aladdin.dailyplate.domain.pricing.*
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.math.abs
import kotlin.random.Random

/** Pure bounded search. No profile, plan, diary or target writes. Persisting callers must compare
 * current versions transactionally (K16); this result is an immutable generation preview. */
object WeeklyPlanGenerator {
    fun generate(q: PlanGenerationRequest, profile: UserProfile, policy: NutritionPolicy?, data: WeeklyPlanningData) =
        generateInternal(q, profile, policy, data, false)

    internal fun generateTest(q: PlanGenerationRequest, profile: UserProfile, policy: NutritionPolicy?, data: WeeklyPlanningData) =
        generateInternal(q, profile, policy, data, true)

    private fun generateInternal(q: PlanGenerationRequest, profile: UserProfile, policy: NutritionPolicy?,
                                 data: WeeklyPlanningData, test: Boolean): PlanGenerationResult {
        val rules = mutableListOf<CandidateRuleResult>()
        var nodes = 0
        var exhausted = false
        fun rule(id: String, status: RuleStatus, reason: String) = CandidateRuleResult(id, q.candidate.policy,
            "week", "matches", null, null, null, status, listOf(q.search.ref.id),
            if (status == RuleStatus.UNKNOWN) listOf(id) else emptyList(), reason)
        fun finish(status: GenerationStatus, plan: GeneratedWeek? = null) =
            PlanGenerationResult(status, test, plan, rules.toList(), nodes)
        if (q.goal != profile.goal || q.mealsPerDay != profile.mealsPerDay) {
            rules += rule("profile_selection", RuleStatus.FAIL, "Goal or meal count differs from the exact profile version")
            return finish(GenerationStatus.STALE_INPUT)
        }
        val candidates = if (test) RecipeCandidateFilter.filterPlanningTest(q.candidate, profile, policy, data.catalog)
            else RecipeCandidateFilter.filter(q.candidate, profile, policy, data.catalog)
        rules += candidates.ruleResults
        if (candidates.status != CandidateStatus.SUCCESS) return finish(GenerationStatus.valueOf(candidates.status.name))
        // Use the current K08-v2 profile adapter, never a caller-supplied target or arbitrary tolerance.
        val assessment = NutritionPolicyEngine.assessCurrent(profile, q.candidate.at)
        val preview = assessment.currentCalculation
        if (assessment.issues.isNotEmpty() || preview?.failure != null || preview?.targetKcal == null) {
            rules += rule("current_target", RuleStatus.FAIL, "Current K08 screening/target is unavailable")
            return finish(GenerationStatus.POLICY_UNAVAILABLE)
        }
        if (profile.dietaryRestrictions.isNotEmpty()) {
            rules += rule("restricted_diet_coverage", RuleStatus.UNKNOWN, "Restricted-diet nutrient/protein-quality coverage is not implemented")
            return finish(GenerationStatus.INCOMPLETE_DATA)
        }
        val recipes = data.catalog.recipes.mapNotNull { (it.content as? EditorialContent.RecipeContent)?.recipe }
            .associateBy { it.ref }
        val options = mutableListOf<Pair<Recipe, Double>>()
        for (candidate in candidates.candidates) {
            val recipe = recipes.getValue(candidate.recipe)
            if (recipe.yieldCookedGrams == null || data.evidence[recipe.ref] == null) continue
            val range = candidate.portion
            val count = (range.maxGrams - range.minGrams) / range.stepGrams
            if (!count.isFinite() || count >= q.search.maximumNodes - options.size) { exhausted = true; break }
            for (i in 0..count.toInt()) options += recipe to (range.minGrams + i * range.stepGrams)
        }
        if (exhausted) {
            rules += rule("search_budget", RuleStatus.FAIL, "Portion grid exceeds the bounded search budget")
            return finish(GenerationStatus.TEMPORARY_FAILURE)
        }
        if (options.isEmpty()) {
            rules += rule("portion_evidence", RuleStatus.UNKNOWN, "No candidate has cooked yield and exact-version planning evidence")
            return finish(GenerationStatus.INCOMPLETE_DATA)
        }
        val shuffled = options.shuffled(Random(q.seed))
        fun meal(option: Pair<Recipe, Double>, index: Int): GeneratedMeal {
            val (recipe, grams) = option
            return GeneratedMeal(CalendarDate(LocalDate.parse(q.weekStart.iso).plusDays((index / q.mealsPerDay).toLong()).toString()),
                index % q.mealsPerDay, recipe.ref, grams,
                WeeklyNutrition.scale(recipe.nutrientsTotal, grams / requireNotNull(recipe.yieldCookedGrams)),
                "${recipe.time.totalMinutes} dəqiqə; seçilmiş allergiya, region və porsiya filtrlərindən keçir. Qida əhatəsi ayrıca göstərilir.")
        }
        fun price(meals: List<GeneratedMeal>): ShoppingCost {
            val needs = meals.flatMap { m ->
                val r = recipes.getValue(m.recipe)
                r.ingredients.map { ingredient -> ShoppingNeed(ShoppingProduct(ingredient.food.id,
                    data.catalog.ingredients.getValue(ingredient.food).food.preparationState),
                    BigDecimal.valueOf(ingredient.edibleGrams) * BigDecimal.valueOf(m.portionGrams)
                        .divide(BigDecimal.valueOf(requireNotNull(r.yieldCookedGrams)), java.math.MathContext.DECIMAL128)) }
            }
            val currency = profile.budget.currency ?: "XXX"
            return if (test) ShoppingPricing.calculateTest(needs, data.prices, profile.budget.region, currency, q.candidate.at, data.freshness)
                else ShoppingPricing.calculate(needs, data.prices, profile.budget.region, currency, q.candidate.at, data.freshness)
        }
        fun weekChecks(meals: List<GeneratedMeal>, cost: ShoppingCost): List<CandidateRuleResult> {
            val result = WeeklyCoverage.checks(meals, data, q.candidate.policy, "week", PlanningWindow.WEEK).toMutableList()
            val evidence = meals.map { data.evidence.getValue(it.recipe) }
            for ((name, groups) in listOf("vegetable_variety" to evidence.map { it.vegetableSources },
                "grain_variety" to evidence.map { it.grainSources }, "protein_variety" to evidence.map { it.proteinSources })) {
                result += rule(name, when { groups.any { it == null } -> RuleStatus.UNKNOWN
                    groups.filterNotNull().flatten().distinct().size >= q.search.minimumSourcesPerGroup -> RuleStatus.PASS
                    else -> RuleStatus.FAIL }, "Versioned product variety minimum: ${q.search.minimumSourcesPerGroup} mapped sources")
            }
            val limit = profile.budget.weeklyAmount
            result += rule("package_budget", when {
                cost.totalCost == null || limit == null || profile.budget.people != 1 || profile.budget.coveredMealSlots != (0 until q.mealsPerDay).map { "slot:$it" }.toSet() -> RuleStatus.UNKNOWN
                cost.totalCost.currency != limit.currency -> RuleStatus.UNKNOWN
                cost.totalCost.amount.toBigDecimal() <= limit.amount.toBigDecimal() -> RuleStatus.PASS
                else -> RuleStatus.FAIL
            }, "Full weekly package cost; exact budget scope must be explicit. No limit/people/coverage is inferred or relaxed")
            return result
        }
        val path = mutableListOf<GeneratedMeal>()
        var solution: List<GeneratedMeal>? = null
        var lastFailures = emptyList<CandidateRuleResult>()
        fun search(index: Int): Boolean {
            if (index == 7 * q.mealsPerDay) {
                val checks = weekChecks(path, price(path))
                lastFailures = WeeklyNutrition.hardFailures(checks)
                if (lastFailures.isEmpty()) { solution = path.toList(); return true }
                return false
            }
            val day = index / q.mealsPerDay
            val slot = index % q.mealsPerDay
            val target = preview.slotKcal[slot]
            fun macroDistance(option: Pair<Recipe, Double>): Double {
                val (recipe, grams) = option
                val fraction = target / requireNotNull(preview.targetKcal)
                val macros = requireNotNull(preview.macros)
                val expected = listOf("protein_g" to macros.proteinGrams, "fat_g" to macros.fatGrams,
                    "available_carbohydrate_g" to macros.availableCarbohydrateGrams)
                return expected.sumOf { (key, daily) ->
                    val amount = WeeklyNutrition.value(recipe.nutrientsTotal, key)
                    if (amount == null) Double.MAX_VALUE / 3 else
                        abs(amount * grams / requireNotNull(recipe.yieldCookedGrams) - daily * fraction) / daily
                }
            }
            val ordered = shuffled.sortedWith(compareBy<Pair<Recipe, Double>> { (r, grams) -> abs((WeeklyNutrition.value(r.nutrientsTotal,
                "energy_kcal", NutrientUnit.KCAL) ?: Double.MAX_VALUE) * grams / requireNotNull(r.yieldCookedGrams) - target) }.thenBy { macroDistance(it) }.thenBy { option -> path.count { it.recipe == option.first.ref } })
            for (option in ordered) {
                if (nodes >= q.search.maximumNodes) { exhausted = true; return false }; nodes++
                if (path.count { it.recipe == option.first.ref } >= q.search.maximumRecipeOccurrences) continue
                if (day > 0 && path.drop((day - 1) * q.mealsPerDay).take(q.mealsPerDay).any { it.recipe == option.first.ref }) continue
                val m = meal(option, index)
                path += m
                val today = path.takeLast(slot + 1)
                val energy = WeeklyNutrition.value(WeeklyNutrition.sum(today.map { it.nutrients }), "energy_kcal", NutrientUnit.KCAL)
                val checks = if (slot == q.mealsPerDay - 1) WeeklyNutrition.check(today, recipes, data, preview, q.candidate.policy, "day:$day") else emptyList()
                val failures = WeeklyNutrition.hardFailures(checks)
                if (failures.isNotEmpty()) lastFailures = failures
                if ((energy == null || energy <= requireNotNull(preview.bounds).upperKcal) && failures.isEmpty() && search(index + 1)) return true
                path.removeAt(path.lastIndex)
                if (exhausted) return false
            }
            return false
        }
        search(0)
        val selected = solution
        if (selected == null) {
            rules += lastFailures
            rules += rule(if (exhausted) "search_budget" else "no_combination", RuleStatus.FAIL,
                if (exhausted) "Bounded deterministic search exhausted; this is not proof of infeasibility"
                else "No real portion combination satisfies the unchanged constraints")
            return finish(if (exhausted) GenerationStatus.TEMPORARY_FAILURE
                else if (lastFailures.any { it.status == RuleStatus.UNKNOWN }) GenerationStatus.INCOMPLETE_DATA else GenerationStatus.NO_FEASIBLE_PLAN)
        }
        val cost = price(selected)
        rules += selected.chunked(q.mealsPerDay).flatMapIndexed { day, meals ->
            WeeklyNutrition.check(meals, recipes, data, preview, q.candidate.policy, "day:$day") }
        rules += weekChecks(selected, cost)
        val alternatives = mutableListOf<GeneratedAlternative>()
        for ((index, original) in selected.withIndex()) {
            for (option in shuffled) {
                if (option.first.ref == original.recipe) continue
                if (nodes >= q.search.maximumNodes) { exhausted = true; break }; nodes++
                val replacement = meal(option, index)
                val changed = selected.toMutableList().also { it[index] = replacement }
                val day = index / q.mealsPerDay
                val neighbors = changed.filterIndexed { i, _ -> abs(i / q.mealsPerDay - day) == 1 }
                if (neighbors.any { it.recipe == replacement.recipe } || changed.count { it.recipe == replacement.recipe } > q.search.maximumRecipeOccurrences) continue
                if (WeeklyNutrition.hardFailures(WeeklyNutrition.check(changed.chunked(q.mealsPerDay)[day], recipes, data,
                        preview, q.candidate.policy, "alternative")).isNotEmpty()) continue
                if (WeeklyNutrition.hardFailures(weekChecks(changed, price(changed))).isNotEmpty()) continue
                alternatives += GeneratedAlternative(original.day, original.slot, replacement.recipe, replacement.portionGrams)
                break
            }
            rules += rule("alternatives:${original.day.iso}:${original.slot}",
                if (alternatives.any { it.day == original.day && it.slot == original.slot }) RuleStatus.PASS else RuleStatus.UNKNOWN,
                if (exhausted) "Alternative search budget exhausted; no alternative promised" else "Only complete revalidated replacements are offered; zero alternatives is explicit")
        }
        val daily = selected.chunked(q.mealsPerDay).map { WeeklyNutrition.sum(it.map { meal -> meal.nutrients }) }
        return finish(GenerationStatus.SUCCESS, GeneratedWeek(q, selected, daily, WeeklyNutrition.sum(daily), alternatives, cost, preview, requireNotNull(data.coverage)))
    }
}
