package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.EditorialSnapshot

/** Evidence is exact-version, editorial data; missing evidence is never an empty safe set. */
data class IngredientSafety(
    val food: Food,
    val mappingResolved: Boolean?,
    val compoundIngredientsComplete: Boolean?,
    val crossContactAllergens: Set<String>?,
    val dietaryTags: Set<String>?,
    val availableRegions: Set<String>?,
)

data class CandidateCatalog(
    val ref: VersionRef,
    val recipes: List<EditorialSnapshot>,
    val ingredients: Map<VersionRef, IngredientSafety>,
) {
    init {
        require(recipes.map { it.content.ref }.distinct().size == recipes.size)
        require(ingredients.all { (ref, evidence) -> ref == evidence.food.ref })
    }
}

data class CandidateRequest(
    val profile: VersionRef, val policy: VersionRef, val catalog: VersionRef,
    val at: RecordedAt,
    // Onboarding does not collect equipment. Null means unknown, not no requirements.
    val availableEquipment: Set<String>? = null,
    val portion: PortionRange? = null,
)

enum class RuleStatus { PASS, FAIL, UNKNOWN, NOT_APPLICABLE }
enum class CandidateStatus { SUCCESS, NO_FEASIBLE_PLAN, INCOMPLETE_DATA, POLICY_UNAVAILABLE, STALE_INPUT }

data class CandidateRuleResult(
    val ruleId: String, val policy: VersionRef, val scope: String,
    val operator: String, val threshold: String?, val observedValue: String?, val unit: String?,
    val status: RuleStatus, val evidenceRefs: List<String>, val missingFields: List<String>,
    val reason: String,
)

data class RecipeCandidate(val recipe: VersionRef, val portion: PortionRange)
data class CandidateResult(
    val status: CandidateStatus, val testOnly: Boolean,
    val candidates: List<RecipeCandidate>, val ruleResults: List<CandidateRuleResult>,
) {
    val unmetConstraints get() = ruleResults.filter { it.status == RuleStatus.FAIL || it.status == RuleStatus.UNKNOWN }
}
