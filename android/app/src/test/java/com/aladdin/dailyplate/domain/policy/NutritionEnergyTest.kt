package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.CalculationSex
import com.aladdin.dailyplate.domain.Goal
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class NutritionEnergyTest {
    private fun input() = NutritionInputs(30, CalculationSex.MALE, 200.0, 100.0,
        true, EerActivity.INACTIVE, true, Goal.MAINTAIN,
        NutritionHealthQuestion.entries.associateWith { false })
    private fun preview(i: NutritionInputs) = NutritionEnergyEligibility.preview(i)
    private fun blocked(i: NutritionInputs, reason: NutritionEnergyReason) {
        val p = preview(i)
        assertNull(p.candidateTargetKcal)
        assertTrue(p.rules.any { it.reason == reason })
    }

    @Test fun ageModelAndProductScopesAreDistinct() {
        for (age in listOf(19, 64)) assertNotNull(preview(input().copy(ageYears = age)).candidateTargetKcal)
        blocked(input().copy(ageYears = 18), NutritionEnergyReason.MODEL_AGE_OUT_OF_SCOPE)
        val older = preview(input().copy(ageYears = 65))
        assertNull(older.candidateTargetKcal)
        assertEquals(NutritionRuleStatus.PASS, older.rules.single { it.rule == NutritionEnergyRule.MODEL_AGE_SCOPE }.status)
        assertTrue(older.rules.any { it.reason == NutritionEnergyReason.PRODUCT_AGE_OUT_OF_SCOPE })
    }

    @Test fun bmiAndGoalBoundariesAreNotRoundedOrChanged() {
        // Height 2m makes exact boundary fixtures independent of decimal-height rounding.
        assertNotNull(preview(input().copy(weightKg = 74.0, goal = Goal.GAIN)).candidateTargetKcal)
        assertNotNull(preview(input().copy(weightKg = 100.0, goal = Goal.LOSE)).candidateTargetKcal)
        blocked(input().copy(weightKg = 73.999), NutritionEnergyReason.BMI_OUT_OF_SCOPE)
        blocked(input().copy(weightKg = 120.0), NutritionEnergyReason.BMI_OUT_OF_SCOPE)
        blocked(input().copy(weightKg = 99.999, goal = Goal.LOSE), NutritionEnergyReason.GOAL_OUT_OF_SCOPE)
        blocked(input().copy(goal = Goal.GAIN), NutritionEnergyReason.GOAL_OUT_OF_SCOPE)
        for (goal in listOf(Goal.MAINTAIN, Goal.HEALTHY)) {
            val p = preview(input().copy(goal = goal))
            assertEquals(p.maintenanceKcal, p.candidateTargetKcal)
        }
    }

    @Test fun everyHealthAnswerMustBeExplicitAndAnyRiskBlocks() {
        blocked(input().copy(healthAnswers = emptyMap()), NutritionEnergyReason.HEALTH_ANSWER_MISSING)
        for (question in NutritionHealthQuestion.entries) {
            blocked(input().copy(healthAnswers = input().healthAnswers - question), NutritionEnergyReason.HEALTH_ANSWER_MISSING)
            blocked(input().copy(healthAnswers = input().healthAnswers + (question to true)), NutritionEnergyReason.HEALTH_EXCLUSION)
        }
    }

    @Test fun missingUnconfirmedAndInvalidInputsCannotProduceTarget() {
        blocked(input().copy(sex = null), NutritionEnergyReason.SEX_MISSING)
        blocked(input().copy(ageYears = null), NutritionEnergyReason.INPUT_MISSING)
        blocked(input().copy(measurementsConfirmed = false), NutritionEnergyReason.CONFIRMATION_REQUIRED)
        blocked(input().copy(activity = null), NutritionEnergyReason.ACTIVITY_UNCERTAIN)
        // Even the overlapping legacy 'active' label cannot bypass explicit confirmation.
        blocked(input().copy(activity = EerActivity.ACTIVE, activityConfirmed = false), NutritionEnergyReason.ACTIVITY_UNCERTAIN)
        for (value in listOf(0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY)) {
            blocked(input().copy(heightCm = value), NutritionEnergyReason.INVALID_INPUT)
            blocked(input().copy(weightKg = value), NutritionEnergyReason.INVALID_INPUT)
        }
        assertNull(preview(input().copy(heightCm = Double.MIN_VALUE, weightKg = Double.MIN_VALUE)).candidateTargetKcal)
    }

    @Test fun floorFailureNeverClampsAndGoalAdjustmentsMatchCandidate() {
        val female = input().copy(ageYears = 64, sex = CalculationSex.FEMALE,
            heightCm = 145.0, weightKg = 55.0, goal = Goal.LOSE)
        blocked(female, NutritionEnergyReason.ENERGY_BELOW_PRODUCT_FLOOR)
        assertEquals(1609.71, preview(female).maintenanceKcal!!, 0.000001)
        assertEquals(1609.71, preview(female.copy(goal = Goal.MAINTAIN)).candidateTargetKcal!!, 0.000001)
        val lose = preview(input().copy(heightCm = 175.0, weightKg = 85.0, goal = Goal.LOSE))
        assertEquals(2264.17, lose.candidateTargetKcal!!, 0.000001)
        val gain = preview(input().copy(heightCm = 175.0, weightKg = 70.0, goal = Goal.GAIN))
        assertEquals(2852.67, gain.candidateTargetKcal!!, 0.000001)
        blocked(input().copy(ageYears = 64, heightCm = 140.0, weightKg = 50.0, goal = Goal.LOSE),
            NutritionEnergyReason.ENERGY_BELOW_PRODUCT_FLOOR)
    }

    @Test fun researchContractAndPreviewRoundTripWithoutProductionAuthority() {
        val p = preview(input())
        assertEquals(p, Json.decodeFromString<NutritionEnergyPreview>(Json.encodeToString(p)))
        assertFalse(p.contract.productionEnabled)
        assertNull(p.contract.approvedBy)
        for (change in listOf<() -> Unit>(
            { NutritionEnergyContract(contractVersion = 1) },
            { NutritionEnergyContract(productionEnabled = true) },
            { NutritionEnergyContract(approvedBy = "reviewer") },
            { NutritionEnergyContract(provenance = NutritionEnergyProvenance(formulaEvidence = emptyList())) },
        )) {
            try { change(); fail("Invalid contract accepted") } catch (_: IllegalArgumentException) { }
        }
    }
}
