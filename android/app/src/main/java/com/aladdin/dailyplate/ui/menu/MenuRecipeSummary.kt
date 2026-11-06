package com.aladdin.dailyplate.ui.menu

import com.aladdin.dailyplate.domain.*

/** Display metadata only. Energy always comes from the accepted item's snapshot. */
internal data class MenuRecipeSummary(val names: LocalizedText, val totalMinutes: Int?)

internal fun menuRecipeSummary(item: MealPlanItem, recipe: Recipe?): MenuRecipeSummary? =
    recipe?.takeIf { it.ref == item.recipe }?.let {
        // Customized preparation time is not inherited from the base recipe.
        MenuRecipeSummary(it.names, it.time.totalMinutes.takeIf { item.customization == null })
    }
