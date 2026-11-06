package com.aladdin.dailyplate.ui

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.ui.menu.menuRecipeSummary
import org.junit.Assert.*
import org.junit.Test

class MenuRecipeSummaryTest {
    @Test fun displayUsesExactRecipeRevisionAndNeverReusesBaseTimeForCustomization() {
        val detail = RecipeDetailTest().detail()
        val recipe = detail.recipe.copy(time = PreparationTime(25, 10, 15))
        assertEquals(25, menuRecipeSummary(detail.item, recipe)!!.totalMinutes)
        assertNull(menuRecipeSummary(detail.item, recipe.copy(ref = recipe.ref.copy(version = recipe.ref.version + 1))))
        assertNull(menuRecipeSummary(detail.item, null))
        assertNull(menuRecipeSummary(detail.item.copy(customization = VersionRef("custom", 1)), recipe)!!.totalMinutes)
        assertNull(menuRecipeSummary(detail.item, recipe.copy(time = PreparationTime(null, null, null)))!!.totalMinutes)
    }
}
