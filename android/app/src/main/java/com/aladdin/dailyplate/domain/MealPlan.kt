package com.aladdin.dailyplate.domain

import kotlinx.serialization.Serializable

@Serializable
enum class PlanItemStatus { PLANNED, PARTIALLY_CONSUMED, CONSUMED, SKIPPED }

@Serializable
data class MealPlanItem(
    val id: String, val date: CalendarDate, val mealSlot: String,
    val recipe: VersionRef, val customization: VersionRef?, val portionGrams: Double?,
    val plannedServings: Double, val remainingPlannedServings: Double,
    val nutrientSnapshot: NutrientSnapshot, val status: PlanItemStatus, val consumedLogIds: Set<String>,
) {
    init {
        require(id.isNotBlank()); require(mealSlot.isNotBlank()); positive(plannedServings)
        nonNegative(remainingPlannedServings); require(remainingPlannedServings <= plannedServings)
        portionGrams?.let(::positive)
    }
}

@Serializable
data class MealAlternative(val itemId: String, val recipe: VersionRef, val reason: String)

/** Plan + shopping are one persisted aggregate so changes cannot tear across tables. */
@Serializable
data class MealPlan(
    val ref: VersionRef, val userId: String, val userProfile: VersionRef,
    val week: CalendarDate, val timezone: String, val policy: VersionRef?,
    val energyEstimate: EnergyEstimate?, val items: List<MealPlanItem>,
    val nutrientSnapshot: NutrientSnapshot, val constraintResults: List<ConstraintResult>,
    val alternatives: List<MealAlternative>, val shopping: List<ShoppingItem>,
    val syncStatus: SyncStatus,
) {
    init {
        java.time.ZoneId.of(timezone)
        require(items.map { it.id }.distinct().size == items.size)
        require(shopping.all { it.planVersion == ref.version })
    }
}

@Serializable
enum class PlanChangeType { EDIT_INGREDIENTS, REPLACE, SWAP, MOVE, UNDO }

@Serializable
data class ShoppingDelta(val foodId: String, val preparationState: String, val quantityDelta: Double, val unit: String) {
    init { require(quantityDelta.isFinite()) }
}

@Serializable
data class PlanChange(
    val operationId: String, val userId: String, val planId: String,
    val type: PlanChangeType, val expectedPlanVersion: Long, val affectedItemIds: Set<String>,
    val before: List<MealPlanItem>, val after: List<MealPlanItem>,
    val validationResults: List<ConstraintResult>, val shoppingDelta: List<ShoppingDelta>,
    val createdAt: RecordedAt, val syncStatus: SyncStatus,
) {
    init {
        require(operationId.isNotBlank()); require(expectedPlanVersion > 0)
        require(affectedItemIds.isNotEmpty())
        require(before.map { it.id }.toSet() == affectedItemIds)
        require(after.map { it.id }.toSet() == affectedItemIds)
    }
}
