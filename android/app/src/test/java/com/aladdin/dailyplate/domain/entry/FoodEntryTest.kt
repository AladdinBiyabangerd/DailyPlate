package com.aladdin.dailyplate.domain.entry

import com.aladdin.dailyplate.domain.*
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class FoodEntryTest {
    private val at = RecordedAt("2026-11-05T09:00:00Z", "Asia/Baku")
    private val nutrients = NutrientSnapshot(mapOf("energy_kcal" to NutrientAmount(120.0, NutrientUnit.KCAL),
        "protein_g" to NutrientAmount(null, NutrientUnit.GRAM)))
    private fun food(state: String = "raw") = Food(VersionRef(state, 2), LocalizedText("Düyü", "Rice", "Рис"),
        listOf("düyü dənəsi"), "123", "USDA", state, nutrients, setOf("protein_g"), null, "v1", null, ContentStatus.DRAFT)

    @Test fun threeLanguagesAliasesAndPreparationVariantsPreserveExactReferences() {
        val foods = listOf(food(), food("cooked"))
        listOf("DÜYÜ", "rice", "РИС", "dənəsi").forEach { query ->
            assertEquals(foods.map { it.ref }, FoodSearch.find(foods, query).map { it.ref })
        }
        assertEquals(foods, FoodSearch.find(foods, ""))
        assertTrue(FoodSearch.find(foods, "missing").isEmpty())
    }
    @Test fun exactQuantitySourceAndNullNutrientsSurviveDraftSerialization() {
        val draft = food("cooked").portionDraft(0.25, EntryUnit.KILOGRAM, at)
        val restored = Json.decodeFromString(PortionDraft.serializer(), Json.encodeToString(PortionDraft.serializer(), draft))
        assertEquals(draft, restored)
        assertEquals(250.0, restored.grams!!, 0.0)
        assertEquals(300.0, restored.nutrients.values.getValue("energy_kcal").value!!, 0.0)
        assertNull(restored.nutrients.values.getValue("protein_g").value)
        assertEquals("cooked", restored.preparationState)
        assertEquals(VersionRef("cooked", 2), restored.food)
    }
    @Test fun manualUnknownStaysNullAndKnownValuesRequireSource() {
        assertEquals(0.25, parseEntryNumber("0,25")!!, 0.0)
        listOf("NaN", "Infinity", "-1", "abc").forEach { assertNull(parseEntryNumber(it)) }
        val unknown = nutrients.copy(values = nutrients.values.mapValues { (_, n) -> n.copy(value = null) })
        val draft = PortionDraft("Home meal", at, EntryMethod.MANUAL, quantity = 100.0, unit = EntryUnit.GRAM, nutrientsPerBasis = unknown)
        assertTrue(draft.nutrients.values.values.all { it.value == null })
        assertThrows(IllegalArgumentException::class.java) { draft.copy(nutrientsPerBasis = nutrients) }
        assertThrows(IllegalArgumentException::class.java) { draft.copy(quantity = 0.0) }
        assertThrows(IllegalArgumentException::class.java) { draft.copy(quantity = Double.MAX_VALUE, unit = EntryUnit.KILOGRAM) }
    }
    @Test fun repeatUsesNewTimeAndFrozenSnapshotWithoutCopyingEventOrPlanIdentity() {
        val old = FoodLog(VersionRef("old", 3), "old-event", "guest", RecordedAt("2025-01-01T12:00:00Z", "UTC"),
            food().ref, null, 150.0, nutrients, EntryMethod.SEARCH, "old-plan", SyncStatus.LOCAL,
            null, null, NutrientBasis.FOOD_SOURCE, null, true)
        val draft = old.repeatDraft("Rice", at).copy(quantity = 2.0)
        assertEquals(at, draft.consumedAt)
        assertEquals(old, draft.previous)
        assertEquals(300.0, draft.grams!!, 0.0)
        assertEquals(240.0, draft.nutrients.values.getValue("energy_kcal").value!!, 0.0)
        assertNull(draft.nutrients.values.getValue("protein_g").value)
        assertEquals(EntryMethod.PREVIOUS, draft.method)
    }
}
