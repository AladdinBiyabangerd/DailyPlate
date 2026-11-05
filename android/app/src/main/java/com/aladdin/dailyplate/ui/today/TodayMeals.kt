package com.aladdin.dailyplate.ui.today

import android.content.Context
import com.aladdin.dailyplate.data.local.CatalogRepository
import com.aladdin.dailyplate.data.local.UserRepositories
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.ui.menu.recipeLabel
import java.time.LocalDate

/** Accepted plans only; presentation never promotes reviewed/draft catalog content. */
internal data class TodayMeal(val planId: String, val item: MealPlanItem, val name: String)

internal fun loadTodayMeals(context: Context, language: String, date: LocalDate): List<TodayMeal> {
    val plan = UserRepositories.open(context).use { repositories ->
        repositories.plans.current("local-guest").filter {
            date.toString() >= it.week.iso &&
                date <= LocalDate.parse(it.week.iso).plusDays(6)
        }.maxByOrNull { it.week.iso }
    } ?: return emptyList()
    return CatalogRepository.open(context).use { catalog ->
        plan.items.filter { it.date.iso == date.toString() }.map { item ->
            TodayMeal(plan.ref.id, item, catalog.recipe(item.recipe)?.names?.recipeLabel(language) ?: item.recipe.id)
        }
    }
}
