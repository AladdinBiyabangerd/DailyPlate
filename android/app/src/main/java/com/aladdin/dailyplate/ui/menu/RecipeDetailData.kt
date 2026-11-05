package com.aladdin.dailyplate.ui.menu

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.*

/** Exact served revision; never silently substitutes the latest library/customization version. */
internal data class RecipeDetailData(
    val plan: MealPlan, val item: MealPlanItem, val recipe: Recipe,
    val customization: RecipeCustomization?, val foods: Map<VersionRef, Food>,
) {
    val ingredients: List<Ingredient> get() {
        val values = recipe.ingredients.associateBy { it.food }.toMutableMap()
        customization?.ingredientChanges?.forEach { change ->
            when (change.type) {
                IngredientChangeType.REMOVE -> values.remove(change.food)
                else -> values[change.food] = Ingredient(change.food, requireNotNull(change.edibleGrams))
            }
        }
        return values.values.toList()
    }
    val instructions get() = if (customization == null) recipe.instructions else customization.instructions
    val instructionStatus get() = customization?.instructionStatus ?: if (recipe.status == ContentStatus.PUBLISHED)
        InstructionStatus.VERIFIED else InstructionStatus.NEEDS_REVIEW
    // Base recipe has a measured cooked yield. Customization explicitly records batch/personal servings.
    val batchServings: Double? get() = customization?.let { it.servingsPrepared * item.plannedServings / it.userServings }
        ?: recipe.yieldCookedGrams?.let { yield -> item.portionGrams?.let { item.plannedServings * yield / it } }

    fun command(servings: Double, name: String, event: String, at: RecordedAt,
        preparedServings: Double = requireNotNull(batchServings)): PlanConsumption {
        require(servings.isFinite() && servings > 0 && servings <= item.remainingPlannedServings)
        val originalBatch = requireNotNull(batchServings)
        require(preparedServings.isFinite() && preparedServings > 0)
        require(servings <= preparedServings && ingredients.isNotEmpty())
        val factor = preparedServings / originalBatch
        require(factor.isFinite() && factor > 0)
        val scaledIngredients = ingredients.map { it.copy(edibleGrams = it.edibleGrams * factor) }
        require(scaledIngredients.all { it.edibleGrams.isFinite() && it.edibleGrams > 0 })
        val yield = if (customization == null) recipe.yieldCookedGrams?.times(factor) else null
        require(yield == null || (yield.isFinite() && yield > 0))
        val actual = ActualRecipeSnapshot(recipe.ref, item.customization, scaledIngredients, preparedServings,
            servings / preparedServings, yield, instructions, instructionStatus)
        val log = FoodLog(VersionRef("meal-$event", 1), event, plan.userId, at, null, item.recipe,
            item.portionGrams?.times(servings / item.plannedServings),
            WeeklyNutrition.scale(item.nutrientSnapshot, servings / item.plannedServings),
            EntryMethod.PLAN, item.id, SyncStatus.LOCAL, actual, false,
            NutrientBasis.RECIPE_CONFIRMED, null, true, displayName = name)
        return PlanConsumption(plan.ref, item.id, servings, log)
    }
}

internal fun LocalizedText.recipeLabel(language: String) = when (language) { "en" -> en; "ru" -> ru; else -> az } ?: az
