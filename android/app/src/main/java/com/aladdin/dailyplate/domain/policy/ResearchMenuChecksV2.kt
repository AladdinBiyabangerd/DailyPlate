package com.aladdin.dailyplate.domain.policy

/** Checks caller-supplied day totals. Does not derive available carbohydrate from by-difference. */
internal object ResearchMenuChecksV2 {
    fun assess(preview: ResearchPlanningPreviewV2, menu: ResearchMenuObservation): ResearchMenuAssessment {
        val results = mutableListOf<ResearchMenuRuleResult>()
        fun result(rule: ResearchMenuRule, status: ResearchRuleStatus, reason: ResearchMenuReason? = null,
                   advisory: Boolean = false) {
            results += ResearchMenuRuleResult(rule, status, reason, advisory)
        }
        fun check(rule: ResearchMenuRule, quantities: List<ResearchMenuQuantity>, advisory: Boolean = false,
                  predicate: () -> Boolean) {
            when {
                preview.failure != null -> result(rule, ResearchRuleStatus.UNKNOWN, ResearchMenuReason.PREVIEW_BLOCKED, advisory)
                quantities.any { !it.complete || it.value == null } ->
                    result(rule, ResearchRuleStatus.UNKNOWN, ResearchMenuReason.MISSING_OR_INCOMPLETE, advisory)
                quantities.any { !requireNotNull(it.value).isFinite() || requireNotNull(it.value) < 0 } ->
                    result(rule, ResearchRuleStatus.FAIL, ResearchMenuReason.INVALID_QUANTITY, advisory)
                predicate() -> result(rule, ResearchRuleStatus.PASS, advisory = advisory)
                else -> result(rule, ResearchRuleStatus.FAIL, ResearchMenuReason.OUT_OF_RANGE, advisory)
            }
        }
        val energy = menu.energyKcal
        check(ResearchMenuRule.ENERGY_INTERVAL, listOf(energy)) {
            requireNotNull(preview.bounds).contains(requireNotNull(energy.value))
        }
        check(ResearchMenuRule.PROTEIN_PRI, listOf(menu.proteinGrams)) {
            requireNotNull(menu.proteinGrams.value) >= 0.83 * requireNotNull(preview.energy.inputs.weightKg)
        }
        // Source energy remains authoritative: never replace it by the 4/9/4 sum.
        fun fatFraction() = requireNotNull(menu.fatGrams.value) * 9 / requireNotNull(energy.value)
        check(ResearchMenuRule.FAT_RI, listOf(menu.fatGrams, energy)) {
            requireNotNull(energy.value) > 0 && fatFraction() in 0.20..0.35
        }
        if (menu.carbohydrateBasis != ResearchCarbohydrateBasis.AVAILABLE_GLYCAEMIC) {
            result(ResearchMenuRule.CARBOHYDRATE_RI, ResearchRuleStatus.UNKNOWN,
                ResearchMenuReason.CARBOHYDRATE_DEFINITION_UNKNOWN)
        } else check(ResearchMenuRule.CARBOHYDRATE_RI, listOf(menu.carbohydrateGrams, energy)) {
            requireNotNull(energy.value) > 0 &&
                requireNotNull(menu.carbohydrateGrams.value) * 4 / requireNotNull(energy.value) in 0.45..0.60
        }
        check(ResearchMenuRule.FIBRE_AI, listOf(menu.fibreGrams)) { requireNotNull(menu.fibreGrams.value) >= 25 }
        when (menu.catalogFeasibility) {
            ResearchCatalogFeasibility.NOT_EVALUATED -> result(ResearchMenuRule.PUBLISHED_PORTION_FEASIBILITY,
                ResearchRuleStatus.UNKNOWN, ResearchMenuReason.CATALOG_NOT_EVALUATED)
            ResearchCatalogFeasibility.EMPTY_CATALOG, ResearchCatalogFeasibility.NO_REAL_COMBINATION ->
                result(ResearchMenuRule.PUBLISHED_PORTION_FEASIBILITY, ResearchRuleStatus.FAIL, ResearchMenuReason.NO_FEASIBLE_PLAN)
            ResearchCatalogFeasibility.VERIFIED_PUBLISHED_PORTIONS -> result(ResearchMenuRule.PUBLISHED_PORTION_FEASIBILITY,
                ResearchRuleStatus.PASS)
        }
        // B12, protein quality, other WHO limits and restricted-diet coverage require future evidence.
        // Passing this subset never claims an automatically balanced vegan/vegetarian or other diet.
        result(ResearchMenuRule.OTHER_NUTRIENTS_AND_PROTEIN_QUALITY, ResearchRuleStatus.UNKNOWN,
            ResearchMenuReason.COVERAGE_NOT_EVALUATED)
        check(ResearchMenuRule.WHO_TOTAL_FAT_ADVISORY, listOf(menu.fatGrams, energy), advisory = true) {
            requireNotNull(energy.value) > 0 && fatFraction() <= 0.30
        }
        return ResearchMenuAssessment(results)
    }
}
