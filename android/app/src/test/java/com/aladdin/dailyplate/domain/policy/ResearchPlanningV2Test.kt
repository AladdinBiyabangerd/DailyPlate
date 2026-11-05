package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.CalculationSex
import com.aladdin.dailyplate.domain.Goal
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class ResearchPlanningV2Test {
    private fun inputs(goal: Goal = Goal.MAINTAIN, weight: Double = 70.0) = EerResearchInputsV2(
        30, CalculationSex.MALE, 175.0, weight, true, EerActivity.INACTIVE, true, goal,
        ResearchHealthQuestion.entries.associateWith { false })
    private fun preview(goal: Goal = Goal.MAINTAIN, weight: Double = 70.0, slots: Int = 3) =
        ResearchPlanningV2.preview(inputs(goal, weight), slots)
    private fun q(value: Double?) = ResearchMenuQuantity(value, true)
    private fun menu() = ResearchMenuObservation(q(2552.67), q(130.0), q(2552.67 * .29 / 9), q(319.08375), q(25.0),
        ResearchCarbohydrateBasis.AVAILABLE_GLYCAEMIC, ResearchCatalogFeasibility.VERIFIED_PUBLISHED_PORTIONS)
    private fun rule(menu: ResearchMenuObservation, id: ResearchMenuRule) =
        ResearchMenuChecksV2.assess(preview(), menu).rules.single { it.rule == id }

    @Test fun independentSyntheticExamplesMatchPlan() {
        val lose = preview(Goal.LOSE, 85.0)
        assertNull(lose.failure)
        assertEquals(2264.17, lose.targetKcal!!, 1e-8)
        assertEquals(113.2085, lose.macros!!.proteinGrams, 1e-8)
        assertEquals(75.47233333333332, lose.macros.fatGrams, 1e-8)
        assertEquals(283.02125, lose.macros.availableCarbohydrateGrams, 1e-8)
        assertEquals(2264.17, lose.bounds!!.lowerKcal, 1e-8)
        assertEquals(2377.3785, lose.bounds.upperKcal, 1e-8)
        val gain = preview(Goal.GAIN)
        assertEquals(2852.67, gain.targetKcal!!, 1e-8)
        assertEquals(142.6335, gain.macros!!.proteinGrams, 1e-8)
        assertEquals(95.089, gain.macros.fatGrams, 1e-8)
        assertEquals(356.58375, gain.macros.availableCarbohydrateGrams, 1e-8)
        assertEquals(2710.0365, gain.bounds!!.lowerKcal, 1e-8)
        assertEquals(2852.67, gain.bounds.upperKcal, 1e-8)
        for (goal in listOf(Goal.MAINTAIN, Goal.HEALTHY)) {
            val p = preview(goal)
            assertEquals(2425.0365, p.bounds!!.lowerKcal, 1e-8)
            assertEquals(2680.3035, p.bounds.upperKcal, 1e-8)
        }
    }

    @Test fun proteinMaximumAndCarbohydrateRemainderDoNotRepairInfeasibleSplit() {
        val m = ResearchPlanningV2.macroVector(2000.0, 140.0)!!
        assertEquals(116.2, m.proteinGrams, 1e-8)
        assertEquals(233.8, m.availableCarbohydrateGrams, 1e-8)
        assertEquals(2000.0, m.proteinGrams * 4 + m.fatGrams * 9 + m.availableCarbohydrateGrams * 4, 1e-8)
        assertNull(ResearchPlanningV2.macroVector(2000.0, 160.0))
        for (invalid in listOf(0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY, Double.MIN_VALUE)) {
            assertNull(ResearchPlanningV2.macroVector(invalid, 70.0))
        }
        // Synthetic extreme height within BMI scope produces an infeasible PRI/energy split.
        val p = ResearchPlanningV2.preview(inputs().copy(sex = CalculationSex.FEMALE, heightCm = 10000.0, weightKg = 250000.0), 3)
        assertEquals(ResearchPlanningFailure.MACRO_INFEASIBLE, p.failure)
        assertNull(p.targetKcal)
        assertNull(p.energy.candidateTargetKcal)
        assertNull(p.macros)
        assertNull(p.bounds)
    }

    @Test fun strictComparisonsAndImpossibleIntervalsRemainHardFailures() {
        val lose = ResearchPlanningV2.menuBounds(Goal.LOSE, 20000.0, 19500.0, 1800.0)!!
        assertFalse(lose.contains(20000.0))
        assertTrue(lose.contains(19500.0))
        assertFalse(lose.contains(Math.nextDown(19500.0)))
        val gain = ResearchPlanningV2.menuBounds(Goal.GAIN, 10000.0, 10300.0, 1800.0)!!
        assertFalse(gain.contains(10000.0))
        assertTrue(gain.contains(Math.nextUp(10000.0)))
        assertFalse(gain.contains(Math.nextUp(10300.0)))
        assertNull(ResearchPlanningV2.menuBounds(Goal.MAINTAIN, 1000.0, 1000.0, 1800.0))
        assertNull(ResearchPlanningV2.menuBounds(Goal.GAIN, 1000.0, 1300.0, 1400.0))
        assertNull(ResearchPlanningV2.menuBounds(Goal.LOSE, Double.MAX_VALUE, Double.MAX_VALUE, 1800.0))
        assertFalse(gain.contains(Double.NaN))
    }

    @Test fun slotTemplatesKeepDailyTargetAndBlockedInputsStayBlocked() {
        val expected = mapOf(3 to listOf(.30, .35, .35), 4 to listOf(.25, .35, .30, .10),
            5 to listOf(.25, .10, .30, .10, .25))
        for ((slots, weights) in expected) {
            val p = preview(slots = slots)
            assertEquals(weights.map { it * p.targetKcal!! }, p.slotKcal)
            assertEquals(p.targetKcal!!, p.slotKcal.sum(), 1e-8)
        }
        assertEquals(ResearchPlanningFailure.UNSUPPORTED_SLOT_COUNT, preview(slots = 2).failure)
        assertEquals(ResearchPlanningFailure.INPUT_BLOCKED,
            ResearchPlanningV2.preview(inputs().copy(healthAnswers = emptyMap()), 3).failure)
        assertEquals(ResearchPlanningFailure.INPUT_BLOCKED, ResearchPlanningV2.preview(inputs(Goal.LOSE)
            .copy(ageYears = 64, sex = CalculationSex.FEMALE, heightCm = 145.0, weightKg = 55.0), 3).failure)
    }

    @Test fun actualMenuReferencesAreSeparateFromTargetsAndWhoIsAdvisory() {
        val p = ResearchMenuChecksV2.assess(preview(), menu())
        assertTrue(p.rules.filter { it.rule != ResearchMenuRule.OTHER_NUTRIENTS_AND_PROTEIN_QUALITY }
            .all { it.status == ResearchRuleStatus.PASS })
        assertFalse(p.allHardRulesPass)
        val higherFat = menu().copy(fatGrams = q(2552.67 * .32 / 9))
        assertEquals(ResearchRuleStatus.PASS, rule(higherFat, ResearchMenuRule.FAT_RI).status)
        assertEquals(ResearchRuleStatus.FAIL, rule(higherFat, ResearchMenuRule.WHO_TOTAL_FAT_ADVISORY).status)
        assertTrue(rule(higherFat, ResearchMenuRule.WHO_TOTAL_FAT_ADVISORY).advisory)
        assertEquals(ResearchRuleStatus.FAIL, rule(menu().copy(fibreGrams = q(24.999)), ResearchMenuRule.FIBRE_AI).status)
        assertEquals(ResearchRuleStatus.FAIL, rule(menu().copy(proteinGrams = q(58.099)), ResearchMenuRule.PROTEIN_PRI).status)
        // Menu energy is checked from its source field, not synthesized from macros.
        assertEquals(ResearchRuleStatus.FAIL, rule(menu().copy(energyKcal = q(1000.0)), ResearchMenuRule.ENERGY_INTERVAL).status)
    }

    @Test fun unknownCoverageDefinitionsAndCatalogNeverPass() {
        for (basis in listOf(ResearchCarbohydrateBasis.BY_DIFFERENCE, ResearchCarbohydrateBasis.UNKNOWN))
            assertEquals(ResearchRuleStatus.UNKNOWN, rule(menu().copy(carbohydrateBasis = basis), ResearchMenuRule.CARBOHYDRATE_RI).status)
        val incomplete = ResearchMenuQuantity(100.0, false)
        for ((m, id) in listOf(
            menu().copy(energyKcal = incomplete) to ResearchMenuRule.ENERGY_INTERVAL,
            menu().copy(proteinGrams = incomplete) to ResearchMenuRule.PROTEIN_PRI,
            menu().copy(fatGrams = incomplete) to ResearchMenuRule.FAT_RI,
            menu().copy(carbohydrateGrams = incomplete) to ResearchMenuRule.CARBOHYDRATE_RI,
            menu().copy(fibreGrams = q(null)) to ResearchMenuRule.FIBRE_AI,
        )) assertEquals(ResearchRuleStatus.UNKNOWN, rule(m, id).status)
        for (catalog in listOf(ResearchCatalogFeasibility.EMPTY_CATALOG, ResearchCatalogFeasibility.NO_REAL_COMBINATION)) {
            val r = rule(menu().copy(catalogFeasibility = catalog), ResearchMenuRule.PUBLISHED_PORTION_FEASIBILITY)
            assertEquals(ResearchRuleStatus.FAIL, r.status)
            assertEquals(ResearchMenuReason.NO_FEASIBLE_PLAN, r.reason)
        }
        assertEquals(ResearchRuleStatus.UNKNOWN, rule(menu().copy(catalogFeasibility = ResearchCatalogFeasibility.NOT_EVALUATED),
            ResearchMenuRule.PUBLISHED_PORTION_FEASIBILITY).status)
        assertEquals(ResearchRuleStatus.FAIL, rule(menu().copy(fatGrams = q(Double.NaN)), ResearchMenuRule.FAT_RI).status)
    }

    @Test fun actualMenuBoundariesUseTheirOwnOperators() {
        val base = menu().copy(energyKcal = q(2520.0))
        for (fraction in listOf(.20, .35)) assertEquals(ResearchRuleStatus.PASS,
            rule(base.copy(fatGrams = q(2520 * fraction / 9)), ResearchMenuRule.FAT_RI).status)
        for (fraction in listOf(.199, .351)) assertEquals(ResearchRuleStatus.FAIL,
            rule(base.copy(fatGrams = q(2520 * fraction / 9)), ResearchMenuRule.FAT_RI).status)
        for (fraction in listOf(.45, .60)) assertEquals(ResearchRuleStatus.PASS,
            rule(base.copy(carbohydrateGrams = q(2520 * fraction / 4)), ResearchMenuRule.CARBOHYDRATE_RI).status)
        for (fraction in listOf(.449, .601)) assertEquals(ResearchRuleStatus.FAIL,
            rule(base.copy(carbohydrateGrams = q(2520 * fraction / 4)), ResearchMenuRule.CARBOHYDRATE_RI).status)
        assertEquals(ResearchRuleStatus.PASS, rule(base.copy(proteinGrams = q(.83 * 70)), ResearchMenuRule.PROTEIN_PRI).status)
        val bounds = preview().bounds!!
        for (kcal in listOf(bounds.lowerKcal, bounds.upperKcal)) assertEquals(ResearchRuleStatus.PASS,
            rule(base.copy(energyKcal = q(kcal)), ResearchMenuRule.ENERGY_INTERVAL).status)
        for (kcal in listOf(Math.nextDown(bounds.lowerKcal), Math.nextUp(bounds.upperKcal)))
            assertEquals(ResearchRuleStatus.FAIL, rule(base.copy(energyKcal = q(kcal)), ResearchMenuRule.ENERGY_INTERVAL).status)
    }

    @Test fun planningAndMenuContractsRoundTrip() {
        val p = preview()
        assertEquals(p, Json.decodeFromString<ResearchPlanningPreviewV2>(Json.encodeToString(p)))
        val m = menu()
        assertEquals(m, Json.decodeFromString<ResearchMenuObservation>(Json.encodeToString(m)))
        val a = ResearchMenuChecksV2.assess(p, m)
        assertEquals(a, Json.decodeFromString<ResearchMenuAssessment>(Json.encodeToString(a)))
    }
}
