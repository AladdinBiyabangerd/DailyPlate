package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.CalculationSex
import com.aladdin.dailyplate.domain.VersionRef

/** S23/S24 category codes. These are not PAL multipliers or legacy activity mappings. */
internal enum class EerActivity(val code: String) {
    INACTIVE("inactive"), LOW_ACTIVE("low_active"), ACTIVE("active"), VERY_ACTIVE("very_active");
}

internal sealed interface EnergyActivity {
    data class LegacyFactor(val factor: Double) : EnergyActivity
    // Construct only after the future v2 profile confirmation check; never infer from a v1 code.
    data class ConfirmedEer(val category: EerActivity) : EnergyActivity
}

internal enum class EnergyFormulaFailure { UNSUPPORTED_FORMULA, ACTIVITY_MODEL_MISMATCH, INVALID_INPUT, INVALID_RESULT }

internal sealed interface EnergyFormulaResult {
    /** Direct EER has no resting estimate. This value is not a personal intake target. */
    data class Estimate(val restingKcal: Double?, val maintenanceKcal: Double) : EnergyFormulaResult
    data class Failure(val reason: EnergyFormulaFailure) : EnergyFormulaResult
}

/** Arithmetic only: no eligibility, goal, approval, persistence or production activation. */
internal object EnergyFormula {
    val MIFFLIN_V1 = VersionRef("mifflin-st-jeor", 1)
    val NASEM_2023_ADULT_V1 = VersionRef("nasem-2023-adult-eer", 1)

    fun estimate(formula: VersionRef, ageYears: Int, sex: CalculationSex,
                 heightCm: Double, weightKg: Double, activity: EnergyActivity): EnergyFormulaResult {
        fun fail(reason: EnergyFormulaFailure) = EnergyFormulaResult.Failure(reason)
        if (formula != MIFFLIN_V1 && formula != NASEM_2023_ADULT_V1)
            return fail(EnergyFormulaFailure.UNSUPPORTED_FORMULA)
        if (ageYears <= 0 || !heightCm.isFinite() || heightCm <= 0 ||
            !weightKg.isFinite() || weightKg <= 0) return fail(EnergyFormulaFailure.INVALID_INPUT)
        val resting: Double?
        val maintenance: Double
        when (formula) {
            MIFFLIN_V1 -> {
                if (activity !is EnergyActivity.LegacyFactor)
                    return fail(EnergyFormulaFailure.ACTIVITY_MODEL_MISMATCH)
                if (!activity.factor.isFinite() || activity.factor <= 0)
                    return fail(EnergyFormulaFailure.INVALID_INPUT)
                resting = 10.0 * weightKg + 6.25 * heightCm - 5.0 * ageYears +
                    when (sex) { CalculationSex.MALE -> 5.0; CalculationSex.FEMALE -> -161.0 }
                maintenance = resting * activity.factor
            }
            else -> {
                if (activity !is EnergyActivity.ConfirmedEer)
                    return fail(EnergyFormulaFailure.ACTIVITY_MODEL_MISMATCH)
                val c = coefficients(sex, activity.category)
                resting = null
                maintenance = c[0] + c[1] * ageYears + c[2] * heightCm + c[3] * weightKg
            }
        }
        if ((resting != null && (!resting.isFinite() || resting <= 0)) ||
            !maintenance.isFinite() || maintenance <= 0) return fail(EnergyFormulaFailure.INVALID_RESULT)
        return EnergyFormulaResult.Estimate(resting, maintenance)
    }

    // Health Canada adult table / NASEM 2023 Appendix G §8.3; no additional PAL factor.
    private fun coefficients(sex: CalculationSex, activity: EerActivity): DoubleArray = when (sex) {
        CalculationSex.MALE -> when (activity) {
            EerActivity.INACTIVE -> doubleArrayOf(753.07, -10.83, 6.50, 14.10)
            EerActivity.LOW_ACTIVE -> doubleArrayOf(581.47, -10.83, 8.30, 14.94)
            EerActivity.ACTIVE -> doubleArrayOf(1004.82, -10.83, 6.52, 15.91)
            EerActivity.VERY_ACTIVE -> doubleArrayOf(-517.88, -10.83, 15.61, 19.11)
        }
        CalculationSex.FEMALE -> when (activity) {
            EerActivity.INACTIVE -> doubleArrayOf(584.90, -7.01, 5.72, 11.71)
            EerActivity.LOW_ACTIVE -> doubleArrayOf(575.77, -7.01, 6.60, 12.14)
            EerActivity.ACTIVE -> doubleArrayOf(710.25, -7.01, 6.54, 12.34)
            EerActivity.VERY_ACTIVE -> doubleArrayOf(511.83, -7.01, 9.07, 12.56)
        }
    }
}
