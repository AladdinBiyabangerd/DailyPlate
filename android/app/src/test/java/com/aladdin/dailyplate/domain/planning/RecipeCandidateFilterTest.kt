package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.*
import com.aladdin.dailyplate.domain.policy.*
import org.junit.Assert.*
import org.junit.Test

class RecipeCandidateFilterTest {
    private val profile = eligibleProfile().copy(maxPreparationMinutes = 15)
    private val policy = policyFixture()
    private val recipe = editorialRecipe().recipe.copy(portionRange = PortionRange(100.0, 200.0, 25.0),
        time = PreparationTime(15, 10, 5))
    private val metadata = completeMetadata
    private val workflow = EditorialWorkflow(testRoles)
    private fun published(r: Recipe = recipe): EditorialSnapshot {
        var s = workflow.create(EditorialContent.RecipeContent(r), metadata, "author", policyTime, "TEST")
        for ((action, actor) in listOf(EditorialAction.SUBMIT to "author", EditorialAction.APPROVE to "reviewer",
            EditorialAction.PUBLISH to "publisher")) s = workflow.apply(s, action, actor, policyTime, "TEST")
        return s
    }
    private val food = Food(recipe.ingredients.single().food, LocalizedText("TEST"), emptyList(), "TEST", "TEST",
        "raw", recipe.nutrientsTotal, emptySet(), emptySet(), "TEST", Review("TEST", policyTime, "TEST"), ContentStatus.PUBLISHED)
    private val safety = IngredientSafety(food, true, true, emptySet(), setOf("gluten_free"), setOf("AZ"))
    private fun catalog(s: IngredientSafety = safety, recipes: List<EditorialSnapshot> = listOf(published())) =
        CandidateCatalog(VersionRef("TEST-catalog", 1), recipes, mapOf(food.ref to s))
    private fun request() = CandidateRequest(profile.ref, policy.ref, VersionRef("TEST-catalog", 1), policyTime,
        setOf("pot"), PortionRange(110.0, 180.0, 1.0))
    private fun run(p: UserProfile = profile, c: CandidateCatalog = catalog(), q: CandidateRequest = request()) =
        RecipeCandidateFilter.filterTest(q, p, policy, c)
    private fun blocked(result: CandidateResult, rule: String, status: RuleStatus) {
        assertTrue(result.candidates.isEmpty())
        assertTrue(result.unmetConstraints.any { it.ruleId == rule && it.status == status && it.reason.isNotBlank() })
    }

    @Test fun currentEnergySuccessOrHealthFailureNeverOpensProductionMenuGate() {
        val current = profile.copy(activityCode = "inactive", activityModel = "nasem-2023-four-category",
            measurementsConfirmed = true, pregnant = false, breastfeeding = false,
            eligibilityAnswers = NutritionHealthQuestion.entries.associate { it.name.lowercase() to false })
        assertNotNull(NutritionPolicyEngine.assessCurrent(current, policyTime).dailyKcal)
        for (p in listOf(current, current.copy(eligibilityAnswers = current.eligibilityAnswers + ("diabetes" to true)))) {
            val result = RecipeCandidateFilter.filter(request(), p, policy, catalog())
            assertEquals(CandidateStatus.POLICY_UNAVAILABLE, result.status)
            assertTrue(result.candidates.isEmpty())
            assertTrue(result.ruleResults.any { it.ruleId == "PRODUCTION_APPROVAL_REQUIRED" })
        }
    }

