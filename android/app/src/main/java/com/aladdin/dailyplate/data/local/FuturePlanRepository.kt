package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.*
import com.aladdin.dailyplate.domain.profile.*
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.Callable
import kotlinx.serialization.Serializable

@Serializable
data class FuturePlanCommand(
    val operationId: String, val plan: VersionRef, val generationRequestId: String, val effectiveFrom: CalendarDate,
) {
    init { require(operationId.isNotBlank() && generationRequestId.isNotBlank()) }
}

/** Durable preview, including rejected merged candidates. No plan/log write on preview/cancel. */
@Serializable
data class FuturePlanRecord(val userId: String, val command: FuturePlanCommand, val calculation: FuturePlanCalculation)

/** Orchestrates existing generation, profile-change and immutable plan contracts. Call generation
 * for the newly confirmed profile first, then preview and explicitly confirm that exact preview. */
class FuturePlanRepository internal constructor(private val database: UserDatabase, private val clock: Clock = Clock.systemUTC()) {
    private val generation = PlanGenerationRepository(database)
    private val plans = MealPlanRepository(database)
    private val history = UserSnapshotRepository(database, "plan", MealPlan.serializer(), MealPlan::userId, MealPlan::ref)
    private val profiles = UserSnapshotRepository(database, "profile", UserProfile.serializer(), UserProfile::userId, UserProfile::ref)
    private val changes = UserSnapshotRepository(database, "profile_change", ProfileChangeRecord.serializer(), ProfileChangeRecord::userId, ProfileChangeRecord::ref)

    fun preview(userId: String, command: FuturePlanCommand): FuturePlanRecord = prepare(userId, command, false)
    internal fun previewTest(userId: String, command: FuturePlanCommand, beforeCommit: () -> Unit = {}): FuturePlanRecord =
        prepare(userId, command, true, beforeCommit)

    fun record(userId: String, operationId: String): FuturePlanRecord? = read("future_plan_preview", userId, operationId)
    fun accepted(userId: String, operationId: String): FuturePlanRecord? = read("future_plan_acceptance", userId, operationId)

    private fun read(kind: String, userId: String, id: String): FuturePlanRecord? =
        database.snapshots().version(kind, userId, id, 1)?.let {
            require(it.payloadSchema == 1)
            snapshotJson.decodeFromString(FuturePlanRecord.serializer(), it.payload)
        }

    private fun checkDate(plan: MealPlan, effectiveFrom: CalendarDate) {
        require(effectiveFrom.iso >= LocalDate.now(clock.withZone(ZoneId.of(plan.timezone))).toString()) {
            "The effective date is in the past; reopen the preview"
        }
    }

    private fun prepare(userId: String, command: FuturePlanCommand, test: Boolean, beforeCommit: () -> Unit = {}): FuturePlanRecord {
        require(userId.isNotBlank())
        fun replay(record: FuturePlanRecord): FuturePlanRecord {
            if (record.command != command) throw IdempotencyConflict("Future-plan operation reused with a different command")
            val envelope = requireNotNull(generation.receipt(userId, command.generationRequestId))
            require(envelope.result.testOnly == test) { "Fixture preview cannot be used in production" }
            return record
        }
        record(userId, command.operationId)?.let { return replay(it) }
        val captured = database.runInTransaction(Callable {
            val before = requireNotNull(plans.version(userId, command.plan))
            if (plans.latest(userId, before.ref.id) != before) throw RevisionConflict("Plan changed; reopen the preview")
            checkDate(before, command.effectiveFrom)
            val envelope = requireNotNull(generation.receipt(userId, command.generationRequestId))
            require(envelope.result.testOnly == test) { "Fixture generation cannot be used in production" }
            val change = requireNotNull(changes.version(userId, envelope.profile.ref))
            require(change.request.fromProfile == before.userProfile && change.request.toProfile == envelope.profile.ref)
            checkInputs(envelope)
            Triple(before, envelope, change)
        })
        val (before, envelope, change) = captured
        val result = FuturePlanCalculator.calculate(before, change.request, envelope.profile, envelope.result,
            envelope.inputs.data, command.effectiveFrom, envelope.inputs.policy)
        val value = FuturePlanRecord(userId, command, result)
        beforeCommit()
        return database.runInTransaction(Callable {
            record(userId, command.operationId)?.let { return@Callable replay(it) }
            if (plans.latest(userId, before.ref.id) != before) throw RevisionConflict("Plan changed during recalculation")
            checkInputs(envelope)
            checkDate(before, command.effectiveFrom)
            write("future_plan_preview", value)
            value
        })
    }

    fun confirm(value: FuturePlanRecord): MealPlan = confirmInternal(value, false)
    internal fun confirmTest(value: FuturePlanRecord): MealPlan = confirmInternal(value, true)

    private fun confirmInternal(value: FuturePlanRecord, test: Boolean): MealPlan = database.runInTransaction(Callable {
        val stored = requireNotNull(record(value.userId, value.command.operationId))
        if (stored != value) throw IdempotencyConflict("Confirmation differs from the stored preview")
        val envelope = requireNotNull(generation.receipt(value.userId, value.command.generationRequestId))
        require(envelope.result.testOnly == test) { "Fixture preview cannot be accepted in production" }
        val preview = requireNotNull(value.calculation.preview) { "Merged candidate cannot be accepted" }
        require(value.calculation.status == GenerationStatus.SUCCESS)
        accepted(value.userId, value.command.operationId)?.let { receipt ->
            if (receipt != value) throw IdempotencyConflict("Future-plan operation already accepted differently")
            return@Callable requireNotNull(plans.version(value.userId, preview.after.ref))
        }
        val current = requireNotNull(plans.latest(value.userId, preview.before.ref.id))
        if (current != preview.before) throw RevisionConflict("Plan changed after preview")
        checkInputs(envelope)
        checkDate(current, preview.effectiveFrom)
        // Existing domain acceptance enforces the exact plan/profile versions and frozen items.
        val acceptance = preview.accept(current.ref, envelope.profile.ref)
        history.save(acceptance.preview.after, current.ref.version)
        write("future_plan_acceptance", value)
        acceptance.preview.after
    })

    private fun checkInputs(envelope: GenerationEnvelope) {
        if (profiles.latest(envelope.userId, envelope.profile.ref.id) != envelope.profile ||
            generation.currentInputs(envelope.userId) != envelope.inputs)
            throw RevisionConflict("Profile or planning inputs changed; generate a new request")
    }

    private fun write(kind: String, value: FuturePlanRecord) {
        database.snapshots().insert(UserSnapshotEntity(kind, value.userId, value.command.operationId, 1,
            snapshotJson.encodeToString(FuturePlanRecord.serializer(), value)))
    }
}
