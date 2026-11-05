package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.*
import com.aladdin.dailyplate.domain.policy.*
import com.aladdin.dailyplate.domain.pricing.*
import java.time.Duration
import org.junit.Assert.*
import org.junit.Test

class WeeklyPlanGeneratorTest {
    private val policy = policyFixture()
    private fun profile(goal: Goal = Goal.MAINTAIN, slots: Int = 3, tier: BudgetTier = BudgetTier.MEDIUM, time: Int = 15) =
        eligibleProfile().copy(goal = goal, heightCm = 175.0, weightKg = if (goal == Goal.LOSE) 85.0 else 70.0,
            activityCode = "inactive", activityModel = "nasem-2023-four-category", measurementsConfirmed = true,
            pregnant = false, breastfeeding = false, mealsPerDay = slots, maxPreparationMinutes = time,
            eligibilityAnswers = NutritionHealthQuestion.entries.associate { it.name.lowercase() to false },
            budget = Budget(tier, Money("1000", "AZN"), "AZ", 1, (0 until slots).map { "slot:$it" }.toSet()))
    private val snapshot = NutrientSnapshot(mapOf(
        "energy_kcal" to NutrientAmount(2000.0, NutrientUnit.KCAL),
        "protein_g" to NutrientAmount(100.0, NutrientUnit.GRAM),
        "fat_g" to NutrientAmount(200.0 / 3, NutrientUnit.GRAM),
        "available_carbohydrate_g" to NutrientAmount(250.0, NutrientUnit.GRAM),
        "fibre_g" to NutrientAmount(30.0, NutrientUnit.GRAM),
        "free_sugar_g" to NutrientAmount(5.0, NutrientUnit.GRAM),
        "saturated_fat_g" to NutrientAmount(10.0, NutrientUnit.GRAM),
        "trans_fat_g" to NutrientAmount(0.0, NutrientUnit.GRAM),
        "sodium_mg" to NutrientAmount(500.0, NutrientUnit.MILLIGRAM)))
    private fun data(count: Int = 8, nutrients: NutrientSnapshot = snapshot): WeeklyPlanningData {
        val base = editorialRecipe().recipe
        val food = Food(base.ingredients.single().food, LocalizedText("TEST"), emptyList(), "TEST", "TEST", "raw",
            snapshot, emptySet(), emptySet(), "TEST", Review("TEST", policyTime, "TEST"), ContentStatus.PUBLISHED)
        val workflow = EditorialWorkflow(testRoles)
        val recipes = (0 until count).map { i ->
            val recipe = base.copy(ref = VersionRef("TEST-recipe-$i", 1), ingredients = listOf(Ingredient(food.ref, 1000.0)),
                nutrientsTotal = nutrients, yieldCookedGrams = 1000.0, nutrientsPer100g = null,
                portionRange = PortionRange(100.0, 600.0, 25.0), time = PreparationTime(15, 10, 5))
            var envelope = workflow.create(EditorialContent.RecipeContent(recipe), completeMetadata, "author", policyTime, "TEST")
            for ((action, actor) in listOf(EditorialAction.SUBMIT to "author", EditorialAction.APPROVE to "reviewer", EditorialAction.PUBLISH to "publisher"))
                envelope = workflow.apply(envelope, action, actor, policyTime, "TEST")
            envelope
        }
        val evidence = recipes.associate { r -> r.content.ref to PlanningEvidence(r.content.ref,
            NutritionCarbohydrateBasis.AVAILABLE_GLYCAEMIC, 500.0, setOf("v${r.content.ref.id.last().digitToInt() % 3}"),
            setOf("g${r.content.ref.id.last().digitToInt() % 3}"), setOf("p${r.content.ref.id.last().digitToInt() % 3}"), listOf("TEST mapping")) }
        return WeeklyPlanningData(CandidateCatalog(VersionRef("TEST-catalog", 1), recipes,
            mapOf(food.ref to IngredientSafety(food, true, true, emptySet(), emptySet(), setOf("AZ")))), evidence,
            listOf(PriceObservation(IngredientPrice(VersionRef("TEST-price", 1), food.ref.id, "AZ", "TEST", policyTime,
                500.0, 1.0, Money("1", "AZN"), "raw", "TEST only"), true)),
            PriceFreshness(VersionRef("TEST-freshness", 1), Duration.ofDays(7)))
    }
    private fun request(p: UserProfile = profile(), nodes: Int = 10000) = PlanGenerationRequest(
        CandidateRequest(p.ref, policy.ref, VersionRef("TEST-catalog", 1), policyTime, setOf("pot")),
        CalendarDate("2026-11-05"), "Asia/Baku", p.goal, p.mealsPerDay, 42, "TEST-request",
        WeeklySearchRules(VersionRef("TEST-search", 1), nodes))
    private fun run(p: UserProfile = profile(), d: WeeklyPlanningData = data(), q: PlanGenerationRequest = request(p)) =
        WeeklyPlanGenerator.generateTest(q, p, policy, d)

