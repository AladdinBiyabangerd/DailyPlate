package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.policy.NutritionCarbohydrateBasis
import com.aladdin.dailyplate.domain.policy.NutritionPlanningPreview
import com.aladdin.dailyplate.domain.pricing.*
import java.time.ZoneId

/** Exact-version editorial mapping; quantities describe the whole cooked batch.
 * No food group, available carbohydrate or free sugar is inferred from a recipe name. */
data class PlanningEvidence(
    val recipe: VersionRef, val carbohydrateBasis: NutritionCarbohydrateBasis,
    val fruitVegetableGrams: Double?, val vegetableSources: Set<String>?,
    val grainSources: Set<String>?, val proteinSources: Set<String>?, val sources: List<String>,
    val proteinQualityReviewed: Boolean? = null,
) {
    init { fruitVegetableGrams?.let { require(it.isFinite() && it >= 0) }; require(sources.isNotEmpty()) }
}

/** Versioned product search parameters, not clinical thresholds. */
data class WeeklySearchRules(
    val ref: VersionRef, val maximumNodes: Int = 100_000,
    val maximumRecipeOccurrences: Int = 7, val minimumSourcesPerGroup: Int = 2,
) {
    init { require(maximumNodes in 1..1_000_000); require(maximumRecipeOccurrences > 0); require(minimumSourcesPerGroup > 0) }
}
data class PlanGenerationRequest(
    val candidate: CandidateRequest, val weekStart: CalendarDate, val timezone: String,
    val goal: Goal, val mealsPerDay: Int, val seed: Long, val requestId: String,
    val search: WeeklySearchRules,
) {
    init { ZoneId.of(timezone); require(mealsPerDay in 3..5); require(requestId.isNotBlank()) }
}
data class WeeklyPlanningData(
    val catalog: CandidateCatalog, val evidence: Map<VersionRef, PlanningEvidence>,
    val prices: List<PriceObservation>, val freshness: PriceFreshness?,
    val coverage: PlanningCoverage? = null,
) { init { require(evidence.all { (ref, evidence) -> ref == evidence.recipe }) } }
enum class GenerationStatus {
    SUCCESS, NO_FEASIBLE_PLAN, INCOMPLETE_DATA, POLICY_UNAVAILABLE, STALE_INPUT,
    TEMPORARY_FAILURE, REQUEST_CONFLICT,
}
data class GeneratedMeal(
    val day: CalendarDate, val slot: Int, val recipe: VersionRef, val portionGrams: Double,
    val nutrients: NutrientSnapshot, val explanation: String,
)
data class GeneratedAlternative(val day: CalendarDate, val slot: Int, val recipe: VersionRef, val portionGrams: Double)
data class GeneratedWeek(
    val request: PlanGenerationRequest, val meals: List<GeneratedMeal>,
    val dailyNutrients: List<NutrientSnapshot>, val weeklyNutrients: NutrientSnapshot,
    val alternatives: List<GeneratedAlternative>, val shoppingCost: ShoppingCost,
    val nutritionPreview: NutritionPlanningPreview, val coverage: PlanningCoverage,
) {
    val targetKcal: Double get() = requireNotNull(nutritionPreview.targetKcal)
}
data class PlanGenerationResult(
    val status: GenerationStatus, val testOnly: Boolean, val plan: GeneratedWeek?,
    val ruleResults: List<CandidateRuleResult>, val visitedNodes: Int,
) {
    val unmetConstraints get() = ruleResults.filter { it.status == RuleStatus.FAIL || it.status == RuleStatus.UNKNOWN }
}

/** Explicit source-specific supplemental policy contract. No default clinical thresholds.
 * Day and week refer to totals in that window, never an unlabelled habitual average. */
enum class PlanningWindow { DAY, WEEK }
enum class PlanningOperator { LESS_THAN, AT_MOST, AT_LEAST, GREATER_THAN }
data class PlanningNutrientRule(
    val id: String, val nutrient: String, val threshold: NutrientAmount,
    val operator: PlanningOperator, val window: PlanningWindow, val source: String,
) {
    init { require(id.isNotBlank() && nutrient.isNotBlank() && source.isNotBlank()); require(threshold.value != null) }
}
data class PlanningCoverage(
    val ref: VersionRef, val policy: VersionRef, val review: Review,
    val rules: List<PlanningNutrientRule>,
) {
    init { require(rules.isNotEmpty()); require(rules.map { it.id }.distinct().size == rules.size) }
}
