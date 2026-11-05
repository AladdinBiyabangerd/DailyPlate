package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.*
import java.util.concurrent.Callable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Active immutable input bundle. Import/orchestration must advance this revision whenever
 * policy, catalog, evidence, coverage or price inputs change. This is not content approval. */
@Serializable
data class PlanningInputs(
    val ref: VersionRef, val userId: String, val policy: NutritionPolicy?, val data: WeeklyPlanningData,
) {
    init { require(ref.id == "active" && userId.isNotBlank()) }
}

@Serializable
data class GenerationEnvelope(
    val userId: String, val request: PlanGenerationRequest, val profile: UserProfile,
    val inputs: PlanningInputs, val result: PlanGenerationResult,
)

/** Durable K15 boundary. Computes outside the write lock, then checks the captured inputs
 * under the same user-DB transaction as the receipt write. Never accepts caller-made results.
 * Production uses the existing generator gates; fixture generation is internal only.
 * No MealPlan/FoodLog is implicitly created: UI acceptance and recalculation are separate. */
class PlanGenerationRepository internal constructor(private val database: UserDatabase) {
    private val json = Json(snapshotJson) { allowStructuredMapKeys = true }
    private val inputs = UserSnapshotRepository(database, "planning_inputs", PlanningInputs.serializer(),
        PlanningInputs::userId, PlanningInputs::ref, json)
    private val profiles = UserSnapshotRepository(database, "profile", UserProfile.serializer(), UserProfile::userId, UserProfile::ref)

    fun currentInputs(userId: String): PlanningInputs? = inputs.latest(userId, "active")
    fun activate(value: PlanningInputs, expectedRevision: Long): PlanningInputs = inputs.save(value, expectedRevision)

    fun receipt(userId: String, requestId: String): GenerationEnvelope? =
        database.snapshots().version("plan_generation", userId, requestId, 1)?.let {
            require(it.payloadSchema == 1) { "Unsupported generation envelope schema" }
            json.decodeFromString(GenerationEnvelope.serializer(), it.payload)
        }

    fun generate(userId: String, request: PlanGenerationRequest): PlanGenerationResult = execute(userId, request, false)
    internal fun generateTest(userId: String, request: PlanGenerationRequest, beforeCommit: () -> Unit = {}): PlanGenerationResult =
        execute(userId, request, true, beforeCommit)

    private fun execute(userId: String, request: PlanGenerationRequest, test: Boolean, beforeCommit: () -> Unit = {}): PlanGenerationResult {
        require(userId.isNotBlank())
        // Replay is intentionally before current-version checks: the original outcome is immutable.
        fun replay(envelope: GenerationEnvelope): PlanGenerationResult {
            if (envelope.request != request || envelope.result.testOnly != test) {
                throw IdempotencyConflict("Generation request ID reused with different payload")
            }
            return envelope.result
        }
        receipt(userId, request.requestId)?.let { return replay(it) }
        val captured = database.runInTransaction(Callable {
            requireNotNull(profiles.latest(userId, request.candidate.profile.id)) to
                requireNotNull(currentInputs(userId))
        })
        val (profile, bundle) = captured
        val result = if (test) WeeklyPlanGenerator.generateTest(request, profile, bundle.policy, bundle.data)
            else WeeklyPlanGenerator.generate(request, profile, bundle.policy, bundle.data)
        beforeCommit()
        return database.runInTransaction(Callable {
            receipt(userId, request.requestId)?.let { return@Callable replay(it) }
            val versionsMatch = profile.ref == request.candidate.profile &&
                bundle.policy?.ref == request.candidate.policy && bundle.data.catalog.ref == request.candidate.catalog &&
                profiles.latest(userId, profile.ref.id) == profile && currentInputs(userId) == bundle
            val committed = if (versionsMatch) result else PlanGenerationResult(
                GenerationStatus.STALE_INPUT, test, null,
                listOf(CandidateRuleResult("current_versions", request.candidate.policy, "generation", "matches",
                    null, null, null, RuleStatus.FAIL, emptyList(), emptyList(),
                    "Profile or planning inputs changed; generate a new request")), 0)
            val envelope = GenerationEnvelope(userId, request, profile, bundle, committed)
            database.snapshots().insert(UserSnapshotEntity("plan_generation", userId, request.requestId, 1,
                json.encodeToString(GenerationEnvelope.serializer(), envelope)))
            committed
        })
    }
}
