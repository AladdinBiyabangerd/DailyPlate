package com.aladdin.dailyplate.domain.policy

import kotlinx.serialization.Serializable

@Serializable
internal data class ResearchMacroVector(val proteinGrams: Double, val fatGrams: Double,
                                       val availableCarbohydrateGrams: Double) {
    init { require(listOf(proteinGrams, fatGrams, availableCarbohydrateGrams).all { it.isFinite() && it > 0 }) }
}

@Serializable internal enum class MaintenanceComparison { ANY, STRICTLY_BELOW, STRICTLY_ABOVE }

/** Bounds are product search tolerances, not a universal clinical maximum. */
@Serializable
internal data class ResearchMenuEnergyBounds(
    val lowerKcal: Double, val upperKcal: Double, val maintenanceKcal: Double,
    val maintenanceComparison: MaintenanceComparison,
) {
    init {
        require(listOf(lowerKcal, upperKcal, maintenanceKcal).all { it.isFinite() && it > 0 })
        require(lowerKcal <= upperKcal)
        require(maintenanceComparison != MaintenanceComparison.STRICTLY_BELOW || lowerKcal < maintenanceKcal)
        require(maintenanceComparison != MaintenanceComparison.STRICTLY_ABOVE || upperKcal > maintenanceKcal)
    }
    fun contains(kcal: Double): Boolean = kcal.isFinite() && kcal >= lowerKcal && kcal <= upperKcal &&
        when (maintenanceComparison) {
            MaintenanceComparison.ANY -> true
            MaintenanceComparison.STRICTLY_BELOW -> kcal < maintenanceKcal
            MaintenanceComparison.STRICTLY_ABOVE -> kcal > maintenanceKcal
        }
}

@Serializable internal enum class ResearchPlanningFailure {
    INPUT_BLOCKED, MACRO_INFEASIBLE, NO_FEASIBLE_PLAN, UNSUPPORTED_SLOT_COUNT,
}

/** Search specification, never evidence of a feasible menu. On failure its usable target is null;
 * energy retains screening/maintenance diagnostics with its candidate target cleared. */
@Serializable
internal data class ResearchPlanningPreviewV2(
    val energy: ResearchEnergyPreviewV2,
    val targetKcal: Double?, val macros: ResearchMacroVector?, val bounds: ResearchMenuEnergyBounds?,
    val slotKcal: List<Double>, val failure: ResearchPlanningFailure?,
    val evidenceRefs: List<String> = listOf("S5", "S27", "S28", "S3"),
) {
    init {
        require((failure == null) == (targetKcal != null && macros != null && bounds != null))
        require(failure == null || (targetKcal == null && macros == null && bounds == null && slotKcal.isEmpty()))
    }
}

/** Source-normalized menu quantities. A value without complete coverage cannot pass a hard rule. */
@Serializable
internal data class ResearchMenuQuantity(val value: Double?, val complete: Boolean = false)
@Serializable internal enum class ResearchCarbohydrateBasis { AVAILABLE_GLYCAEMIC, BY_DIFFERENCE, UNKNOWN }
@Serializable internal enum class ResearchCatalogFeasibility {
    NOT_EVALUATED, EMPTY_CATALOG, NO_REAL_COMBINATION, VERIFIED_PUBLISHED_PORTIONS,
}

/** Supplied by a future catalog adapter, not inferred from successful arithmetic. */
@Serializable
internal data class ResearchMenuObservation(
    val energyKcal: ResearchMenuQuantity, val proteinGrams: ResearchMenuQuantity,
    val fatGrams: ResearchMenuQuantity, val carbohydrateGrams: ResearchMenuQuantity,
    val fibreGrams: ResearchMenuQuantity, val carbohydrateBasis: ResearchCarbohydrateBasis,
    val catalogFeasibility: ResearchCatalogFeasibility,
)
@Serializable internal enum class ResearchMenuRule {
    ENERGY_INTERVAL, PROTEIN_PRI, FAT_RI, CARBOHYDRATE_RI, FIBRE_AI,
    PUBLISHED_PORTION_FEASIBILITY, OTHER_NUTRIENTS_AND_PROTEIN_QUALITY, WHO_TOTAL_FAT_ADVISORY,
}
@Serializable internal enum class ResearchMenuReason {
    MISSING_OR_INCOMPLETE, INVALID_QUANTITY, CARBOHYDRATE_DEFINITION_UNKNOWN,
    OUT_OF_RANGE, NO_FEASIBLE_PLAN, CATALOG_NOT_EVALUATED, COVERAGE_NOT_EVALUATED, PREVIEW_BLOCKED,
}
@Serializable
internal data class ResearchMenuRuleResult(
    val rule: ResearchMenuRule, val status: ResearchRuleStatus, val reason: ResearchMenuReason? = null,
    val advisory: Boolean = false,
)
@Serializable
internal data class ResearchMenuAssessment(val rules: List<ResearchMenuRuleResult>) {
    init {
        require(rules.map { it.rule }.toSet() == ResearchMenuRule.entries.toSet())
        require(rules.size == ResearchMenuRule.entries.size)
        require(rules.all { it.advisory == (it.rule == ResearchMenuRule.WHO_TOTAL_FAT_ADVISORY) })
    }
    // Slice 3 never certifies complete nutrition, restricted diets, or production eligibility.
    val allHardRulesPass: Boolean get() = rules.filterNot { it.advisory }.all { it.status == ResearchRuleStatus.PASS }
}
