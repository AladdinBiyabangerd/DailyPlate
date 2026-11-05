package com.aladdin.dailyplate.domain.entry

import com.aladdin.dailyplate.domain.*
import org.junit.Assert.*
import org.junit.Test

class PortionConfirmationTest {
    private val at = RecordedAt("2026-11-05T10:00:00Z", "Asia/Baku")
    private fun draft() = PortionDraft("Home meal", at, EntryMethod.MANUAL,
        quantity = 0.25, unit = EntryUnit.KILOGRAM, source = "label",
        nutrientsPerBasis = NutrientSnapshot(mapOf("energy" to NutrientAmount(120.0, NutrientUnit.KCAL),
            "protein" to NutrientAmount(null, NutrientUnit.GRAM), "fat" to NutrientAmount(0.0, NutrientUnit.GRAM))))
    @Test fun explicitConfirmationFreezesPersonalSnapshotAndManualName() {
        val log = draft().confirm("guest", "event")
        assertEquals(250.0, log.confirmedPortionGrams!!, 0.0)
        assertEquals(300.0, log.nutrientSnapshot.values.getValue("energy").value!!, 0.0)
        assertNull(log.nutrientSnapshot.values.getValue("protein").value)
        assertEquals(0.0, log.nutrientSnapshot.values.getValue("fat").value!!, 0.0)
        assertEquals("Home meal", log.displayName)
        assertEquals(log, draft().confirm("guest", "event"))
        assertEquals(SyncStatus.LOCAL, log.syncStatus)
    }
    @Test fun repeatScalesActualShareWithoutCopyingEventDateOrPlanIdentity() {
        val old = draft().confirm("guest", "old").copy(recipe = VersionRef("recipe", 3),
            actualRecipeSnapshot = ActualRecipeSnapshot(VersionRef("recipe", 3), null,
                listOf(Ingredient(VersionRef("food", 1), 100.0)), 4.0, 0.25, null, null, InstructionStatus.NEEDS_REVIEW),
            nutrientBasis = NutrientBasis.RECIPE_CONFIRMED, recipeChangedAnswer = false,
            confirmedPortionGrams = null, sourcePlanItemId = "plan", photoReference = "photo")
        val nextAt = RecordedAt("2026-11-06T10:00:00Z", "Asia/Baku")
        val next = old.repeatDraft("Meal", nextAt).copy(quantity = 2.0).confirm("guest", "new")
        assertEquals(0.5, next.actualRecipeSnapshot!!.consumedShare, 0.0)
        assertNull(next.confirmedPortionGrams)
        assertNull(next.sourcePlanItemId); assertNull(next.photoReference)
        assertEquals(nextAt, next.consumedAt)
        assertEquals("new", next.clientEventId)
        assertEquals(600.0, next.nutrientSnapshot.values.getValue("energy").value!!, 0.0)
    }
}
