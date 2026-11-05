package com.aladdin.dailyplate.domain.entry

import com.aladdin.dailyplate.domain.*

/** Build only after explicit confirmation. The caller retains this immutable event for retries. */
fun PortionDraft.confirm(userId: String, eventId: String): FoodLog {
    val prior = previous
    val actual = prior?.actualRecipeSnapshot?.let {
        it.copy(consumedShare = it.consumedShare * quantity)
    }
    return FoodLog(
        ref = VersionRef(eventId, 1), clientEventId = eventId, userId = userId,
        consumedAt = consumedAt, food = food, recipe = prior?.recipe,
        confirmedPortionGrams = grams, nutrientSnapshot = nutrients, entryMethod = method,
        sourcePlanItemId = null, syncStatus = SyncStatus.LOCAL,
        actualRecipeSnapshot = actual, recipeChangedAnswer = prior?.recipeChangedAnswer,
        nutrientBasis = prior?.nutrientBasis ?: if (method == EntryMethod.MANUAL) NutrientBasis.MANUAL else NutrientBasis.FOOD_SOURCE,
        photoReference = null, portionConfirmed = true, displayName = name,
        preparationState = preparationState ?: prior?.preparationState,
        repeatedFrom = prior?.ref, nutrientSource = prior?.nutrientSource ?: source, enteredQuantity = quantity, enteredUnit = unit.name,
    )
}
