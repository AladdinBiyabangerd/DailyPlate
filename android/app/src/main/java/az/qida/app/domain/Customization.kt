package az.qida.app.domain

import kotlinx.serialization.Serializable

@Serializable
enum class IngredientChangeType { ADD, REMOVE, SET_GRAMS }

@Serializable
data class IngredientChange(val food: VersionRef, val type: IngredientChangeType, val edibleGrams: Double?) {
    init {
        if (type == IngredientChangeType.REMOVE) require(edibleGrams == null)
        else { require(edibleGrams != null); positive(edibleGrams) }
    }
}

@Serializable
enum class InstructionStatus { NEEDS_REVIEW, ADAPTED_UNVERIFIED, VERIFIED }

@Serializable
data class RecipeCustomization(
    val ref: VersionRef, val userId: String, val baseRecipe: VersionRef,
    val ingredientChanges: List<IngredientChange>, val servingsPrepared: Double,
    val userServings: Double, val nutrientSnapshot: NutrientSnapshot,
    val missingFields: Set<String>, val instructionStatus: InstructionStatus,
    val instructions: List<String>?, val constraintResults: List<ConstraintResult>,
    val createdAt: RecordedAt,
) {
    init { positive(servingsPrepared); positive(userServings); require(userServings <= servingsPrepared) }
}

@Serializable
data class ActualRecipeSnapshot(
    val recipe: VersionRef, val customization: VersionRef?, val ingredients: List<Ingredient>,
    val servingsPrepared: Double, val consumedShare: Double, val yieldCookedGrams: Double?,
    val instructions: List<String>?, val instructionStatus: InstructionStatus,
) {
    init {
        require(ingredients.isNotEmpty()); positive(servingsPrepared); positive(consumedShare)
        require(consumedShare <= 1); yieldCookedGrams?.let(::positive)
    }
}

@Serializable
enum class ConfirmationStatus { DRAFT, AWAITING_PORTION, CONFIRMED }

@Serializable
data class PhotoMealDraft(
    val ref: VersionRef, val userId: String, val photoId: String,
    val sourcePlanItemId: String?, val servedRecipe: VersionRef?,
    val existingCustomization: VersionRef?, val recipeChangedAnswer: Boolean?,
    val actualIngredientChanges: List<IngredientChange>, val servingsPrepared: Double?,
    val consumedShare: Double?, val confirmationStatus: ConfirmationStatus,
) {
    init {
        servingsPrepared?.let(::positive)
        consumedShare?.let { positive(it); require(it <= 1) }
        if (confirmationStatus == ConfirmationStatus.CONFIRMED) require(consumedShare != null)
    }
}
