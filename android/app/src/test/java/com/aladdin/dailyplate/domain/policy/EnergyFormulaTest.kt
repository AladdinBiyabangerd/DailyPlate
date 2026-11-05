package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.CalculationSex
import com.aladdin.dailyplate.domain.VersionRef
import org.junit.Assert.*
import org.junit.Test

class EnergyFormulaTest {
    private fun eer(sex: CalculationSex, activity: EerActivity, age: Int = 30,
                    height: Double = 175.0, weight: Double = 70.0) = EnergyFormula.estimate(
        EnergyFormula.NASEM_2023_ADULT_V1, age, sex, height, weight, EnergyActivity.ConfirmedEer(activity))

    @Test fun allEightRowsMatchIndependentResearchExamplesWithoutPalOrRestingEstimate() {
        val rows = mapOf(CalculationSex.MALE to listOf(2552.67, 2754.87, 2934.62, 3226.67),
            CalculationSex.FEMALE to listOf(2195.30, 2370.27, 2508.25, 2767.98))
        rows.forEach { (sex, expected) -> EerActivity.entries.forEachIndexed { index, activity ->
            val result = eer(sex, activity) as EnergyFormulaResult.Estimate
            assertNull(result.restingKcal)
            assertEquals(expected[index], result.maintenanceKcal, 1e-9)
        } }
        assertEquals(listOf("inactive", "low_active", "active", "very_active"), EerActivity.entries.map { it.code })
    }

    @Test fun ageHeightAndWeightReachTheirOwnEquationTerms() {
        CalculationSex.entries.forEach { sex -> EerActivity.entries.forEach { activity ->
            val base = (eer(sex, activity) as EnergyFormulaResult.Estimate).maintenanceKcal
            val older = (eer(sex, activity, age = 31) as EnergyFormulaResult.Estimate).maintenanceKcal
            assertEquals(if (sex == CalculationSex.MALE) -10.83 else -7.01, older - base, 1e-9)
        } }
        assertEquals(2764.17, (eer(CalculationSex.MALE, EerActivity.INACTIVE,
            weight = 85.0) as EnergyFormulaResult.Estimate).maintenanceKcal, 1e-9)
        assertEquals(1609.71, (eer(CalculationSex.FEMALE, EerActivity.INACTIVE,
            age = 64, height = 145.0, weight = 55.0) as EnergyFormulaResult.Estimate).maintenanceKcal, 1e-9)
    }

    @Test fun legacyMifflinKeepsExactArithmeticAndSeparateRestingEstimate() {
        val result = EnergyFormula.estimate(EnergyFormula.MIFFLIN_V1, 30, CalculationSex.MALE,
            180.0, 80.0, EnergyActivity.LegacyFactor(1.5)) as EnergyFormulaResult.Estimate
        assertEquals(1780.0, result.restingKcal!!, 0.0)
        assertEquals(2670.0, result.maintenanceKcal, 0.0)
    }

    @Test fun modelMismatchNeverSilentlyMapsActivityOrMultipliesEer() {
        listOf(EnergyFormula.NASEM_2023_ADULT_V1 to EnergyActivity.LegacyFactor(1.5),
            EnergyFormula.MIFFLIN_V1 to EnergyActivity.ConfirmedEer(EerActivity.ACTIVE)).forEach { (formula, activity) ->
            assertEquals(EnergyFormulaResult.Failure(EnergyFormulaFailure.ACTIVITY_MODEL_MISMATCH),
                EnergyFormula.estimate(formula, 30, CalculationSex.MALE, 175.0, 70.0, activity))
        }
    }

    @Test fun invalidInputsOverflowAndUnsupportedVersionsReturnExplicitFailures() {
        listOf(Double.NaN, Double.POSITIVE_INFINITY, 0.0, -1.0).forEach { bad ->
            assertEquals(EnergyFormulaResult.Failure(EnergyFormulaFailure.INVALID_INPUT),
                eer(CalculationSex.MALE, EerActivity.INACTIVE, height = bad))
            assertEquals(EnergyFormulaResult.Failure(EnergyFormulaFailure.INVALID_INPUT),
                eer(CalculationSex.MALE, EerActivity.INACTIVE, weight = bad))
        }
        assertEquals(EnergyFormulaResult.Failure(EnergyFormulaFailure.INVALID_INPUT),
            eer(CalculationSex.MALE, EerActivity.INACTIVE, age = 0))
        assertEquals(EnergyFormulaResult.Failure(EnergyFormulaFailure.INVALID_RESULT),
            eer(CalculationSex.MALE, EerActivity.INACTIVE, weight = Double.MAX_VALUE))
        listOf(VersionRef("unknown", 1), VersionRef("nasem-2023-adult-eer", 2)).forEach {
            assertEquals(EnergyFormulaResult.Failure(EnergyFormulaFailure.UNSUPPORTED_FORMULA),
                EnergyFormula.estimate(it, 30, CalculationSex.MALE, 175.0, 70.0,
                    EnergyActivity.ConfirmedEer(EerActivity.INACTIVE)))
        }
    }

    @Test fun nasemCannotExecuteThroughTheV1AssessmentContract() {
        val result = NutritionPolicyEngine.assessTest(eligibleProfile(),
            policyFixture().copy(formula = EnergyFormula.NASEM_2023_ADULT_V1), policyTime)
        assertNull(result.calculation)
        assertNull(result.dailyKcal)
        assertTrue(result.issues.any { it.reason == PolicyReason.FORMULA_UNSUPPORTED })
    }
}
