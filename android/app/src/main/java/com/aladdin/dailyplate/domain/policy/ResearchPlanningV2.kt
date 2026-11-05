package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.CalculationSex
import com.aladdin.dailyplate.domain.Goal
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/** NP-13–23 research arithmetic. No generation, calorie titration, storage or approval. */
internal object ResearchPlanningV2 {
    fun preview(inputs: EerResearchInputsV2, slots: Int,
                contract: ResearchEnergyContractV2 = ResearchEnergyContractV2()): ResearchPlanningPreviewV2 {
        val energy = ResearchEnergyEligibilityV2.preview(inputs, contract)
        fun fail(reason: ResearchPlanningFailure) =
            ResearchPlanningPreviewV2(energy.copy(candidateTargetKcal = null), null, null, null, emptyList(), reason)
        val target = energy.candidateTargetKcal ?: return fail(ResearchPlanningFailure.INPUT_BLOCKED)
        val weights = when (slots) {
            3 -> listOf(0.30, 0.35, 0.35)
            4 -> listOf(0.25, 0.35, 0.30, 0.10)
            5 -> listOf(0.25, 0.10, 0.30, 0.10, 0.25)
            else -> return fail(ResearchPlanningFailure.UNSUPPORTED_SLOT_COUNT)
        }
        val macros = macroVector(target, requireNotNull(inputs.weightKg))
            ?: return fail(ResearchPlanningFailure.MACRO_INFEASIBLE)
        val bounds = menuBounds(requireNotNull(inputs.goal), requireNotNull(energy.maintenanceKcal),
            target, if (inputs.sex == CalculationSex.FEMALE) 1500.0 else 1800.0)
            ?: return fail(ResearchPlanningFailure.NO_FEASIBLE_PLAN)
        return ResearchPlanningPreviewV2(energy, target, macros, bounds, weights.map { it * target }, null)
    }

    /** Null means MACRO_INFEASIBLE, never a repaired split or reduced protein minimum. */
    fun macroVector(targetKcal: Double, actualWeightKg: Double): ResearchMacroVector? {
        if (!targetKcal.isFinite() || targetKcal <= 0 || !actualWeightKg.isFinite() || actualWeightKg <= 0) return null
        val protein = max(0.83 * actualWeightKg, 0.20 * targetKcal / 4)
        val fat = 0.30 * targetKcal / 9
        val carbohydrate = (targetKcal - 4 * protein - 9 * fat) / 4
        if (!listOf(protein, fat, carbohydrate).all { it.isFinite() && it > 0 }) return null
        val carbohydrateFraction = 4 * carbohydrate / targetKcal
        val sum = 4 * protein + 9 * fat + 4 * carbohydrate
        // ULP tolerance is for arithmetic conservation only, never energy/BMI/RI boundaries.
        if (carbohydrateFraction !in 0.45..0.60 || !sum.isFinite() ||
            abs(sum - targetKcal) > 8 * Math.ulp(targetKcal)) return null
        return ResearchMacroVector(protein, fat, carbohydrate)
    }

    /** Low-level interval arithmetic; preview calls this only after screening and sex floor checks. */
    fun menuBounds(goal: Goal, maintenance: Double, target: Double, floor: Double): ResearchMenuEnergyBounds? {
        if (!listOf(maintenance, target, floor).all { it.isFinite() && it > 0 }) return null
        val lower: Double
        val upper: Double
        val comparison: MaintenanceComparison
        when (goal) {
            Goal.MAINTAIN, Goal.HEALTHY -> {
                lower = max(0.95 * maintenance, floor)
                upper = 1.05 * maintenance
                comparison = MaintenanceComparison.ANY
            }
            Goal.LOSE -> {
                lower = maxOf(0.95 * target, floor, maintenance - 500)
                upper = min(1.05 * target, maintenance)
                comparison = MaintenanceComparison.STRICTLY_BELOW
            }
            Goal.GAIN -> {
                lower = maxOf(0.95 * target, floor, maintenance)
                upper = min(1.05 * target, maintenance + 300)
                comparison = MaintenanceComparison.STRICTLY_ABOVE
            }
        }
        if (!lower.isFinite() || !upper.isFinite() || lower > upper ||
            (comparison == MaintenanceComparison.STRICTLY_BELOW && lower >= maintenance) ||
            (comparison == MaintenanceComparison.STRICTLY_ABOVE && upper <= maintenance)) return null
        return ResearchMenuEnergyBounds(lower, upper, maintenance, comparison)
    }
}
