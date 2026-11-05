package com.aladdin.dailyplate.domain

import com.aladdin.dailyplate.domain.profile.ProfileEdit
import com.aladdin.dailyplate.domain.policy.TargetPath
import org.junit.Assert.*
import org.junit.Test

class ProfileEditTest {
    private val at = RecordedAt("2026-11-05T10:00:00Z", "Asia/Baku")
    private val date = CalendarDate("2026-11-05")
    private fun base() = readyOnboarding().change("allergies", "milk").change("extra_allergies", "sesame")
        .change("disliked_foods", "onion").profile("az", at, CalendarDate("2026-11-04"))

    @Test fun previewPreservesOldAgeAndUntouchedDataAndDoesNotInventClinicalTarget() {
        val p = base().copy(eligibilityAnswers = mapOf("future_screening" to true), dislikedFoodIds = setOf("fdc:123", "user:onion"), equipment = setOf("oven"))
        val draft = ProfileEdit.draft(p)
        assertEquals("AZN", draft.answer("currency"))
        val preview = ProfileEdit.preview(p, draft.change("goal", "GAIN"), "", false, "New goal", at, date)
        assertEquals(Goal.HEALTHY, p.goal)
        assertEquals(Goal.GAIN, preview.after.goal)
        assertEquals(p.ageAsOfDate, preview.after.ageAsOfDate)
        assertEquals(p.allergies, preview.after.allergies)
        assertEquals(p.dislikedFoodIds, preview.after.dislikedFoodIds)
        assertEquals(p.equipment, preview.after.equipment)
        assertEquals(true, preview.after.eligibilityAnswers["future_screening"])
        assertEquals(TargetPath.GENERAL, preview.assessment.path)
        assertNull(preview.assessment.dailyKcal)
        assertEquals(setOf("goal"), preview.request.changedFields)
        assertEquals("pending_k16_preview", preview.request.status)
    }

    @Test fun ageRequiresExplicitConfirmationAndNeverSilentlyAdvances() {
        val p = base()
        val edited = ProfileEdit.draft(p).change("age", "31")
        assertThrows(IllegalArgumentException::class.java) { ProfileEdit.preview(p, edited, "", false, "Age", at, date) }
        val preview = ProfileEdit.preview(p, edited, "", true, "Age confirmed", at, date)
        assertEquals(31, preview.after.ageYears)
        assertEquals(date, preview.after.ageAsOfDate)
        assertEquals(30, p.ageYears)
    }

    @Test fun specialistInputIsOptionalValidatedAndDoesNotRemoveExclusion() {
        val p = base().copy(pregnant = true)
        val preview = ProfileEdit.preview(p, ProfileEdit.draft(p), "1800,5", false, "Specialist", at, date)
        assertEquals(TargetPath.SPECIALIST, preview.assessment.path)
        assertEquals(1800.5, preview.assessment.dailyKcal!!, 0.0)
        assertEquals("excluded", preview.after.eligibilityStatus)
        for (value in listOf("NaN", "Infinity", "0", "-1")) assertThrows(IllegalArgumentException::class.java) {
            ProfileEdit.preview(p, ProfileEdit.draft(p), value, false, "Specialist", at, date)
        }
        assertThrows(IllegalArgumentException::class.java) { ProfileEdit.preview(p, ProfileEdit.draft(p), "", false, "", at, date) }
    }
}
