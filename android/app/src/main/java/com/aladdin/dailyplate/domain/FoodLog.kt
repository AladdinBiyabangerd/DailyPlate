package com.aladdin.dailyplate.domain

import kotlinx.serialization.Serializable

@Serializable
enum class EntryMethod { SEARCH, MANUAL, PHOTO, PLAN, PREVIOUS }
@Serializable
enum class NutrientBasis { FOOD_SOURCE, MANUAL, RECIPE_CONFIRMED, RECIPE_USER_MODIFIED, PHOTO_ESTIMATE }

@Serializable
data class FoodLog(
    val ref: VersionRef, val clientEventId: String, val userId: String,
    val consumedAt: RecordedAt, val food: VersionRef?, val recipe: VersionRef?,
    val confirmedPortionGrams: Double?, val nutrientSnapshot: NutrientSnapshot,
    val entryMethod: EntryMethod, val sourcePlanItemId: String?, val syncStatus: SyncStatus,
    val actualRecipeSnapshot: ActualRecipeSnapshot?, val recipeChangedAnswer: Boolean?,
    val nutrientBasis: NutrientBasis, val photoReference: String?, val portionConfirmed: Boolean,
    val deleted: Boolean = false,
    val displayName: String? = null, val preparationState: String? = null,
    val repeatedFrom: VersionRef? = null, val nutrientSource: String? = null, val enteredQuantity: Double? = null, val enteredUnit: String? = null,
) {
    init {
        require(clientEventId.isNotBlank()); require(userId.isNotBlank())
        require(food == null || recipe == null)
        require(portionConfirmed)
        confirmedPortionGrams?.let(::positive)
        require(confirmedPortionGrams != null || actualRecipeSnapshot != null)
        if (nutrientBasis == NutrientBasis.RECIPE_CONFIRMED || nutrientBasis == NutrientBasis.RECIPE_USER_MODIFIED) {
            require(actualRecipeSnapshot != null && recipe == actualRecipeSnapshot.recipe)
            require(recipeChangedAnswer == (nutrientBasis == NutrientBasis.RECIPE_USER_MODIFIED))
        }
    }
}
