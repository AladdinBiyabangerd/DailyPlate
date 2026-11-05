package com.aladdin.dailyplate.domain

import com.aladdin.dailyplate.domain.onboarding.OnboardingDraft
import org.junit.Assert.*
import org.junit.Test

fun readyOnboarding() = OnboardingDraft("onboarding-test", answers = mapOf(
    "goal" to "HEALTHY", "age" to "30", "height" to "170,5", "weight" to "65,2",
    "activity" to "some", "allergies" to "unknown", "time" to "30", "meals" to "5",
    "tier" to "ECONOMICAL", "region" to "Baku", "currency" to "AZN", "people" to "2",
    "coverage" to "breakfast;lunch;dinner"))

class OnboardingDraftTest {
    @Test fun decimalCommaAndOptionalSexProduceUnassessedProfileWithoutInventedTargets() {
        val d = readyOnboarding()
        assertTrue((0..4).all { d.errors(it).isEmpty() })
        val p = d.profile("az", RecordedAt("2026-11-04T10:00:00Z", "Asia/Baku"), CalendarDate("2026-11-04"))
        assertEquals(65.2, p.weightKg!!, 0.0); assertNull(p.calculationSex); assertNull(p.allergies)
        assertNull(p.pregnant); assertNull(p.specialistTargetKcal); assertNull(p.budget.weeklyAmount)
        assertEquals("not_evaluated", p.eligibilityStatus); assertEquals(5, p.mealsPerDay)
        assertEquals("2026-11-04", p.ageAsOfDate!!.iso)
        Goal.entries.forEach { goal ->
            assertEquals(goal, d.change("goal", goal.name).profile("en", p.updatedAt, p.ageAsOfDate).goal)
        }
    }
    @Test fun maleSelectionClearsReproductiveAnswersAndIgnoresStaleSavedAnswers() {
        val female = readyOnboarding().change("sex", "FEMALE")
            .change("pregnant", "yes").change("breastfeeding", "no")
        assertTrue(female.asksReproductiveHealth)
        assertTrue(female.change("sex", "").asksReproductiveHealth)
        val male = female.change("sex", "MALE")
        assertFalse(male.asksReproductiveHealth)
        assertEquals("", male.answer("pregnant"))
        assertEquals("", male.answer("breastfeeding"))
        assertEquals("", male.change("sex", "FEMALE").answer("pregnant"))
        val stale = female.copy(answers = female.answers + ("sex" to "MALE"))
        val profile = stale.profile("az", RecordedAt("2026-11-04T10:00:00Z", "UTC"), CalendarDate("2026-11-04"))
        assertNull(profile.pregnant)
        assertNull(profile.breastfeeding)
    }
    @Test fun blankNegativeNonFiniteAndFractionalCountsAreRejected() {
        for (value in listOf("", "-1", "0", "NaN", "Infinity", "1e999")) {
            assertTrue("weight=$value", "weight" in readyOnboarding().change("weight", value).errors(1))
        }
        assertTrue("age" in readyOnboarding().change("age", "18,5").errors(1))
        assertTrue("people" in readyOnboarding().change("people", "2.5").errors(4))
        assertTrue("amount" in readyOnboarding().change("amount", "NaN").errors(4))
        assertTrue("allergies" in readyOnboarding().change("allergies", "").errors(3))
    }
    @Test fun allergyNoneIsExclusiveAndWheatRemainsSeparateFromGluten() {
        var d = readyOnboarding().toggleAllergen("none").toggleAllergen("wheat")
        assertEquals(setOf("wheat"), d.items("allergies"))
        d = d.change("restrictions", "gluten").change("extra_allergies", "sesame")
        val p = d.profile("az", RecordedAt("2026-11-04T10:00:00Z", "UTC"), CalendarDate("2026-11-04"))
        assertEquals(setOf("wheat", "custom:sesame"), p.allergies)
        assertEquals(setOf("gluten"), p.dietaryRestrictions)
        assertEquals("none", d.toggleAllergen("none").answer("allergies"))
        assertEquals("", d.change("extra_allergies", "").answer("extra_allergies"))
        assertTrue(d.change("disliked_foods", "onion").change("disliked_foods", "").items("disliked_foods").isEmpty())
    }
    @Test fun budgetScopeKeepsItsAmountAndCoverage() {
        for (tier in BudgetTier.entries) {
            val d = readyOnboarding()
                .change("tier", tier.name).change("amount", "125,50")
            val p = d.profile("ru", RecordedAt("2026-11-04T10:00:00Z", "UTC"), CalendarDate("2026-11-04"))
            assertEquals("125.50", p.budget.weeklyAmount!!.amount)
            assertEquals("AZN", p.budget.weeklyAmount.currency)
            assertEquals(2, p.budget.people)
            assertEquals(setOf("breakfast", "lunch", "dinner"), p.budget.coveredMealSlots)
        }
    }
    @Test fun removedInventoryInputsCannotBlockOrInfluenceNewProfilesFromOldDrafts() {
        val draft = readyOnboarding().copy(storedHomeFoods = listOf(
            HomeFood(VersionRef("user:rice", 1), "dry", 200.0, "g")))
            .change("equipment", "oven").change("home_name", "rice").change("home_quantity", "NaN")
        assertTrue(draft.errors(4).isEmpty())
        val profile = draft.profile("az", RecordedAt("2026-11-05T10:00:00Z", "UTC"), CalendarDate("2026-11-05"))
        assertTrue(profile.equipment.isEmpty())
        assertTrue(profile.homeFoods.isEmpty())
    }

}
