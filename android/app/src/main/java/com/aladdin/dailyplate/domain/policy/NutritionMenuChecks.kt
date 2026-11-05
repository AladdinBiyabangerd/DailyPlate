package com.aladdin.dailyplate.domain.policy

/** Checks caller-supplied day totals. Does not derive available carbohydrate from by-difference. */
object NutritionMenuChecks {
    fun assess(preview: NutritionPlanningPreview, menu: NutritionMenuObservation): NutritionMenuAssessment {
        val results = mutableListOf<NutritionMenuRuleResult>()
        fun result(rule: NutritionMenuRule, status: NutritionRuleStatus, reason: NutritionMenuReason? = null,
                   advisory: Boolean = false) {
            results += NutritionMenuRuleResult(rule, status, reason, advisory)
        }
        fun check(rule: NutritionMenuRule, quantities: List<NutritionMenuQuantity>, advisory: Boolean = false,
                  predicate: () -> Boolean) {
            when {
                preview.failure != null -> result(rule, NutritionRuleStatus.UNKNOWN, NutritionMenuReason.PREVIEW_BLOCKED, advisory)
                quantities.any { !it.complete || it.value == null } ->
                    result(rule, NutritionRuleStatus.UNKNOWN, NutritionMenuReason.MISSING_OR_INCOMPLETE, advisory)
                quantities.any { !requireNotNull(it.value).isFinite() || requireNotNull(it.value) < 0 } ->
                    result(rule, NutritionRuleStatus.FAIL, NutritionMenuReason.INVALID_QUANTITY, advisory)
                predicate() -> result(rule, NutritionRuleStatus.PASS, advisory = advisory)
                else -> result(rule, NutritionRuleStatus.FAIL, NutritionMenuReason.OUT_OF_RANGE, advisory)
            }
        }
        val energy = menu.energyKcal
        check(NutritionMenuRule.ENERGY_INTERVAL, listOf(energy)) {
            requireNotNull(preview.bounds).contains(requireNotNull(energy.value))
        }
        check(NutritionMenuRule.PROTEIN_PRI, listOf(menu.proteinGrams)) {
            requireNotNull(menu.proteinGrams.value) >= 0.83 * requireNotNull(preview.energy.inputs.weightKg)
        }
        // Source energy remains authoritative: never replace it by the 4/9/4 sum.
        fun fatFraction() = requireNotNull(menu.fatGrams.value) * 9 / requireNotNull(energy.value)
        check(NutritionMenuRule.FAT_RI, listOf(menu.fatGrams, energy)) {
            requireNotNull(energy.value) > 0 && fatFraction() in 0.20..0.35
        }
        if (menu.carbohydrateBasis != NutritionCarbohydrateBasis.AVAILABLE_GLYCAEMIC) {
            result(NutritionMenuRule.CARBOHYDRATE_RI, NutritionRuleStatus.UNKNOWN,
                NutritionMenuReason.CARBOHYDRATE_DEFINITION_UNKNOWN)
        } else check(NutritionMenuRule.CARBOHYDRATE_RI, listOf(menu.carbohydrateGrams, energy)) {
            requireNotNull(energy.value) > 0 &&
                requireNotNull(menu.carbohydrateGrams.value) * 4 / requireNotNull(energy.value) in 0.45..0.60
        }
        check(NutritionMenuRule.FIBRE_AI, listOf(menu.fibreGrams)) { requireNotNull(menu.fibreGrams.value) >= 25 }
        when (menu.catalogFeasibility) {
            NutritionCatalogFeasibility.NOT_EVALUATED -> result(NutritionMenuRule.PUBLISHED_PORTION_FEASIBILITY,
                NutritionRuleStatus.UNKNOWN, NutritionMenuReason.CATALOG_NOT_EVALUATED)
            NutritionCatalogFeasibility.EMPTY_CATALOG, NutritionCatalogFeasibility.NO_REAL_COMBINATION ->
                result(NutritionMenuRule.PUBLISHED_PORTION_FEASIBILITY, NutritionRuleStatus.FAIL, NutritionMenuReason.NO_FEASIBLE_PLAN)
            NutritionCatalogFeasibility.VERIFIED_PUBLISHED_PORTIONS -> result(NutritionMenuRule.PUBLISHED_PORTION_FEASIBILITY,
                NutritionRuleStatus.PASS)
        }
        // B12, protein quality, other WHO limits and restricted-diet coverage require future evidence.
        // Passing this subset never claims an automatically balanced vegan/vegetarian or other diet.
        result(NutritionMenuRule.OTHER_NUTRIENTS_AND_PROTEIN_QUALITY, NutritionRuleStatus.UNKNOWN,
            NutritionMenuReason.COVERAGE_NOT_EVALUATED)
        check(NutritionMenuRule.WHO_TOTAL_FAT_ADVISORY, listOf(menu.fatGrams, energy), advisory = true) {
            requireNotNull(energy.value) > 0 && fatFraction() <= 0.30
        }
        return NutritionMenuAssessment(results)
    }
}
