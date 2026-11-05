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
        require(before.items.map { it.id }.toSet() == after.items.map { it.id }.toSet())
        before.items.forEach { old ->
            val next = after.items.single { it.id == old.id }
            require(next.date == old.date && next.mealSlot == old.mealSlot)
            require(next.status == old.status && next.consumedLogIds == old.consumedLogIds)
        }
        before.items.filter { it.date.iso < effectiveFrom.iso || it.consumedLogIds.isNotEmpty() ||
            it.status != PlanItemStatus.PLANNED }.forEach { old ->
            require(after.items.single { it.id == old.id } == old) { "Past/consumed items are immutable" }
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