    @Test fun acceptsOnlyRecipeGridInsideIntersectionAndRetainsVersions() {
        val r = run()
        assertEquals(CandidateStatus.SUCCESS, r.status)
        assertTrue(r.testOnly)
        assertEquals(RecipeCandidate(recipe.ref, PortionRange(125.0, 175.0, 25.0)), r.candidates.single())
        assertTrue(r.ruleResults.all { it.policy == policy.ref })
    }
    @Test fun productionNeverAcceptsTestPublicationOrActiveTestPolicy() {
        val r = RecipeCandidateFilter.filter(request(), profile, policy, catalog())
        assertEquals(CandidateStatus.POLICY_UNAVAILABLE, r.status)
        assertFalse(r.testOnly)
        assertTrue(r.candidates.isEmpty())
    }
    @Test fun milkCheeseAndCheapAlternativeRemainBlocked() {
        val c = catalog(safety.copy(food = food.copy(allergens = setOf("milk"))))
        for (tier in BudgetTier.entries) blocked(run(profile.copy(allergies = setOf("milk"),
            budget = profile.budget.copy(tier = tier)), c), "allergens", RuleStatus.FAIL)
    }
    @Test fun unknownAllergenOrMissingOffTagDoesNotMeanSafe() {
        blocked(run(c = catalog(safety.copy(food = food.copy(allergens = null)))), "allergens", RuleStatus.UNKNOWN)
        blocked(run(c = catalog(safety.copy(crossContactAllergens = null))), "allergens", RuleStatus.UNKNOWN)
        blocked(run(profile.copy(allergies = null)), "allergens", RuleStatus.UNKNOWN)
    }
    @Test fun crossContactIsNotAnIngredientFreePass() {
        blocked(run(profile.copy(allergies = setOf("milk")), catalog(safety.copy(crossContactAllergens = setOf("milk")))),
            "allergens", RuleStatus.FAIL)
    }
    @Test fun wheatAndGlutenAreDistinctAndGlutenRestrictionNeedsEvidence() {
        val c = catalog(safety.copy(food = food.copy(allergens = setOf("gluten"))))
        assertEquals(CandidateStatus.SUCCESS, run(profile.copy(allergies = setOf("wheat")), c).status)
        blocked(run(profile.copy(allergies = setOf("gluten")), c), "allergens", RuleStatus.FAIL)
        blocked(run(profile.copy(dietaryRestrictions = setOf("gluten_free")),
            catalog(safety.copy(dietaryTags = emptySet()))), "dietary_restrictions", RuleStatus.FAIL)
    }
    @Test fun unmappedCompoundOrMissingIngredientIsBlocked() {
        blocked(run(c = catalog(safety.copy(mappingResolved = false))), "mapping", RuleStatus.FAIL)
        blocked(run(c = catalog(safety.copy(compoundIngredientsComplete = null))), "compound_ingredients", RuleStatus.UNKNOWN)
        blocked(run(c = catalog().copy(ingredients = emptyMap())), "mapping", RuleStatus.UNKNOWN)
        blocked(run(c = catalog(safety.copy(food = food.copy(status = ContentStatus.WITHDRAWN)))), "ingredient_publication", RuleStatus.FAIL)
    }
    @Test fun draftAndArchivedRecipesNeverPass() {
        for (stage in listOf(EditorialStage.DRAFT, EditorialStage.ARCHIVED)) {
            blocked(run(c = catalog(recipes = listOf(published().copy(stage = stage)))), "publication", RuleStatus.FAIL)
        }
    }
    @Test fun totalTimeBoundaryUnknownAndWaitingAreChecked() {
        assertEquals(CandidateStatus.SUCCESS, run().status)
        blocked(run(c = catalog(recipes = listOf(published(recipe.copy(time = PreparationTime(16, 1, 15)))))), "time", RuleStatus.FAIL)
        // Bypass publication fixture so the unknown time reaches the independent hard check.
        val s = published().copy(content = EditorialContent.RecipeContent(recipe.copy(time = PreparationTime(null, 1, 1))))
        blocked(run(c = catalog(recipes = listOf(s))), "time", RuleStatus.UNKNOWN)
    }
    @Test fun regionEquipmentAndDislikesAreNeverSilentlyRelaxed() {
        blocked(run(c = catalog(safety.copy(availableRegions = setOf("OTHER")))), "region", RuleStatus.FAIL)
        blocked(run(c = catalog(safety.copy(availableRegions = null))), "region", RuleStatus.UNKNOWN)
        blocked(run(q = request().copy(availableEquipment = null)), "equipment", RuleStatus.UNKNOWN)
        blocked(run(q = request().copy(availableEquipment = emptySet())), "equipment", RuleStatus.FAIL)
        blocked(run(profile.copy(dislikedFoodIds = setOf(food.ref.id))), "disliked_food", RuleStatus.FAIL)
        blocked(run(profile.copy(dislikedRecipeIds = setOf(recipe.ref.id))), "disliked_recipe", RuleStatus.FAIL)
    }
    @Test fun portionNoGridPointAndExactBoundary() {
        blocked(run(q = request().copy(portion = PortionRange(101.0, 124.0, 1.0))), "portion", RuleStatus.FAIL)
        assertEquals(PortionRange(125.0, 125.0, 25.0), run(q = request().copy(portion = PortionRange(125.0, 125.0, 1.0))).candidates.single().portion)
        val s = published().copy(content = EditorialContent.RecipeContent(recipe.copy(portionRange = null)))
        blocked(run(c = catalog(recipes = listOf(s))), "portion", RuleStatus.UNKNOWN)
    }
    @Test fun staleInputsEmptyCatalogAndDeterministicOrder() {
        assertEquals(CandidateStatus.STALE_INPUT, run(q = request().copy(profile = VersionRef(profile.ref.id, 2))).status)
        assertEquals(CandidateStatus.NO_FEASIBLE_PLAN, run(c = catalog(recipes = emptyList())).status)
        val a = published(recipe.copy(ref = VersionRef("a", 1)))
        val b = published(recipe.copy(ref = VersionRef("b", 1)))
        assertEquals(run(c = catalog(recipes = listOf(a, b))), run(c = catalog(recipes = listOf(b, a))))
    }
    @Test fun clinicalExclusionAndSpecialistNeverAuthorizeMenus() {
        assertEquals(CandidateStatus.POLICY_UNAVAILABLE, run(profile.copy(ageYears = 17)).status)
        assertEquals(CandidateStatus.POLICY_UNAVAILABLE, run(profile.copy(specialistTargetKcal = 2000.0)).status)
    }
}
