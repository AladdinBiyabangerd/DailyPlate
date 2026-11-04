package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.MealPlan
import com.aladdin.dailyplate.domain.PlanChange
import com.aladdin.dailyplate.domain.VersionRef
import java.util.concurrent.Callable

/** Persistence boundary only: callers must compute/validate the preview before committing it. */
class MealPlanRepository internal constructor(private val database: UserDatabase) {
    private val history = UserSnapshotRepository(database, "plan", MealPlan.serializer(), MealPlan::userId, MealPlan::ref)

    fun latest(userId: String, id: String): MealPlan? = history.latest(userId, id)
    fun version(userId: String, ref: VersionRef): MealPlan? = history.version(userId, ref)

    fun create(plan: MealPlan): MealPlan {
        require(plan.ref.version == 1L)
        return history.save(plan, 0)
    }

    fun change(userId: String, operationId: String): PlanChange? =
        database.snapshots().version("plan_change", userId, operationId, 1)?.let {
            require(it.payloadSchema == 1) { "Unsupported plan change payload schema" }
            snapshotJson.decodeFromString(PlanChange.serializer(), it.payload)
        }

    fun apply(change: PlanChange, next: MealPlan): MealPlan = database.runInTransaction(Callable {
        require(change.userId == next.userId && change.planId == next.ref.id)
        require(next.ref.version == Math.addExact(change.expectedPlanVersion, 1))
        change(change.userId, change.operationId)?.let { existing ->
            val previousResult = version(next.userId, next.ref)
            if (existing != change || previousResult != next) {
                throw IdempotencyConflict("Plan operation reused with different content")
            }
            return@Callable requireNotNull(previousResult)
        }
        val current = requireNotNull(latest(next.userId, next.ref.id))
        if (current.ref.version != change.expectedPlanVersion) throw RevisionConflict("Plan changed since preview")
        val affected = change.affectedItemIds
        require(current.items.filter { it.id in affected }.sortedBy { it.id } == change.before.sortedBy { it.id })
        require(next.items.filter { it.id in affected }.sortedBy { it.id } == change.after.sortedBy { it.id })
        require(current.items.filter { it.id !in affected } == next.items.filter { it.id !in affected })
        require(current.copy(
            ref = next.ref, items = next.items, shopping = next.shopping,
            nutrientSnapshot = next.nutrientSnapshot, constraintResults = next.constraintResults,
            alternatives = next.alternatives, syncStatus = next.syncStatus,
        ) == next) { "A meal change cannot replace profile, policy, week or energy target" }
        history.save(next, change.expectedPlanVersion)
        database.snapshots().insert(UserSnapshotEntity(
            "plan_change", change.userId, change.operationId, 1,
            snapshotJson.encodeToString(PlanChange.serializer(), change),
        ))
        next
    })
}
