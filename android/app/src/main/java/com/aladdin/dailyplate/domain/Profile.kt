package com.aladdin.dailyplate.domain

import kotlinx.serialization.Serializable

@Serializable
enum class Goal { LOSE, MAINTAIN, GAIN, HEALTHY }
@Serializable
enum class CalculationSex { MALE, FEMALE }
@Serializable
enum class BudgetTier { ECONOMICAL, MEDIUM, FLEXIBLE }

/** Decimal string preserves money without binary floating-point rounding. */
@Serializable
data class Money(val amount: String, val currency: String) {
    init {
        require(amount.toBigDecimal().signum() >= 0)
        require(currency.matches(Regex("[A-Z]{3}")))
    }
}

@Serializable
data class Budget(
    val tier: BudgetTier?, val weeklyAmount: Money?, val region: String?,
    val people: Int, val coveredMealSlots: Set<String>,
) { init { require(people > 0) } }

@Serializable
data class HomeFood(val food: VersionRef, val preparationState: String, val quantity: Double, val unit: String) {
    init { nonNegative(quantity) }
}

@Serializable
data class UserProfile(
    val ref: VersionRef, val userId: String, val updatedAt: RecordedAt,
    val goal: Goal, val ageYears: Int?, val calculationSex: CalculationSex?,
    val heightCm: Double?, val weightKg: Double?, val activityCode: String?,
    val pregnant: Boolean?, val breastfeeding: Boolean?, val eligibilityAnswers: Map<String, Boolean?>,
    val allergies: Set<String>?, val dietaryRestrictions: Set<String>,
    val dislikedFoodIds: Set<String>, val dislikedRecipeIds: Set<String>,
    val maxPreparationMinutes: Int?, val equipment: Set<String>,
    val budget: Budget, val homeFoods: List<HomeFood>, val mealsPerDay: Int,
    val specialistTargetKcal: Double?, val language: String,
) {
    init {
        require(userId.isNotBlank())
        ageYears?.let { require(it > 0) }
        heightCm?.let(::positive); weightKg?.let(::positive)
        maxPreparationMinutes?.let { require(it > 0) }
        specialistTargetKcal?.let(::positive)
        require(mealsPerDay in 3..5)
    }
}
