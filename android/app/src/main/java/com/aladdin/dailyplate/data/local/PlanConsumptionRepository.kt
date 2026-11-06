package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.PlanConsumption
import java.util.concurrent.Callable
import kotlinx.serialization.Serializable

@Serializable
private data class ConsumptionReceipt(val command: PlanConsumption, val result: MealPlan)

/** Log, event ledger, remaining portion and durable retry receipt commit or roll back together. */
class PlanConsumptionRepository internal constructor(private val database: UserDatabase) {
    fun confirm(command: PlanConsumption): MealPlan = database.runInTransaction(Callable {
        val log = command.log
        val dao = database.snapshots()
        dao.version("plan_consumption", log.userId, log.clientEventId, 1)?.let {
            require(it.payloadSchema == 1)
            val receipt = snapshotJson.decodeFromString(ConsumptionReceipt.serializer(), it.payload)
            if (receipt.command != command) throw IdempotencyConflict("Consumption event reused with different content")
            return@Callable receipt.result
        }
        // An unrelated existing food event must not reduce the plan on retry.
        if (dao.event(log.userId, log.clientEventId) != null)
            throw IdempotencyConflict("Food event already belongs to another operation")
        val plans = MealPlanRepository(database)
        val current = requireNotNull(plans.latest(log.userId, command.plan.id))
        if (current.ref != command.plan) throw RevisionConflict("Plan changed since portion confirmation")
        val next = command.applyTo(current)
        FoodLogRepository(database).record(log)
        UserSnapshotRepository(database, "plan", MealPlan.serializer(), MealPlan::userId, MealPlan::ref)
            .save(next, current.ref.version)
        dao.insert(UserSnapshotEntity("plan_consumption", log.userId, log.clientEventId, 1,
            snapshotJson.encodeToString(ConsumptionReceipt.serializer(), ConsumptionReceipt(command, next))))
        next
    })
}
