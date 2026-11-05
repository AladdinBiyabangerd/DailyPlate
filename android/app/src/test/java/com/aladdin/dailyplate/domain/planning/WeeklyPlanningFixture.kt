package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.*
import com.aladdin.dailyplate.domain.policy.*
import com.aladdin.dailyplate.domain.pricing.*
import java.time.Duration
import org.junit.Assert.*
import org.junit.Test

abstract class WeeklyPlanningFixture {
    protected val policy = policyFixture()
    protected fun profile(goal: Goal = Goal.MAINTAIN, slots: Int = 3, tier: BudgetTier = BudgetTier.MEDIUM, time: Int = 15) =
        eligibleProfile().copy(goal = goal, heightCm = 175.0, weightKg = if (goal == Goal.LOSE) 85.0 else 70.0,
            activityCode = "inactive", activityModel = "nasem-2023-four-category", measurementsConfirmed = true,
            pregnant = false, breastfeeding = false, mealsPerDay = slots, maxPreparationMinutes = time,
            eligibilityAnswers = NutritionHealthQuestion.entries.associate { it.name.lowercase() to false },
            budget = Budget(tier, Money("1000", "AZN"), "AZ", 1, (0 until slots).map { "slot:$it" }.toSet()))
    protected val snapshot = NutrientSnapshot(mapOf(
        "energy_kcal" to NutrientAmount(2000.0, NutrientUnit.KCAL),
        "protein_g" to NutrientAmount(100.0, NutrientUnit.GRAM),
        "fat_g" to NutrientAmount(200.0 / 3, NutrientUnit.GRAM),
        "available_carbohydrate_g" to NutrientAmount(250.0, NutrientUnit.GRAM),
        "fibre_g" to NutrientAmount(30.0, NutrientUnit.GRAM),
        "free_sugar_g" to NutrientAmount(5.0, NutrientUnit.GRAM),
        "saturated_fat_g" to NutrientAmount(10.0, NutrientUnit.GRAM),
        "trans_fat_g" to NutrientAmount(0.0, NutrientUnit.GRAM),
        "sodium_mg" to NutrientAmount(500.0, NutrientUnit.MILLIGRAM)))
    protected fun data(count: Int = 8, nutrients: NutrientSnapshot = snapshot): WeeklyPlanningData {
        val base = editorialRecipe().recipe
        val food = Food(base.ingredients.single().food, LocalizedText("TEST"), emptyList(), "TEST", "TEST", "raw",
            snapshot, emptySet(), emptySet(), "TEST", Review("TEST", policyTime, "TEST"), ContentStatus.PUBLISHED)
        val workflow = EditorialWorkflow(testRoles)
        val recipes = (0 until count).map { i ->
            val recipe = base.copy(ref = VersionRef("TEST-recipe-$i", 1), ingredients = listOf(Ingredient(food.ref, 1000.0)),
                nutrientsTotal = nutrients, yieldCookedGrams = 1000.0, nutrientsPer100g = null,
                portionRange = PortionRange(100.0, 600.0, 25.0), time = PreparationTime(15, 10, 5))
            var envelope = workflow.create(EditorialContent.RecipeContent(recipe), completeMetadata.copy(yieldEvidence = "TEST measured yield"), "author", policyTime, "TEST")
            for ((action, actor) in listOf(EditorialAction.SUBMIT to "author", EditorialAction.APPROVE to "reviewer", EditorialAction.PUBLISH to "publisher"))
                envelope = workflow.apply(envelope, action, actor, policyTime, "TEST")
            envelope
        }
        val evidence = recipes.associate { r -> r.content.ref to PlanningEvidence(r.content.ref,
            NutritionCarbohydrateBasis.AVAILABLE_GLYCAEMIC, 500.0, setOf("v${r.content.ref.id.last().digitToInt() % 3}"),
            setOf("g${r.content.ref.id.last().digitToInt() % 3}"), setOf("p${r.content.ref.id.last().digitToInt() % 3}"), listOf("TEST mapping"), true) }
        return WeeklyPlanningData(CandidateCatalog(VersionRef("TEST-catalog", 1), recipes,
            mapOf(food.ref to IngredientSafety(food, true, true, emptySet(), emptySet(), setOf("AZ")))), evidence,
            listOf(PriceObservation(IngredientPrice(VersionRef("TEST-price", 1), food.ref.id, "AZ", "TEST", policyTime,
                500.0, 1.0, Money("1", "AZN"), "raw", "TEST only"), true)),
            PriceFreshness(VersionRef("TEST-freshness", 1), Duration.ofDays(7)),
            PlanningCoverage(VersionRef("TEST-coverage", 1), policy.ref, Review("TEST", policyTime, "TEST"), listOf(
                PlanningNutrientRule("TEST-fibre-week", "fibre_g", NutrientAmount(175.0, NutrientUnit.GRAM),
                    PlanningOperator.AT_LEAST, PlanningWindow.WEEK, "TEST weekly rule"),
                PlanningNutrientRule("TEST-protein-day", "protein_g", NutrientAmount(50.0, NutrientUnit.GRAM),
                    PlanningOperator.AT_LEAST, PlanningWindow.DAY, "TEST daily rule"))))
    }
    protected fun request(p: UserProfile = profile(), nodes: Int = 10000) = PlanGenerationRequest(
        CandidateRequest(p.ref, policy.ref, VersionRef("TEST-catalog", 1), policyTime, setOf("pot")),
        CalendarDate("2026-11-05"), "Asia/Baku", p.goal, p.mealsPerDay, 42, "TEST-request",
        WeeklySearchRules(VersionRef("TEST-search", 1), nodes))
    protected fun run(p: UserProfile = profile(), d: WeeklyPlanningData = data(), q: PlanGenerationRequest = request(p)) =
        WeeklyPlanGenerator.generateTest(q, p, policy, d)

}
