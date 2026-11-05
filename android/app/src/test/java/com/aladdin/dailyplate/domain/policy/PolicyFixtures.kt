package com.aladdin.dailyplate.domain.policy

import com.aladdin.dailyplate.domain.*

internal val policyTime = RecordedAt("2026-11-05T10:00:00Z", "Asia/Baku")

/** Deliberately synthetic numbers: not clinical recommendations or a production policy. */
internal fun policyFixture(): NutritionPolicy {
    val description = PolicyRule("TEST_DESCRIPTION", mapOf("scope" to "test only"), listOf("TEST source"))
    return NutritionPolicy(
        VersionRef("TEST-policy", 1), "TEST adults only", listOf(description),
        NutritionPolicyEngine.MIFFLIN_ST_JEOR_V1, FormulaInput.entries.map { it.name }.toSet(),
        mapOf("sedentary" to 1.25, "some" to 1.5, "active" to 1.75),
        Goal.entries.associateWith { description }, listOf(description), listOf(description),
        listOf(description), listOf(description), listOf("TEST source"),
        Review("TEST-reviewer", policyTime, "TEST-audit"), policyTime, PolicyStatus.ACTIVE,
        PolicyExecutionRules(1, AgeScope(18, 70), FormulaInput.entries.toSet(),
            PolicyExecutionRules.HEALTH_ANSWER_KEYS,
            mapOf(Goal.LOSE to -200.0, Goal.MAINTAIN to 0.0, Goal.GAIN to 300.0, Goal.HEALTHY to 0.0),
            DailyEnergyBounds(100.0, 10000.0),
            mapOf("protein" to MacroRule(MacroBasis.GRAMS_PER_KG, 1.0),
                "fat" to MacroRule(MacroBasis.ENERGY_FRACTION, 0.25, 9.0),
                "carbohydrate" to MacroRule(MacroBasis.ENERGY_FRACTION, 0.5, 4.0)),
            listOf(reference("iron", CalculationSex.MALE, 8.0),
                reference("iron", CalculationSex.FEMALE, 18.0),
                reference("calcium", null, 1000.0)),
            180, listOf("TEST reference tables, not approved")),
    )
}

internal fun reference(nutrient: String, sex: CalculationSex?, value: Double) = NutrientReferenceRow(
    nutrient, AgeScope(18, 70), sex, LifeStage.ADULT, NutrientAmount(value, NutrientUnit.MILLIGRAM),
    VersionRef("TEST-$nutrient-table", 2), "TEST row $sex",
)

internal fun eligibleProfile() = UserProfile(
    VersionRef("profile", 1), "guest", policyTime, Goal.MAINTAIN, 30, CalculationSex.MALE,
    180.0, 80.0, "some", null, null,
    PolicyExecutionRules.HEALTH_ANSWER_KEYS.associateWith { false },
    emptySet(), emptySet(), emptySet(), emptySet(), 30, emptySet(),
    Budget(BudgetTier.MEDIUM, null, "AZ", 1, emptySet()), emptyList(), 3, null, "az",
    CalendarDate("2026-11-05"),
)
