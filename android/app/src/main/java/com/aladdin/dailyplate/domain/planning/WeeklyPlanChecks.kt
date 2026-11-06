package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.pricing.*
import java.math.BigDecimal

/** Shared exact-portion shopping and whole-week checks for generation and recalculation. */
internal object WeeklyPlanChecks {
    fun price(meals: List<GeneratedMeal>, recipes: Map<VersionRef, Recipe>, data: WeeklyPlanningData,
              profile: UserProfile, q: PlanGenerationRequest, test: Boolean): ShoppingCost {
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

    fun weekChecks(meals: List<GeneratedMeal>, cost: ShoppingCost, data: WeeklyPlanningData,
                   profile: UserProfile, q: PlanGenerationRequest): List<CandidateRuleResult> {
        fun rule(id: String, status: RuleStatus, reason: String) = CandidateRuleResult(id, q.candidate.policy,
            "week", "matches", null, null, null, status, listOf(q.search.ref.id),
            if (status == RuleStatus.UNKNOWN) listOf(id) else emptyList(), reason)
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
}
