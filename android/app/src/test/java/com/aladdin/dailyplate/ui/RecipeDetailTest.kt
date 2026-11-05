package com.aladdin.dailyplate.ui

import com.aladdin.dailyplate.data.local.*
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.editorialRecipe
import com.aladdin.dailyplate.ui.menu.*
import org.junit.Assert.*
import org.junit.Test

class RecipeDetailTest {
    internal fun detail(): RecipeDetailData {
        val plan = plan().let { p -> p.copy(items = p.items.map { it.copy(nutrientSnapshot = NutrientSnapshot(mapOf(
            "energy_kcal" to NutrientAmount(400.0, NutrientUnit.KCAL),
            "protein_g" to NutrientAmount(null, NutrientUnit.GRAM)))) }) }
        val recipe = editorialRecipe().recipe.copy(ref = plan.items.first().recipe,
            ingredients = listOf(Ingredient(foodRef, 400.0)), yieldCookedGrams = 720.0)
        return RecipeDetailData(plan, plan.items.first(), recipe, null, emptyMap())
    }
    @Test fun halfPersonalPortionIsNotHalfOfTheBatch() {
        val d = detail()
        val c = d.command(0.5, "TEST", "event", recordedAt)
        assertEquals(200.0, c.log.nutrientSnapshot.values.getValue("energy_kcal").value!!, 0.0)
        assertNull(c.log.nutrientSnapshot.values.getValue("protein_g").value)
        assertEquals(0.125, c.log.actualRecipeSnapshot!!.consumedShare, 0.0)
        assertEquals(90.0, c.log.confirmedPortionGrams!!, 0.0)
        assertEquals(0.5, c.applyTo(d.plan).items.first().remainingPlannedServings, 0.0)
        assertEquals(d.recipe.ingredients, c.log.actualRecipeSnapshot!!.ingredients)
    }
    @Test fun exactCustomizationAndMissingYieldArePreserved() {
        val d = detail()
        val custom = RecipeCustomization(VersionRef("custom", 2), d.plan.userId, d.recipe.ref,
            listOf(IngredientChange(foodRef, IngredientChangeType.SET_GRAMS, 600.0)), 4.0, 1.0,
            d.item.nutrientSnapshot, setOf("protein_g"), InstructionStatus.NEEDS_REVIEW, null, emptyList(), recordedAt)
        val item = d.item.copy(customization = custom.ref)
        val changed = d.copy(item = item, plan = d.plan.copy(items = listOf(item)), customization = custom)
        val c = changed.command(0.5, "TEST", "custom-event", recordedAt)
        assertEquals(custom.ref, c.log.actualRecipeSnapshot!!.customization)
        assertEquals(600.0, c.log.actualRecipeSnapshot!!.ingredients.single().edibleGrams, 0.0)
        assertNull(c.log.actualRecipeSnapshot!!.yieldCookedGrams)
        assertNull(c.log.actualRecipeSnapshot!!.instructions)
        c.applyTo(changed.plan)
        val noYield = d.copy(recipe = d.recipe.copy(yieldCookedGrams = null, nutrientsPer100g = null))
        assertNull(noYield.batchServings)
        assertThrows(IllegalArgumentException::class.java) { noYield.command(0.5, "TEST", "e", recordedAt) }
        listOf(0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY, 2.0).forEach { amount ->
            assertThrows(IllegalArgumentException::class.java) { d.command(amount, "TEST", "e", recordedAt) }
        }
    }
}
