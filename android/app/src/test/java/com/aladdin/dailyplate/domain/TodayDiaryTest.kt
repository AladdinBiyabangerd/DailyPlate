package com.aladdin.dailyplate.domain

import com.aladdin.dailyplate.domain.diary.*
import com.aladdin.dailyplate.domain.entry.*
import java.time.*
import org.junit.Assert.*
import org.junit.Test

class TodayDiaryTest {
    private fun log(at: String, energy: Double? = 100.0) = PortionDraft("Meal",
        RecordedAt(at, "Asia/Baku"), EntryMethod.MANUAL, quantity = 100.0, unit = EntryUnit.GRAM,
        source = "label", nutrientsPerBasis = NutrientSnapshot(mapOf("energy" to NutrientAmount(energy, NutrientUnit.KCAL))))
        .confirm("guest", at)
    @Test fun midnightIsHalfOpenAndZoneDependent() {
        val before = log("2026-11-04T19:59:59Z")
        val start = log("2026-11-04T20:00:00Z")
        val end = log("2026-11-05T20:00:00Z")
        val day = LocalDate.parse("2026-11-05")
        assertEquals(listOf(start), diaryDay(listOf(before, start, end), day, ZoneId.of("Asia/Baku")))
        assertEquals(listOf(end), diaryDay(listOf(before, start, end), day, ZoneOffset.UTC))
        assertTrue(diaryDay(listOf(start.copy(deleted = true)), day, ZoneId.of("Asia/Baku")).isEmpty())
    }
    @Test fun unknownAbsentOrWrongUnitCannotBecomeExactTotals() {
        val known = log("2026-11-05T10:00:00Z")
        val unknown = log("2026-11-05T11:00:00Z", null)
        assertEquals(100.0, diaryTotal(listOf(known), "energy", NutrientUnit.KCAL)!!, 0.0)
        assertNull(diaryTotal(listOf(known, unknown), "energy", NutrientUnit.KCAL))
        assertNull(diaryTotal(listOf(known), "protein", NutrientUnit.GRAM))
        assertNull(diaryTotal(listOf(known), "energy", NutrientUnit.GRAM))
        assertNull(diaryTotal(emptyList(), "energy", NutrientUnit.KCAL))
        val edited = known.resizePortion(2.0)
        assertEquals(200.0, diaryTotal(listOf(edited), "energy", NutrientUnit.KCAL)!!, 0.0)
        assertEquals(known.consumedAt, edited.consumedAt)
        assertEquals(known.clientEventId, edited.clientEventId)
        assertEquals(known.nutrientBasis, edited.nutrientBasis)
        assertNull(unknown.resizePortion(2.0).nutrientSnapshot.values["energy"]!!.value)
    }
    @Test fun daylightSavingDayUsesLocalMidnights() {
        val day = LocalDate.parse("2026-03-29")
        val entries = listOf(log("2026-03-28T23:00:00Z"), log("2026-03-29T21:59:59Z"), log("2026-03-29T22:00:00Z"))
        assertEquals(entries.take(2), diaryDay(entries, day, ZoneId.of("Europe/Berlin")))
    }
}
