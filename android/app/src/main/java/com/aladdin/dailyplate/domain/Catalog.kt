package com.aladdin.dailyplate.domain

import kotlinx.serialization.Serializable

@Serializable
data class Food(
    val ref: VersionRef,
    val names: LocalizedText,
    val aliases: List<String>,
    val sourceId: String,
    val sourceType: String,
    val preparationState: String,
    val nutrientsPer100g: NutrientSnapshot,
    val missingFields: Set<String>,
    val allergens: Set<String>?,
    val sourceVersion: String,
    val review: Review?,
    val status: ContentStatus,
) {
    init { require(status != ContentStatus.PUBLISHED || review != null) }
}

@Serializable
data class Ingredient(val food: VersionRef, val edibleGrams: Double) {
    init { positive(edibleGrams) }
}

@Serializable
data class PortionRange(val minGrams: Double, val maxGrams: Double, val stepGrams: Double) {
    init { positive(minGrams); positive(stepGrams); require(maxGrams.isFinite() && maxGrams >= minGrams) }
}

@Serializable
data class PreparationTime(val totalMinutes: Int?, val activeMinutes: Int?, val waitingMinutes: Int?) {
    init { listOfNotNull(totalMinutes, activeMinutes, waitingMinutes).forEach { require(it >= 0) } }
}

@Serializable
data class RecipeDiscovery(
    val cuisineInspiration: List<String>, val budgetTierCandidate: BudgetTier?,
    val budgetStatus: String, val goalMatching: Set<Goal>,
    val evidenceRefs: List<String>, val evidenceScope: String,
)

@Serializable
data class Recipe(
    val ref: VersionRef,
    val names: LocalizedText,
    val ingredients: List<Ingredient>,
    val cookingMethod: String,
    val instructions: List<String>,
    val yieldCookedGrams: Double?,
    val retentionMethod: String?,
    val nutrientsTotal: NutrientSnapshot,
    val nutrientsPer100g: NutrientSnapshot?,
    val portionRange: PortionRange?,
    val portionPhotos: Map<String, String>,
    val allergens: Set<String>?,
    val equipment: Set<String>,
    val time: PreparationTime,
    val storageInstructionId: String?,
    val costRegion: String?,
    val costDate: CalendarDate?,
    val priceEstimate: Money?,
    val discovery: RecipeDiscovery?,
    val review: Review?,
    val status: ContentStatus,
) {
    init {
        require(ingredients.isNotEmpty())
        yieldCookedGrams?.let(::positive)
        require(nutrientsPer100g == null || yieldCookedGrams != null)
        require(status != ContentStatus.PUBLISHED || review != null)
    }
}

@Serializable
data class EvidenceCard(
    val ref: VersionRef, val claimId: String, val text: LocalizedText,
    val sourceUrl: String, val sourceSection: String, val eligiblePopulation: String,
    val limitations: LocalizedText, val review: Review?, val status: ContentStatus,
) {
    init { require(status != ContentStatus.PUBLISHED || review != null) }
}
