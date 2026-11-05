package com.aladdin.dailyplate.domain.profile

import com.aladdin.dailyplate.domain.*
import kotlinx.serialization.Serializable

/** K16 must compute both aggregates and display differences before requesting acceptance.
 * Constructing or accepting this contract performs no menu write in K09.
 */
@Serializable
data class FuturePlanPreview(
    val request: FuturePlanRecalculation, val before: MealPlan, val after: MealPlan,
    val effectiveFrom: CalendarDate, val explanation: String,
) {
    init {
        require(before.userId == after.userId && before.ref.id == after.ref.id)
        require(after.ref.version == before.ref.version + 1)
        require(after.userProfile == request.toProfile && explanation.isNotBlank())
        val oldIds = before.items.map { it.id }.toSet()
        before.items.forEach { old ->
            val next = after.items.singleOrNull { it.id == old.id }
            val protected = old.date.iso < effectiveFrom.iso || old.consumedLogIds.isNotEmpty() ||
                old.status != PlanItemStatus.PLANNED || old.remainingPlannedServings != old.plannedServings
            if (protected) require(next == old) { "Past/consumed items are immutable" }
            else if (next != null) {
                require(next.date == old.date && next.mealSlot == old.mealSlot)
                require(next.status == old.status && next.consumedLogIds == old.consumedLogIds)
            }
        }
        // Meal-count edits may add/remove future unconsumed slots, never actual intake.
        after.items.filter { it.id !in oldIds }.forEach { added ->
            require(added.date.iso >= effectiveFrom.iso && added.status == PlanItemStatus.PLANNED)
            require(added.consumedLogIds.isEmpty() && added.remainingPlannedServings == added.plannedServings)
        }
    }

    /** Explicit acceptance binds to the exact preview and current revisions; no silent refresh. */
    fun accept(currentPlan: VersionRef, currentProfile: VersionRef): FuturePlanAcceptance {
        require(currentPlan == before.ref && currentProfile == request.toProfile) { "Stale future-plan preview" }
        return FuturePlanAcceptance(this)
    }
}

@Serializable
data class FuturePlanAcceptance(val preview: FuturePlanPreview)
