package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.profile.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class NutritionReviewTest {
    private val start = RecordedAt("2026-11-05T08:00:00Z", "Asia/Baku")
    private val now = RecordedAt("2026-11-19T08:00:00Z", "Asia/Baku")
    private fun profile(): UserProfile = readyOnboarding().change("height", "175").change("weight", "70")
        .change("sex", "MALE").change("activity", "inactive").change("measurements_confirmed", "yes")
        .let { d -> NutritionHealthQuestion.entries.fold(d) { v, q -> v.change(q.name.lowercase(), "no") } }
        .profile("az", start, CalendarDate("2026-11-05"))
    private fun entry(day: String, kg: Double = 70.0, id: String = day, version: Long = 1) =
        WeightEntry(VersionRef(id, version), profile().userId, RecordedAt("2026-11-${day}T07:00:00Z", "Asia/Baku"), kg)

    @Test fun windowAndReviewDateUseLocalCalendarAndNeverInventMissingWeight() {
        val p = profile(); val a = NutritionPolicyEngine.assessCurrent(p, start)
        assertFalse(NutritionReview.evaluate(p, a, emptyList(), now.copy(instant = "2026-11-18T19:59:59Z")).due)
        val empty = NutritionReview.evaluate(p, a, emptyList(), now)
        assertTrue(empty.due); assertNull(empty.observedChangeKg); assertNull(empty.latestWeightKg)
        val points = listOf(entry("05"), entry("06", 72.0), entry("12", 71.0), entry("19", 70.0), entry("20"))
        val review = NutritionReview.evaluate(p, a, points, now)
        assertEquals(3, review.measurementCount); assertEquals(-2.0, review.observedChangeKg!!, 0.0)
        assertEquals(a, NutritionPolicyEngine.assessCurrent(p, start))
        assertEquals(70.0, p.weightKg!!, 0.0)
        val preview = ProfileEdit.preview(p, ProfileEdit.draft(p).change("weight", "69")
            .change("measurements_confirmed", "yes"), "", false, "New measurement", now, CalendarDate("2026-11-19"))
        assertNotEquals(a.dailyKcal, preview.assessment.dailyKcal)
        assertEquals(p.ref.version + 1, preview.after.ref.version)
    }

    @Test fun revisionsOwnersDuplicatesAndFutureMeasurementsCannotCreateFalseTrend() {
        val p = profile(); val a = NutritionPolicyEngine.assessCurrent(p, start)
        val rows = listOf(entry("10"), entry("10", 71.0, version = 2), entry("10", id = "duplicate"),
            entry("11").copy(userId = "other"), entry("20"))
        val review = NutritionReview.evaluate(p, a, rows, now)
        assertEquals(1, review.measurementCount); assertNull(review.observedChangeKg)
        assertEquals(71.0, review.latestWeightKg!!, 0.0)
        assertNull(NutritionReview.evaluate(p, a, listOf(entry("10"), entry("11")), now).observedChangeKg)
    }

    @Test fun symptomsSuspendBothAutomaticAndSpecialistTargetsAndSurviveOrdinaryEdits() {
        val p = profile(); val old = NutritionPolicyEngine.assessCurrent(p, start)
        for (reason in NutritionSuspension.entries) for (specialist in listOf(null, 1800.0)) {
            val stop = NutritionReview.suspension(p.copy(specialistTargetKcal = specialist), reason, now)
            assertEquals(TargetPath.GENERAL, stop.assessment.path)
            assertNull(stop.assessment.diaryTarget()); assertNull(stop.assessment.currentCalculation)
            assertTrue(stop.assessment.canUseGeneralDiary)
            val edited = ProfileEdit.preview(stop.after, ProfileEdit.draft(stop.after).change("meals", "3"),
                "2000", false, "Ordinary edit", now, CalendarDate("2026-11-19"))
            assertEquals(reason, edited.after.nutritionSuspension); assertNull(edited.assessment.dailyKcal)
            assertEquals(p, profileForDiaryDate(listOf(p, stop.after), LocalDate.parse("2026-11-18"), ZoneId.of("Asia/Baku")))
        }
        assertNotNull(old.diaryTarget()); assertNull(p.nutritionSuspension)
    }
}
