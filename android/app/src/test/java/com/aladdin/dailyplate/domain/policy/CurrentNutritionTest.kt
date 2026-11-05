package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.onboarding.OnboardingPreview
import com.aladdin.dailyplate.domain.profile.ProfileEdit
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class CurrentNutritionTest {
    private val at = RecordedAt("2026-11-05T08:00:00Z", "Asia/Baku")
    private val date = CalendarDate("2026-11-05")
    private fun draft() = readyOnboarding().change("height", "175").change("weight", "70")
        .change("sex", "MALE").change("activity", "inactive").change("measurements_confirmed", "yes")
        .let { d -> NutritionHealthQuestion.entries.fold(d) { value, key -> value.change(key.name.lowercase(), "no") } }
    private fun assess(d: com.aladdin.dailyplate.domain.onboarding.OnboardingDraft = draft()) =
        OnboardingPreview.create(d, "az", at, date)

    @Test fun onboardingAndProfileUseExactlyTheSameCurrentCalculation() {
        val first = assess()
        assertEquals(TargetPath.AUTOMATIC, first.assessment.path)
        assertEquals(2552.67, first.assessment.dailyKcal!!, 0.000001)
        assertNull(first.assessment.calculation)
        assertEquals(first.assessment.dailyKcal, first.assessment.diaryTarget())
        val next = ProfileEdit.preview(first.profile, ProfileEdit.draft(first.profile).change("meals", "3"),
            "", false, "Meal slots", at, date)
        assertEquals(first.assessment.currentCalculation!!.targetKcal, next.assessment.currentCalculation!!.targetKcal)
        assertEquals(3, next.assessment.currentCalculation.slotKcal.size)
        assertEquals(first.profile.ref.version + 1, next.after.ref.version)
        assertFalse(next.assessment.currentCalculation.energy.contract.productionEnabled)
        assertEquals(NutritionPolicyStatus.SPECIALIST_APPROVAL_REPORTED,
            next.assessment.currentCalculation.energy.contract.status)
    }
    @Test fun legacyActivityIncludingActiveNeverSilentlyMaps() {
        for (code in listOf("sedentary", "some", "active")) {
            val d = draft().copy(answers = draft().answers + ("activity" to code) - "activity_model")
            assertNull(assess(d).assessment.dailyKcal)
            assertNull(assess(d).assessment.currentCalculation!!.targetKcal)
        }
        for (code in listOf("inactive", "low_active", "active", "very_active"))
            assertNotNull(assess(draft().change("activity", code)).assessment.dailyKcal)
        assertNull(assess(draft().change("activity", "uncertain")).assessment.dailyKcal)
    }
    @Test fun everyMissingOrPositiveHealthAnswerAndStaleAgeBlocksAutomaticTarget() {
        for (question in NutritionHealthQuestion.entries) for (answer in listOf("", "yes"))
            assertNull(assess(draft().change(question.name.lowercase(), answer)).assessment.dailyKcal)
        val p = assess().profile
        for (oldDate in listOf(null, CalendarDate("2025-11-04"), CalendarDate("2026-11-06")))
            assertNull(NutritionPolicyEngine.assessCurrent(p.copy(ageAsOfDate = oldDate), at).dailyKcal)
        assertNull(assess(draft().change("weight", "71")).assessment.dailyKcal)
    }
    @Test fun boundaryFailuresDoNotProduceUsableCalculationSnapshots() {
        for (age in listOf("18", "65")) assertNull(assess(draft().change("age", age).change("measurements_confirmed", "yes")).assessment.dailyKcal)
        for (age in listOf("19", "64")) assertNotNull(assess(draft().change("age", age).change("measurements_confirmed", "yes")).assessment.dailyKcal)
        val floor = draft().change("sex", "FEMALE").change("age", "64").change("height", "145").change("weight", "55")
            .change("goal", "LOSE").change("measurements_confirmed", "yes")
        assertNull(assess(floor).assessment.dailyKcal)
        assertNull(assess(floor).assessment.currentCalculation!!.macros)
        assertNull(assess(draft().change("goal", "LOSE")).assessment.dailyKcal)
    }
    @Test fun diaryUsesFrozenEffectiveRevisionAndNeverFutureProfileForPastDay() {
        val p = assess().profile
        val next = p.copy(ref = p.ref.copy(version = 2), updatedAt = at.copy(instant = "2026-11-07T08:00:00Z"))
        val zone = ZoneId.of("Asia/Baku")
        assertNull(profileForDiaryDate(listOf(p, next), LocalDate.parse("2026-11-04"), zone))
        assertEquals(p, profileForDiaryDate(listOf(next, p), LocalDate.parse("2026-11-06"), zone))
        assertEquals(next, profileForDiaryDate(listOf(p, next), LocalDate.parse("2026-11-07"), zone))
    }
}
