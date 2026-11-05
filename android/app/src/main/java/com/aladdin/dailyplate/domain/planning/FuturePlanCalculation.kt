package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.EditorialContent
import com.aladdin.dailyplate.domain.pricing.ShoppingCost
import com.aladdin.dailyplate.domain.profile.*
import java.time.LocalDate
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.round

@Serializable
data class FuturePlanCalculation(
    val status: GenerationStatus, val preview: FuturePlanPreview?,
    val rules: List<CandidateRuleResult>, val shoppingDelta: List<ShoppingDelta>,
    val shoppingCost: ShoppingCost?,
)

/** Revalidates the actual merged candidate, never transfers a fresh week's SUCCESS to it.
 * A rejected merge is not proof that all possible future menus are infeasible. A new generation
 * request/seed can propose another candidate. Planned totals are not actual diary forecasts. */
internal object FuturePlanCalculator {
    fun calculate(before: MealPlan, change: FuturePlanRecalculation, profile: UserProfile,
                  generated: PlanGenerationResult, data: WeeklyPlanningData,
                  effectiveFrom: CalendarDate, policy: NutritionPolicy?): FuturePlanCalculation {
        val week = generated.plan
        if (generated.status != GenerationStatus.SUCCESS || week == null)
            return FuturePlanCalculation(generated.status, null, generated.ruleResults, emptyList(), null)
        val q = week.request
        require(before.userProfile == change.fromProfile && profile.ref == change.toProfile)
        require(q.candidate.profile == profile.ref && q.weekStart == before.week && q.timezone == before.timezone)
        val end = LocalDate.parse(before.week.iso).plusDays(6).toString()
        require(effectiveFrom.iso in before.week.iso..end) { "Effective date must be within this plan week" }
        val rules = mutableListOf<CandidateRuleResult>()
        fun issue(id: String, reason: String, status: RuleStatus = RuleStatus.UNKNOWN) {
            rules += CandidateRuleResult(id, q.candidate.policy, "recalculation", "matches", null, null, null,
                status, emptyList(), if (status == RuleStatus.UNKNOWN) listOf(id) else emptyList(), reason)
        }
        val revision = before.ref.copy(version = Math.addExact(before.ref.version, 1))
        val fresh = GeneratedMealPlan.create(before.userId, revision, profile, generated)
        fun protected(item: MealPlanItem) = item.date.iso < effectiveFrom.iso || item.consumedLogIds.isNotEmpty() ||
            item.status != PlanItemStatus.PLANNED || item.remainingPlannedServings != item.plannedServings
        val frozen = before.items.filter(::protected)
        val occupied = frozen.map { it.date to it.mealSlot }.toSet()
        val oldSlots = before.items.associateBy { it.date to it.mealSlot }
        require(oldSlots.size == before.items.size) { "Plan has ambiguous meal slots" }
        val replacements = fresh.items.filter { it.date.iso >= effectiveFrom.iso && (it.date to it.mealSlot) !in occupied }
            .map { item -> item.copy(id = oldSlots[item.date to item.mealSlot]?.id ?: item.id) }
        val items = (frozen + replacements).sortedWith(compareBy({ it.date.iso }, { it.mealSlot }, { it.id }))
        val recipes = data.catalog.recipes.mapNotNull { (it.content as? EditorialContent.RecipeContent)?.recipe }.associateBy { it.ref }
        // Historical snapshots stay intact even when exact-version evidence is unavailable.
        // Such a merge cannot acquire a new suitability/price claim.
        if (items.any { it.customization != null || it.portionGrams == null || recipes[it.recipe]?.yieldCookedGrams == null ||
                data.evidence[it.recipe] == null || recipes.getValue(it.recipe).ingredients.any { ingredient -> ingredient.food !in data.catalog.ingredients } }) {
            issue("merged_evidence", "Exact recipe, yield, ingredient or customization evidence missing for preserved items")
            return FuturePlanCalculation(GenerationStatus.INCOMPLETE_DATA, null, rules, emptyList(), null)
        }
        val candidates = if (generated.testOnly) RecipeCandidateFilter.filterPlanningTest(q.candidate, profile, policy, data.catalog)
            else RecipeCandidateFilter.filter(q.candidate, profile, policy, data.catalog)
        if (candidates.status != CandidateStatus.SUCCESS)
            return FuturePlanCalculation(GenerationStatus.valueOf(candidates.status.name), null, candidates.ruleResults, emptyList(), null)
        val allowed = candidates.candidates.associateBy { it.recipe }
        items.filter { it.date.iso >= effectiveFrom.iso && it.remainingPlannedServings > 0 &&
            it.status in setOf(PlanItemStatus.PLANNED, PlanItemStatus.PARTIALLY_CONSUMED) }.forEach { item ->
            val range = allowed[item.recipe]?.portion
            val grams = requireNotNull(item.portionGrams)
            val steps = range?.let { (grams - it.minGrams) / it.stepGrams }
            if (range == null || grams !in range.minGrams..range.maxGrams || abs(requireNotNull(steps) - round(steps)) > 1e-8)
                issue("preserved_safety:${item.id}", "Preserved remaining portion does not pass the current hard filters/portion grid", RuleStatus.FAIL)
        }
        val meals = items.map { item -> GeneratedMeal(item.date, item.mealSlot.toIntOrNull() ?: -1, item.recipe,
            requireNotNull(item.portionGrams), item.nutrientSnapshot, "Merged planned snapshot") }
        for ((date, day) in meals.groupBy { it.day }.toSortedMap(compareBy { it.iso })) {
            if (date.iso >= effectiveFrom.iso) rules += WeeklyNutrition.check(day, recipes, data, week.nutritionPreview,
                q.candidate.policy, "merged-day:${date.iso}")
        }
        val cost = WeeklyPlanChecks.price(meals, recipes, data, profile, q, generated.testOnly)
        rules += WeeklyPlanChecks.weekChecks(meals, cost, data, profile, q)
        if (meals.groupingBy { it.recipe }.eachCount().values.any { it > q.search.maximumRecipeOccurrences })
            issue("merged_occurrences", "Merged week exceeds the versioned recipe repetition limit", RuleStatus.FAIL)
        if (meals.any { a -> meals.any { b -> a.recipe == b.recipe &&
                abs(LocalDate.parse(a.day.iso).toEpochDay() - LocalDate.parse(b.day.iso).toEpochDay()) == 1L } })
            issue("merged_adjacent_days", "Merged week repeats a recipe on adjacent days", RuleStatus.FAIL)
        val failures = WeeklyNutrition.hardFailures(rules)
        if (failures.isNotEmpty()) return FuturePlanCalculation(
            if (failures.any { it.status == RuleStatus.UNKNOWN }) GenerationStatus.INCOMPLETE_DATA else GenerationStatus.NO_FEASIBLE_PLAN,
            null, rules, emptyList(), cost)
        require(before.shopping.all { it.unit == "g" }) { "Shopping conversion requires documented gram units" }
        fun key(item: ShoppingItem) = item.foodId to item.preparationState
        val oldShopping = before.shopping.associateBy(::key)
        require(oldShopping.size == before.shopping.size)
        val needed = cost.lines.associate { (it.product.foodId to it.product.preparationState) to it.requiredEdibleGrams.toDouble() }
        val shopping = (oldShopping.keys + needed.keys).sortedWith(compareBy({ it.first }, { it.second })).mapNotNull { key ->
            val old = oldShopping[key]
            val quantity = needed[key] ?: 0.0
            if (quantity == 0.0 && (old?.homeQuantity ?: 0.0) == 0.0 && (old?.purchasedQuantity ?: 0.0) == 0.0) null
            else ShoppingItem(key.first, key.second, quantity, "g", old?.homeQuantity ?: 0.0,
                old?.purchasedQuantity ?: 0.0, revision.version)
        }
        val delta = (oldShopping.keys + needed.keys).sortedWith(compareBy({ it.first }, { it.second })).mapNotNull { key ->
            val difference = (needed[key] ?: 0.0) - (oldShopping[key]?.quantity ?: 0.0)
            if (difference == 0.0) null else ShoppingDelta(key.first, key.second, difference, "g")
        }
        issue("merged_alternatives", "Alternatives from the standalone generated week are not validated for this merge; none are offered")
        val after = fresh.copy(items = items, nutrientSnapshot = WeeklyNutrition.sum(items.map { it.nutrientSnapshot }),
            shopping = shopping, alternatives = emptyList(), constraintResults = rules.map { rule ->
                ConstraintResult("${rule.scope}:${rule.ruleId}", when (rule.status) {
                    RuleStatus.PASS -> ConstraintOutcome.PASS
                    RuleStatus.FAIL -> ConstraintOutcome.FAIL
                    else -> ConstraintOutcome.UNKNOWN
                }, rule.reason)
            })
        val preview = FuturePlanPreview(change, before, after, effectiveFrom,
            "${change.reason}. Future untouched slots recalculated; past, consumed and partial items retained. " +
                "Daily target ${before.energyEstimate?.dailyKcal} → ${after.energyEstimate?.dailyKcal}. " +
                "Merged planned totals rechecked; these are not actual intake forecasts. Alternatives require a new validation.")
        return FuturePlanCalculation(GenerationStatus.SUCCESS, preview, rules, delta, cost)
    }
}
