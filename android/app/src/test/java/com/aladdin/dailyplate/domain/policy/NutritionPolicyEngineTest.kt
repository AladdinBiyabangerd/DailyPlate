package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class NutritionPolicyEngineTest {
    private val policy = policyFixture()
    private val profile = eligibleProfile()
    private fun assess(p: UserProfile = profile, rules: NutritionPolicy = policy) =
        NutritionPolicyEngine.assessTest(p, rules, policyTime)
    private fun reasons(result: NutritionAssessment) = result.issues.map { it.reason }.toSet()

    @Test fun independentArithmeticKeepsReeAndDailyEnergyDistinct() {
        val c = requireNotNull(assess().calculation)
        // 800 + 1125 - 150 + 5 = 1780; 1780 * 1.5 = 2670.
        assertEquals(1780.0, c.restingKcal, 0.0)
        assertEquals(2670.0, c.maintenanceKcal, 0.0)
        assertEquals(2670.0, c.dailyKcal, 0.0)
        assertEquals(80.0, c.macroGrams.getValue("protein"), 0.0)
        assertEquals(74.16666666666667, c.macroGrams.getValue("fat"), 1e-10)
        assertEquals(333.75, c.macroGrams.getValue("carbohydrate"), 0.0)
        assertTrue(c.testOnly)
        assertEquals(TargetPath.AUTOMATIC_TEST, assess().path)
        assertEquals(c, assess().calculation)
    }

    @Test fun sexChangesReeBy166AndAgeChangesReeByFivePerYear() {
        val male = requireNotNull(assess().calculation)
        val female = requireNotNull(assess(profile.copy(calculationSex = CalculationSex.FEMALE,
            pregnant = false, breastfeeding = false)).calculation)
        assertEquals(166.0, male.restingKcal - female.restingKcal, 0.0)
        assertEquals(249.0, male.dailyKcal - female.dailyKcal, 0.0)
        assertEquals(1730.0, assess(profile.copy(ageYears = 40)).calculation!!.restingKcal, 0.0)
    }

    @Test fun allGoalsUseOnlyExplicitPolicyAdjustmentAndHealthyHasNoAdjustment() {
        mapOf(Goal.LOSE to 2470.0, Goal.MAINTAIN to 2670.0, Goal.GAIN to 2970.0,
            Goal.HEALTHY to 2670.0).forEach { (goal, expected) ->
            assertEquals(expected, assess(profile.copy(goal = goal)).dailyKcal!!, 0.0)
        }
        val r = policy.execution!!
        assertThrows(IllegalArgumentException::class.java) {
            r.copy(goalAdjustmentKcal = r.goalAdjustmentKcal + (Goal.HEALTHY to -1.0))
        }
    }

    @Test fun missingFormulaInputsNeverProduceAutomaticTarget() {
        listOf(profile.copy(ageYears = null) to PolicyReason.AGE_MISSING,
            profile.copy(calculationSex = null) to PolicyReason.SEX_MISSING,
            profile.copy(heightCm = null) to PolicyReason.HEIGHT_MISSING,
            profile.copy(weightKg = null) to PolicyReason.WEIGHT_MISSING,
            profile.copy(activityCode = null) to PolicyReason.ACTIVITY_MISSING).forEach { (p, reason) ->
            val result = assess(p)
            assertNull(result.calculation); assertNull(result.dailyKcal)
            assertTrue(reason in reasons(result))
            assertTrue(result.canUseGeneralDiary && result.canEnterSpecialistTarget)
        }
    }

    @Test fun modelScopeAndUnderageAreSeparateChecksIncludingBoundaries() {
        assertTrue(PolicyReason.UNDER_18 in reasons(assess(profile.copy(ageYears = 17))))
        assertTrue(PolicyReason.MODEL_AGE_OUT_OF_SCOPE in reasons(assess(profile.copy(ageYears = 71))))
        listOf(18, 70).forEach { assertNotNull(assess(profile.copy(ageYears = it)).calculation) }
        val narrower = policy.copy(execution = policy.execution!!.copy(modelAgeScope = AgeScope(25, 60)))
        assertNull(assess(profile.copy(ageYears = 20), narrower).calculation)
    }

    @Test fun everyExplicitRiskBlocksAllAutomaticGoalsAndUnknownAnswersRemainUnknown() {
        Goal.entries.forEach { goal ->
            val p = profile.copy(goal = goal)
            listOf(p.copy(pregnant = true), p.copy(breastfeeding = true)).forEach {
                assertEquals(EligibilityStatus.EXCLUDED, assess(it).eligibility.status)
                assertNull(assess(it).calculation)
            }
            PolicyExecutionRules.HEALTH_ANSWER_KEYS.forEach { key ->
                val excluded = assess(p.copy(eligibilityAnswers = p.eligibilityAnswers + (key to true)))
                assertEquals(EligibilityStatus.EXCLUDED, excluded.eligibility.status)
                assertTrue(PolicyIssue(PolicyReason.HEALTH_EXCLUSION, key) in excluded.issues)
                val unknown = assess(p.copy(eligibilityAnswers = p.eligibilityAnswers - key))
                assertEquals(EligibilityStatus.INCOMPLETE, unknown.eligibility.status)
                assertNull(unknown.calculation)
            }
        }
        val unknown = assess(profile.copy(calculationSex = CalculationSex.FEMALE))
        assertTrue(PolicyIssue(PolicyReason.HEALTH_ANSWER_MISSING, "pregnant") in unknown.issues)
        assertTrue(PolicyIssue(PolicyReason.HEALTH_ANSWER_MISSING, "breastfeeding") in unknown.issues)
    }

    @Test fun ageConfirmationUsesUserTimezoneAndDoesNotGuessBirthday() {
        listOf(null, CalendarDate("2026-11-06"), CalendarDate("2026-01-01")).forEach {
            assertTrue(PolicyReason.AGE_CONFIRMATION_REQUIRED in reasons(assess(profile.copy(ageAsOfDate = it))))
        }
        val boundary = profile.copy(ageAsOfDate = CalendarDate("2026-05-09")) // 180 days
        assertNotNull(assess(boundary).calculation)
        val nextLocalDay = RecordedAt("2026-11-05T21:00:00Z", "Asia/Baku")
        val result = NutritionPolicyEngine.assessTest(boundary, policy, nextLocalDay)
        assertTrue(PolicyReason.AGE_CONFIRMATION_REQUIRED in reasons(result))
        assertEquals(30, boundary.ageYears)
    }

    @Test fun onlyKnownFormulaAndMappedActivityCanRun() {
        assertTrue(PolicyReason.FORMULA_UNSUPPORTED in reasons(assess(rules = policy.copy(formula = VersionRef("unknown", 1)))))
        assertTrue(PolicyReason.FORMULA_UNSUPPORTED in reasons(assess(rules = policy.copy(formula = VersionRef("mifflin-st-jeor", 2)))))
        assertTrue(PolicyReason.ACTIVITY_UNMAPPED in reasons(assess(profile.copy(activityCode = "unknown"))))
        assertEquals(2225.0, assess(profile.copy(activityCode = "sedentary")).dailyKcal!!, 0.0)
        assertEquals(3115.0, assess(profile.copy(activityCode = "active")).dailyKcal!!, 0.0)
    }

    @Test fun noPolicyOrInactivePolicyFallsBackAndFakeActiveCannotOpenProduction() {
        assertNull(NutritionPolicyEngine.assessTest(profile, null, policyTime).calculation)
        listOf(PolicyStatus.DRAFT, PolicyStatus.APPROVED, PolicyStatus.WITHDRAWN).forEach {
            assertTrue(PolicyReason.POLICY_NOT_ACTIVE in reasons(assess(rules = policy.copy(status = it))))
        }
        val live = NutritionPolicyEngine.assess(profile, policy, policyTime)
        assertEquals(TargetPath.GENERAL, live.path)
        assertNull(live.dailyKcal); assertNull(live.calculation)
        assertTrue(PolicyReason.PRODUCTION_APPROVAL_REQUIRED in reasons(live))
        assertTrue(PolicyReason.EXECUTION_RULES_MISSING in reasons(assess(rules = policy.copy(execution = null))))
    }

    @Test fun specialistTargetDoesNotRunFormulaOrOverrideClinicalScreening() {
        val p = profile.copy(calculationSex = null, pregnant = true, specialistTargetKcal = 2100.0)
        val result = NutritionPolicyEngine.assess(p, null, policyTime)
        assertEquals(TargetPath.SPECIALIST, result.path)
        assertEquals(2100.0, result.dailyKcal!!, 0.0)
        assertNull(result.calculation)
        assertEquals(EligibilityStatus.EXCLUDED, result.eligibility.status)
        assertNull(assess(profile.copy(specialistTargetKcal = 2100.0)).calculation)
    }

    @Test fun energyBoundsRejectWithoutClampingAndOverflowNeverLeaks() {
        val bounded = policy.copy(execution = policy.execution!!.copy(dailyBounds = DailyEnergyBounds(2700.0, 3000.0)))
        assertTrue(PolicyReason.ENERGY_OUT_OF_BOUNDS in reasons(assess(rules = bounded)))
        assertNull(assess(rules = bounded).dailyKcal)
        assertNull(assess(profile.copy(weightKg = Double.MAX_VALUE)).calculation)
        assertThrows(IllegalArgumentException::class.java) { policy.copy(activityMapping = mapOf("some" to Double.NaN)) }
    }

    @Test fun calculationFreezesVersionedInputsAndSerializationRetainsUnknownAnswers() {
        val c = assess().calculation!!
        assertEquals(profile.ref, c.profile); assertEquals(policy.ref, c.policy)
        assertEquals(policy.formula, c.formula); assertEquals(policyTime, c.calculatedAt)
        assertNull(c.inputs.pregnant)
        assertEquals(CalculationSex.MALE, c.inputs.sex)
        assertEquals(profile.ageAsOfDate, c.inputs.ageAsOfDate)
        assertEquals(c, Json.decodeFromString(PolicyCalculation.serializer(), Json.encodeToString(PolicyCalculation.serializer(), c)))
        assertEquals(policy, Json.decodeFromString(NutritionPolicy.serializer(), Json.encodeToString(NutritionPolicy.serializer(), policy)))
        val updated = assess(profile.copy(ref = VersionRef("profile", 2), ageYears = 40), policy.copy(ref = VersionRef("TEST-policy", 2))).calculation!!
        assertNotEquals(c.dailyKcal, updated.dailyKcal)
        assertEquals(30, c.inputs.ageYears)
        assertEquals(VersionRef("TEST-policy", 1), c.policy)
    }

    @Test fun typedContractRejectsMissingRulesAndInvalidNumbers() {
        val r = policy.execution!!
        assertThrows(IllegalArgumentException::class.java) { r.copy(formulaRequiredInputs = setOf(FormulaInput.AGE)) }
        assertThrows(IllegalArgumentException::class.java) { r.copy(excludedAnswerKeys = emptySet()) }
        assertThrows(IllegalArgumentException::class.java) { r.copy(goalAdjustmentKcal = emptyMap()) }
        assertThrows(IllegalArgumentException::class.java) { r.copy(ageConfirmationMaxDays = 0) }
        assertThrows(IllegalArgumentException::class.java) { r.copy(macros = emptyMap()) }
        assertThrows(IllegalArgumentException::class.java) { r.copy(contractVersion = 2) }
        assertThrows(IllegalArgumentException::class.java) { AgeScope(17, 70) }
        assertThrows(IllegalArgumentException::class.java) { MacroRule(MacroBasis.ENERGY_FRACTION, 0.5) }
        assertThrows(IllegalArgumentException::class.java) { DailyEnergyBounds(2000.0, 1000.0) }
    }
}
