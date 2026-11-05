package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.*
import com.aladdin.dailyplate.domain.policy.*
import kotlin.math.ceil
import kotlin.math.floor

/** K13 only: no scoring, prices, nutrient adequacy claim or generation/persistence. */
object RecipeCandidateFilter {
    fun filter(request: CandidateRequest, profile: UserProfile, policy: NutritionPolicy?,
               catalog: CandidateCatalog): CandidateResult = evaluate(request, profile, policy, catalog, false)

    /** K05 editorial envelopes and K08 calculations remain synthetic even after publication. */
    internal fun filterTest(request: CandidateRequest, profile: UserProfile, policy: NutritionPolicy?,
                            catalog: CandidateCatalog): CandidateResult = evaluate(request, profile, policy, catalog, true)

    private fun evaluate(q: CandidateRequest, p: UserProfile, policy: NutritionPolicy?,
                         catalog: CandidateCatalog, test: Boolean): CandidateResult {
        val results = mutableListOf<CandidateRuleResult>()
        fun rule(id: String, scope: String, status: RuleStatus, reason: String,
                 observed: String? = null, threshold: String? = null, unit: String? = null,
                 operator: String = "matches", evidence: List<String> = emptyList()) {
            results += CandidateRuleResult(id, q.policy, scope, operator, threshold, observed, unit,
                status, evidence, if (status == RuleStatus.UNKNOWN) listOf(id) else emptyList(), reason)
        }
        fun finish(status: CandidateStatus, candidates: List<RecipeCandidate> = emptyList()) =
            CandidateResult(status, test, candidates, results.toList())
        if (q.profile != p.ref || q.catalog != catalog.ref || (policy != null && q.policy != policy.ref)) {
            rule("versions", "request", RuleStatus.FAIL, "Profile, policy or catalog version changed")
            return finish(CandidateStatus.STALE_INPUT)
        }
        val assessment = if (test) NutritionPolicyEngine.assessTest(p, policy, q.at)
            else NutritionPolicyEngine.assess(p, policy, q.at)
        if (assessment.issues.isNotEmpty() || assessment.calculation == null) {
            assessment.issues.forEach { rule(it.reason.name, "profile", RuleStatus.FAIL,
                "Policy/eligibility prevents personalized candidates", it.field) }
            if (assessment.issues.isEmpty()) rule("policy_path", "profile", RuleStatus.FAIL,
                "Specialist target does not authorize automatic menus")
            return finish(CandidateStatus.POLICY_UNAVAILABLE)
        }
        val accepted = mutableListOf<RecipeCandidate>()
        catalog.recipes.sortedWith(compareBy({ it.content.ref.id }, { it.content.ref.version })).forEach { envelope ->
            val r = (envelope.content as? EditorialContent.RecipeContent)?.recipe ?: return@forEach
            val start = results.size
            val scope = "${r.ref.id}:${r.ref.version}"
            fun check(id: String, value: Boolean?, reason: String, observed: String? = null,
                      threshold: String? = null, unit: String? = null) = rule(id, scope,
                when (value) { true -> RuleStatus.PASS; false -> RuleStatus.FAIL; null -> RuleStatus.UNKNOWN },
                reason, observed, threshold, unit, evidence = envelope.metadata.sourceRefs)
            check("publication", envelope.stage == EditorialStage.PUBLISHED && envelope.review != null &&
                PublicationRequirements.missing(envelope.content, envelope.metadata).isEmpty(),
                "Only complete currently published exact versions are candidates")
            check("disliked_recipe", r.ref.id !in p.dislikedRecipeIds, "Recipe dislikes are retained")
            val safety = r.ingredients.map { catalog.ingredients[it.food] }
            check("ingredient_publication", safety.all { it?.food?.status == ContentStatus.PUBLISHED && it.food.review != null },
                "Every exact ingredient version must be published")
            check("mapping", if (safety.any { it?.mappingResolved == false }) false
                else if (safety.any { it?.mappingResolved == null }) null else true, "Unresolved mappings cannot pass")
            check("compound_ingredients", if (safety.any { it?.compoundIngredientsComplete == false }) false
                else if (safety.any { it?.compoundIngredientsComplete == null }) null else true,
                "Hidden/compound ingredients must be reviewed")
            val allergyData = listOf(r.allergens) + safety.flatMap { listOf(it?.food?.allergens, it?.crossContactAllergens) }
            val knownAllergens = allergyData.filterNotNull().flatten().toSet()
            check("allergens", when {
                p.allergies == null -> null
                p.allergies.any { it in knownAllergens } -> false
                allergyData.any { it == null } -> null
                else -> true
            }, "Allergens and cross contact must be known; wheat and gluten remain distinct",
                knownAllergens.sorted().joinToString(), p.allergies?.sorted()?.joinToString())
            check("disliked_food", r.ingredients.none { it.food.id in p.dislikedFoodIds }, "Ingredient dislikes are retained")
            check("dietary_restrictions", if (p.dietaryRestrictions.isEmpty()) true
                else if (safety.any { it?.dietaryTags == null }) null
                else safety.all { it!!.dietaryTags!!.containsAll(p.dietaryRestrictions) },
                "Every dietary restriction needs explicit reviewed compatibility")
            val region = p.budget.region
            check("region", if (region == null || safety.any { it?.availableRegions == null }) null
                else safety.all { region in it!!.availableRegions!! }, "Availability uses shopping region, not cuisine")
            check("equipment", if (r.equipment.isEmpty()) true else q.availableEquipment?.containsAll(r.equipment),
                "Do not infer equipment from missing onboarding answers")
            check("time", p.maxPreparationMinutes?.let { limit -> r.time.totalMinutes?.let { it <= limit } },
                "Unknown total time cannot satisfy a time limit", r.time.totalMinutes?.toString(),
                p.maxPreparationMinutes?.toString(), "minute")
            val range = feasiblePortion(r.portionRange, q.portion)
            check("portion", if (r.portionRange == null) null else range != null,
                "Portion must lie on the recipe grid within both intervals", range?.toString(), q.portion?.toString(), "g")
            if (results.drop(start).all { it.status == RuleStatus.PASS || it.status == RuleStatus.NOT_APPLICABLE }) {
                accepted += RecipeCandidate(r.ref, requireNotNull(range))
            }
        }
        if (accepted.isNotEmpty()) return finish(CandidateStatus.SUCCESS, accepted)
        if (results.isEmpty()) rule("catalog_empty", "catalog", RuleStatus.FAIL, "No recipe candidates exist")
        return finish(if (results.any { it.status == RuleStatus.UNKNOWN }) CandidateStatus.INCOMPLETE_DATA
            else CandidateStatus.NO_FEASIBLE_PLAN)
    }

    /** Request step is not a second grid: recipe step defines legal portions. */
    private fun feasiblePortion(recipe: PortionRange?, requested: PortionRange?): PortionRange? {
        recipe ?: return null
        val low = maxOf(recipe.minGrams, requested?.minGrams ?: recipe.minGrams)
        val high = minOf(recipe.maxGrams, requested?.maxGrams ?: recipe.maxGrams)
        if (low > high) return null
        val first = recipe.minGrams + ceil((low - recipe.minGrams) / recipe.stepGrams) * recipe.stepGrams
        val last = recipe.minGrams + floor((high - recipe.minGrams) / recipe.stepGrams) * recipe.stepGrams
        return if (first <= last && first.isFinite() && last.isFinite()) PortionRange(first, last, recipe.stepGrams) else null
    }
}