    @Test fun all108CombinationsUseRealPortionsAndUnchangedTargets() {
        for (goal in Goal.entries) for (tier in BudgetTier.entries) for (slots in 3..5) for (time in listOf(15, 30, 60)) {
            val p = profile(goal, slots, tier, time)
            val r = run(p)
            assertEquals("$goal/$tier/$slots/$time ${r.unmetConstraints}", GenerationStatus.SUCCESS, r.status)
            val plan = r.plan!!
            assertTrue(r.testOnly); assertEquals(7 * slots, plan.meals.size)
            assertEquals(7, plan.meals.map { it.day }.distinct().size)
            assertEquals(NutritionPolicyEngine.assessCurrent(p, policyTime).dailyKcal!!, plan.targetKcal, 0.0)
            assertTrue(plan.meals.all { it.portionGrams in 100.0..600.0 && (it.portionGrams - 100) % 25 == 0.0 })
            assertTrue(r.ruleResults.filter { it.scope.startsWith("day:") }.let { WeeklyNutrition.hardFailures(it).isEmpty() })
            assertFalse(plan.shoppingCost.budgetComplianceVerified)
            assertTrue(plan.shoppingCost.totalCost!!.amount.toBigDecimal() <= p.budget.weeklyAmount!!.amount.toBigDecimal())
            assertEquals(plan, run(p).plan)
        }
    }
    @Test fun sameSeedVersionsAndReorderedCatalogAreDeterministic() {
        val d = data()
        assertEquals(run(d = d), run(d = d.copy(catalog = d.catalog.copy(recipes = d.catalog.recipes.reversed()))))
        val r = run().plan!!
        assertTrue(r.meals.map { it.portionGrams }.distinct().size > 1)
        for ((a, b) in r.meals.chunked(3).zipWithNext()) assertTrue(a.none { x -> b.any { it.recipe == x.recipe } })
        assertEquals(WeeklyNutrition.sum(r.dailyNutrients), r.weeklyNutrients)
        assertTrue(r.alternatives.isNotEmpty())
    }
    @Test fun missingCriticalNutrientsNeverPassAndSearchIsBounded() {
        for (key in listOf("fibre_g", "free_sugar_g", "sodium_mg", "trans_fat_g", "available_carbohydrate_g")) {
            val d = data(nutrients = snapshot.copy(values = snapshot.values - key))
            val r = run(d = d, q = request(nodes = 200))
            assertNull(r.plan); assertNotEquals(GenerationStatus.SUCCESS, r.status)
            assertTrue(r.visitedNodes <= 200)
            assertTrue(r.ruleResults.any { it.status == RuleStatus.UNKNOWN && it.scope.startsWith("day:") })
        }
    }
    @Test fun noSilentBudgetIncreaseAndUnknownScopeOrPricesAreExplicit() {
        val p = profile().let { it.copy(budget = it.budget.copy(weeklyAmount = Money("0", "AZN"))) }
        for ((profile, data) in listOf(p to data(), profile() to data().copy(prices = emptyList()),
            profile().let { it.copy(budget = it.budget.copy(coveredMealSlots = emptySet())) } to data())) {
            val r = run(profile, data, request(profile, 200))
            assertNull(r.plan); assertTrue(r.unmetConstraints.any { it.ruleId == "package_budget" })
        }
        assertEquals("0", p.budget.weeklyAmount!!.amount)
    }
    @Test fun staleEmptyWithdrawnAndProductionGates() {
        assertEquals(GenerationStatus.STALE_INPUT, run(q = request().copy(goal = Goal.HEALTHY)).status)
        assertEquals(GenerationStatus.STALE_INPUT, run(q = request().copy(candidate = request().candidate.copy(profile = VersionRef("profile", 2)))).status)
        assertEquals(GenerationStatus.NO_FEASIBLE_PLAN, run(d = data(0)).status)
        val d = data().let { it.copy(catalog = it.catalog.copy(recipes = it.catalog.recipes.map { r -> r.copy(stage = EditorialStage.ARCHIVED) })) }
        assertEquals(GenerationStatus.NO_FEASIBLE_PLAN, run(d = d).status)
        assertEquals(GenerationStatus.POLICY_UNAVAILABLE, WeeklyPlanGenerator.generate(request(), profile(), policy, data()).status)
        assertEquals(GenerationStatus.POLICY_UNAVAILABLE, run(profile().copy(ageYears = 17)).status)
        assertEquals(GenerationStatus.INCOMPLETE_DATA, run(profile().copy(dietaryRestrictions = setOf("vegan")),
            data().let { it.copy(catalog = it.catalog.copy(ingredients = it.catalog.ingredients.mapValues { (_, s) -> s.copy(dietaryTags = setOf("vegan")) })) }).status)
    }
    @Test fun zeroAlternativesAndExhaustionAreNotFabricated() {
        val d = data(2)
        val q = request().copy(search = request().search.copy(maximumRecipeOccurrences = 21, minimumSourcesPerGroup = 1))
        val r = run(d = d, q = q)
        assertEquals(GenerationStatus.SUCCESS, r.status)
        assertTrue(r.plan!!.alternatives.isEmpty())
        assertEquals(21, r.ruleResults.count { it.ruleId.startsWith("alternatives:") && it.status == RuleStatus.UNKNOWN })
        assertEquals(GenerationStatus.TEMPORARY_FAILURE, run(q = request(nodes = 1)).status)
        assertNull(run(d = data(1), q = q).plan)
    }
    @Test fun sessionIdempotencyConflictsAndCapacity() {
        val session = WeeklyPlanSession(1)
        val q = request(); val p = profile(); val d = data()
        val first = session.generateTest(q, p, policy, d)
        assertSame(first, session.generateTest(q, p, policy, d))
        assertEquals(GenerationStatus.REQUEST_CONFLICT, session.generateTest(q.copy(seed = 43), p, policy, d).status)
        assertEquals(GenerationStatus.TEMPORARY_FAILURE, session.generateTest(q.copy(requestId = "other"), p, policy, d).status)
    }
}
