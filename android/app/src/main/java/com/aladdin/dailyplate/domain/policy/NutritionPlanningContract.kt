package com.aladdin.dailyplate.domain.policy

import kotlinx.serialization.Serializable

@Serializable
data class NutritionMacroVector(val proteinGrams: Double, val fatGrams: Double,
                                       val availableCarbohydrateGrams: Double) {
    init { require(listOf(proteinGrams, fatGrams, availableCarbohydrateGrams).all { it.isFinite() && it > 0 }) }
}

@Serializable enum class MaintenanceComparison { ANY, STRICTLY_BELOW, STRICTLY_ABOVE }

/** Bounds are product search tolerances, not a universal clinical maximum. */
@Serializable
data class NutritionMenuEnergyBounds(
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

@Serializable enum class NutritionPlanningFailure {
    INPUT_BLOCKED, MACRO_INFEASIBLE, NO_FEASIBLE_PLAN, UNSUPPORTED_SLOT_COUNT,
}

/** Search specification, never evidence of a feasible menu. On failure its usable target is null;
 * energy retains screening/maintenance diagnostics with its candidate target cleared. */
@Serializable
data class NutritionPlanningPreview(
    val energy: NutritionEnergyPreview,
    val targetKcal: Double?, val macros: NutritionMacroVector?, val bounds: NutritionMenuEnergyBounds?,
    val slotKcal: List<Double>, val failure: NutritionPlanningFailure?,
    val evidenceRefs: List<String> = listOf("S5", "S27", "S28", "S3"),
) {
    init {
        require((failure == null) == (targetKcal != null && macros != null && bounds != null))
        require(failure == null || (targetKcal == null && macros == null && bounds == null && slotKcal.isEmpty()))
    }
}

/** Source-normalized menu quantities. A value without complete coverage cannot pass a hard rule. */
@Serializable
data class NutritionMenuQuantity(val value: Double?, val complete: Boolean = false)
@Serializable enum class NutritionCarbohydrateBasis { AVAILABLE_GLYCAEMIC, BY_DIFFERENCE, UNKNOWN }
@Serializable enum class NutritionCatalogFeasibility {
    NOT_EVALUATED, EMPTY_CATALOG, NO_REAL_COMBINATION, VERIFIED_PUBLISHED_PORTIONS,
}

/** Supplied by a future catalog adapter, not inferred from successful arithmetic. */
@Serializable
data class NutritionMenuObservation(
    val energyKcal: NutritionMenuQuantity, val proteinGrams: NutritionMenuQuantity,
    val fatGrams: NutritionMenuQuantity, val carbohydrateGrams: NutritionMenuQuantity,
    val fibreGrams: NutritionMenuQuantity, val carbohydrateBasis: NutritionCarbohydrateBasis,
    val catalogFeasibility: NutritionCatalogFeasibility,
)
@Serializable enum class NutritionMenuRule {
    ENERGY_INTERVAL, PROTEIN_PRI, FAT_RI, CARBOHYDRATE_RI, FIBRE_AI,
    PUBLISHED_PORTION_FEASIBILITY, OTHER_NUTRIENTS_AND_PROTEIN_QUALITY, WHO_TOTAL_FAT_ADVISORY,
}
@Serializable enum class NutritionMenuReason {
    MISSING_OR_INCOMPLETE, INVALID_QUANTITY, CARBOHYDRATE_DEFINITION_UNKNOWN,
    OUT_OF_RANGE, NO_FEASIBLE_PLAN, CATALOG_NOT_EVALUATED, COVERAGE_NOT_EVALUATED, PREVIEW_BLOCKED,
}
@Serializable
data class NutritionMenuRuleResult(
    val rule: NutritionMenuRule, val status: NutritionRuleStatus, val reason: NutritionMenuReason? = null,
    val advisory: Boolean = false,
)
@Serializable
data class NutritionMenuAssessment(val rules: List<NutritionMenuRuleResult>) {
    init {
        require(rules.map { it.rule }.toSet() == NutritionMenuRule.entries.toSet())
        require(rules.size == NutritionMenuRule.entries.size)
        require(rules.all { it.advisory == (it.rule == NutritionMenuRule.WHO_TOTAL_FAT_ADVISORY) })
    }
    // Slice 3 never certifies complete nutrition, restricted diets, or production eligibility.
    val allHardRulesPass: Boolean get() = rules.filterNot { it.advisory }.all { it.status == NutritionRuleStatus.PASS }
}
