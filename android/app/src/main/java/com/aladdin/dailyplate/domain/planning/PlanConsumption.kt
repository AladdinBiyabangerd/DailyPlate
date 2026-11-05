package com.aladdin.dailyplate.domain.planning

import com.aladdin.dailyplate.domain.*
import kotlinx.serialization.Serializable

/** A confirmed personal portion, distinct from the prepared batch. No write on construction. */
@Serializable
data class PlanConsumption(
    val plan: VersionRef, val itemId: String, val consumedServings: Double, val log: FoodLog,
) {
    init {
        require(itemId.isNotBlank())
        require(consumedServings.isFinite() && consumedServings > 0)
        require(log.ref.version == 1L && !log.deleted && log.entryMethod == EntryMethod.PLAN)
        require(log.sourcePlanItemId == itemId && log.actualRecipeSnapshot != null)
        require(log.recipeChangedAnswer != null)
    }

    fun applyTo(current: MealPlan): MealPlan {
        require(current.ref == plan && current.userId == log.userId) { "Stale or foreign plan" }
        val item = current.items.single { it.id == itemId }
        require(item.status == PlanItemStatus.PLANNED || item.status == PlanItemStatus.PARTIALLY_CONSUMED)
        require(consumedServings <= item.remainingPlannedServings)
        require(log.ref.id !in item.consumedLogIds)
        require(log.recipe == item.recipe)
        if (log.recipeChangedAnswer == false) {
            require(log.actualRecipeSnapshot?.customization == item.customization)
            require(log.nutrientSnapshot == WeeklyNutrition.scale(item.nutrientSnapshot,
                consumedServings / item.plannedServings)) { "Confirmed intake must match the served plan portion" }
            require(log.confirmedPortionGrams == item.portionGrams?.times(consumedServings / item.plannedServings))
        }
        val remaining = item.remainingPlannedServings - consumedServings
        val nextVersion = Math.addExact(current.ref.version, 1)
        val nextItem = item.copy(remainingPlannedServings = remaining,
            status = if (remaining == 0.0) PlanItemStatus.CONSUMED else PlanItemStatus.PARTIALLY_CONSUMED,
            consumedLogIds = item.consumedLogIds + log.ref.id)
        return current.copy(ref = current.ref.copy(version = nextVersion),
            items = current.items.map { if (it.id == itemId) nextItem else it },
            shopping = current.shopping.map { it.copy(planVersion = nextVersion) }, syncStatus = SyncStatus.LOCAL)
    }
}
