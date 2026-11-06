package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.*
import com.aladdin.dailyplate.domain.policy.*
import com.aladdin.dailyplate.domain.pricing.*
import java.time.Duration
import org.junit.Assert.*
import org.junit.Test

class WeeklyPlanGeneratorTest : WeeklyPlanningFixture() {
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
        assertEquals(VersionRef("nasem-2023-adult-eer", 1), r.nutritionPreview.energy.contract.formula)
        assertEquals(data().coverage, r.coverage)
        assertEquals(r.meals.sumOf { it.portionGrams } * 2,
            r.weeklyNutrients.values.getValue("energy_kcal").value!!, 1e-8)
        for (alternative in r.alternatives) {
            val original = r.meals.single { it.day == alternative.day && it.slot == alternative.slot }
            assertNotEquals(original.recipe, alternative.recipe)
            assertTrue(alternative.portionGrams in 100.0..600.0 && (alternative.portionGrams - 100) % 25 == 0.0)
            val dayGrams = r.meals.filter { it.day == alternative.day }.sumOf { it.portionGrams } -
                original.portionGrams + alternative.portionGrams
            assertTrue(r.nutritionPreview.bounds!!.contains(dayGrams * 2))
        }
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
    @Test fun widerCoverageAndProteinQualityRemainHardWithoutEvidence() {
        val d = data()
        for (incomplete in listOf(d.copy(coverage = null),
            d.copy(evidence = d.evidence.mapValues { (_, e) -> e.copy(proteinQualityReviewed = null) }),
            d.copy(coverage = d.coverage!!.copy(policy = VersionRef(policy.ref.id, 2))))) {
            val r = run(d = incomplete, q = request(nodes = 200))
            assertNull(r.plan)
            assertTrue(r.ruleResults.any { it.ruleId == "OTHER_NUTRIENTS_AND_PROTEIN_QUALITY" && it.status != RuleStatus.PASS })
        }
    }
    @Test fun whoOperatorsUseExactBoundariesAndUnknownNeverBecomesZero() {
        val d = data()
        val p = profile()
        val day = run(p, d).plan!!.meals.take(3).mapIndexed { i, meal -> meal.copy(nutrients = NutrientSnapshot(
            meal.nutrients.values + mapOf(
                "energy_kcal" to NutrientAmount(900.0, NutrientUnit.KCAL),
                "free_sugar_g" to NutrientAmount(22.5, NutrientUnit.GRAM),
                "saturated_fat_g" to NutrientAmount(10.0, NutrientUnit.GRAM),
                "trans_fat_g" to NutrientAmount(1.0, NutrientUnit.GRAM),
                "sodium_mg" to NutrientAmount(if (i == 0) 2000.0 else 0.0, NutrientUnit.MILLIGRAM)))) }
        val recipes = d.catalog.recipes.associate { it.content.ref to (it.content as EditorialContent.RecipeContent).recipe }
        fun check(meals: List<GeneratedMeal>) = WeeklyNutrition.check(meals, recipes, d,
            NutritionPolicyEngine.assessCurrent(p, policyTime).currentCalculation!!, policy.ref, "test")
        val exact = check(day).associateBy { it.ruleId }
        assertEquals(RuleStatus.FAIL, exact.getValue("free_sugar").status)
        assertEquals(RuleStatus.FAIL, exact.getValue("salt_from_sodium").status)
        assertEquals(RuleStatus.PASS, exact.getValue("saturated_fat").status)
        assertEquals(RuleStatus.PASS, exact.getValue("trans_fat").status)
        val below = day.map { m -> m.copy(nutrients = NutrientSnapshot(m.nutrients.values.mapValues { (key, value) ->
            if (key in setOf("free_sugar_g", "sodium_mg")) value.copy(value = value.value!! * 0.999) else value })) }
        assertEquals(RuleStatus.PASS, check(below).single { it.ruleId == "free_sugar" }.status)
        assertEquals(RuleStatus.PASS, check(below).single { it.ruleId == "salt_from_sodium" }.status)
        val missing = day.map { it.copy(nutrients = NutrientSnapshot(it.nutrients.values - "free_sugar_g")) }
        assertEquals(RuleStatus.UNKNOWN, check(missing).single { it.ruleId == "free_sugar" }.status)
    }
    @Test fun weeklyReferencesCannotBeSubstitutedByDailyOrIgnored() {
        val d = data()
        val impossible = d.coverage!!.copy(rules = d.coverage.rules.map {
            if (it.window == PlanningWindow.WEEK) it.copy(threshold = NutrientAmount(10000.0, NutrientUnit.GRAM)) else it })
        val r = run(d = d.copy(coverage = impossible), q = request(nodes = 200))
        assertNull(r.plan)
        assertTrue(r.ruleResults.any { it.ruleId == "TEST-fibre-week" && it.scope == "week" && it.status == RuleStatus.FAIL })
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
