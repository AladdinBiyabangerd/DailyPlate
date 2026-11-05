package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*

/** Converts a stored successful week; one serving denotes its generated personal portion. */
internal object GeneratedMealPlan {
    fun create(userId: String, ref: VersionRef, profile: UserProfile, result: PlanGenerationResult): MealPlan {
        require(result.status == GenerationStatus.SUCCESS)
        val week = requireNotNull(result.plan)
        fun itemId(day: CalendarDate, slot: Int) = "${day.iso}:$slot"
        val energyInputs = week.nutritionPreview.energy.inputs
        val measuredInputs = buildMap {
            energyInputs.ageYears?.let { put("ageYears", it.toDouble()) }
            energyInputs.heightCm?.let { put("heightCm", it) }
            energyInputs.weightKg?.let { put("weightKg", it) }
        }
        return MealPlan(ref, userId, profile.ref, week.request.weekStart,
            week.request.timezone, week.request.candidate.policy,
            EnergyEstimate(week.nutritionPreview.energy.contract.formula, week.request.candidate.policy,
                profile.ref, measuredInputs, week.request.candidate.at, week.targetKcal),
            // One serving here is the generated personal portion, not the entire cooked batch.
            week.meals.map { meal -> MealPlanItem(itemId(meal.day, meal.slot), meal.day, meal.slot.toString(),
                meal.recipe, null, meal.portionGrams, 1.0, 1.0, meal.nutrients,
                PlanItemStatus.PLANNED, emptySet()) },
            week.weeklyNutrients, result.ruleResults.filter { it.status != RuleStatus.NOT_APPLICABLE }.map { rule -> ConstraintResult(rule.ruleId,
                when (rule.status) {
                    RuleStatus.PASS -> ConstraintOutcome.PASS
                    RuleStatus.NOT_APPLICABLE -> error("Not applicable rules are not constraints")
                    RuleStatus.FAIL -> ConstraintOutcome.FAIL
                    RuleStatus.UNKNOWN -> ConstraintOutcome.UNKNOWN
                }, rule.reason) },
            week.alternatives.map { MealAlternative(itemId(it.day, it.slot), it.recipe, "generated alternative") },
            week.shoppingCost.lines.map { ShoppingItem(it.product.foodId, it.product.preparationState,
                it.requiredEdibleGrams.toDouble(), "g", 0.0, 0.0, ref.version) }, SyncStatus.LOCAL)
    }
}
