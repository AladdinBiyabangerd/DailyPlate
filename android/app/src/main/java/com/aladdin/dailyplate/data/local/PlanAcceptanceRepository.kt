package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.*
import java.util.concurrent.Callable
import kotlinx.serialization.Serializable

/** One immutable acceptance per generation request. The generation envelope retains complete
 * catalog/coverage/pricing provenance; the initial plan remains readable after later edits. */
@Serializable
data class PlanAcceptance(val userId: String, val requestId: String, val plan: VersionRef)

/** Explicit acceptance only. Never accepts caller-supplied meals and never writes intake.
 * Recalculation of an existing plan is deliberately a separate preview/change operation. */
class PlanAcceptanceRepository internal constructor(private val database: UserDatabase) {
    private val generation = PlanGenerationRepository(database)
    private val plans = MealPlanRepository(database)
    private val profiles = UserSnapshotRepository(database, "profile", UserProfile.serializer(), UserProfile::userId, UserProfile::ref)

    fun receipt(userId: String, requestId: String): PlanAcceptance? =
        database.snapshots().version("plan_acceptance", userId, requestId, 1)?.let {
            require(it.payloadSchema == 1)
            snapshotJson.decodeFromString(PlanAcceptance.serializer(), it.payload)
        }

    fun accept(userId: String, requestId: String, planId: String): MealPlan = execute(userId, requestId, planId, false)
    internal fun acceptTest(userId: String, requestId: String, planId: String): MealPlan = execute(userId, requestId, planId, true)

    private fun execute(userId: String, requestId: String, planId: String, test: Boolean): MealPlan =
        database.runInTransaction(Callable {
            require(userId.isNotBlank() && requestId.isNotBlank() && planId.isNotBlank())
            val envelope = requireNotNull(generation.receipt(userId, requestId)) { "Generation receipt missing" }
            require(envelope.result.testOnly == test) { "Fixture results cannot be accepted in production" }
            receipt(userId, requestId)?.let {
                if (it.plan.id != planId) throw IdempotencyConflict("Generation already accepted under another plan ID")
                return@Callable requireNotNull(plans.version(userId, it.plan))
            }
            require(envelope.result.status == GenerationStatus.SUCCESS) { "Only successful generation can be accepted" }
            if (profiles.latest(userId, envelope.profile.ref.id) != envelope.profile ||
                generation.currentInputs(userId) != envelope.inputs) {
                throw RevisionConflict("Planning inputs changed since generation; generate a new request")
            }
            val week = requireNotNull(envelope.result.plan)
            require(week.request == envelope.request)
            val ref = VersionRef(planId, 1)
            if (plans.latest(userId, planId) != null) throw RevisionConflict("Acceptance requires a new plan ID")
            val plan = GeneratedMealPlan.create(userId, ref, envelope.profile, envelope.result)
            plans.create(plan)
            val acceptance = PlanAcceptance(userId, requestId, ref)
            database.snapshots().insert(UserSnapshotEntity("plan_acceptance", userId, requestId, 1,
                snapshotJson.encodeToString(PlanAcceptance.serializer(), acceptance)))
            plan
        })
}
