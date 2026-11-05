package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*
import org.junit.Assert.*
import org.junit.Test

class NutrientReferenceSelectorTest {
    @Test fun eachNutrientSelectsItsOwnAgeSexAndLifeStageSourceRow() {
        val ironMale = reference("iron", CalculationSex.MALE, 8.0)
        val ironFemale = reference("iron", CalculationSex.FEMALE, 18.0)
        val calciumYoung = reference("calcium", null, 1000.0).copy(ages = AgeScope(18, 49))
        val calciumOlder = calciumYoung.copy(ages = AgeScope(50, 70),
            amount = NutrientAmount(1200.0, NutrientUnit.MILLIGRAM), sourceSection = "TEST older row")
        val pregnancy = ironFemale.copy(stage = LifeStage.PREGNANT,
            amount = NutrientAmount(27.0, NutrientUnit.MILLIGRAM))
        val lactation = pregnancy.copy(stage = LifeStage.BREASTFEEDING,
            amount = NutrientAmount(9.0, NutrientUnit.MILLIGRAM))
        val table = listOf(ironMale, ironFemale, calciumYoung, calciumOlder, pregnancy, lactation)
        assertEquals(listOf(calciumYoung, ironMale), NutrientReferenceSelector.select(table, 30,
            CalculationSex.MALE, LifeStage.ADULT).rows)
        assertEquals(listOf(calciumOlder, ironFemale), NutrientReferenceSelector.select(table, 50,
            CalculationSex.FEMALE, LifeStage.ADULT).rows)
        assertEquals(listOf(pregnancy), NutrientReferenceSelector.select(table, 30,
            CalculationSex.FEMALE, LifeStage.PREGNANT).rows)
        assertEquals(listOf(lactation), NutrientReferenceSelector.select(table, 30,
            CalculationSex.FEMALE, LifeStage.BREASTFEEDING).rows)
        // Selection support is not permission to activate an automatic pregnancy/lactation plan.
    }

    @Test fun absentOrOverlappingReferenceNeverBecomesZeroOrAnArbitraryRow() {
        val row = reference("iron", CalculationSex.FEMALE, 18.0)
        listOf(NutrientReferenceSelector.select(listOf(row), 30, null, LifeStage.ADULT),
            NutrientReferenceSelector.select(listOf(row), null, CalculationSex.FEMALE, LifeStage.ADULT),
            NutrientReferenceSelector.select(listOf(row), 30, CalculationSex.FEMALE, null)).forEach {
            assertTrue(it.rows.isEmpty())
            assertEquals(PolicyReason.NUTRIENT_REFERENCE_MISSING, it.issues.single().reason)
        }
        val ambiguous = NutrientReferenceSelector.select(listOf(row, row.copy(sourceSection = "other row")),
            30, CalculationSex.FEMALE, LifeStage.ADULT)
        assertTrue(ambiguous.rows.isEmpty())
        assertEquals(PolicyReason.NUTRIENT_REFERENCE_AMBIGUOUS, ambiguous.issues.single().reason)
    }
}
