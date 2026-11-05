package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.NutritionPolicy
import com.aladdin.dailyplate.domain.VersionRef
import com.aladdin.dailyplate.domain.planning.WeeklyPlanningData
import java.util.concurrent.Callable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Complete replacement, never a merge with an older catalog, evidence or price selection.
 * This transport is not a signature, publication approval or a library-database import.
 */
@Serializable
data class PlanningInputPackage(
    val schemaVersion: Int, val ref: VersionRef,
    val policy: NutritionPolicy?, val data: WeeklyPlanningData,
) {
    init { require(schemaVersion == 1); require(ref.id == "planning-import") }
}

@Serializable
internal data class PlanningImportReceipt(val source: PlanningInputPackage, val activated: PlanningInputs)

/** Worker-thread boundary for a trusted local caller. Receipt and active revision commit together.
 * Current editorial envelopes are all test-only: none can enter through this production boundary.
 * Empty/unavailable inputs may be imported, but cannot authorize personalized menu generation.
 */
class PlanningInputImporter internal constructor(private val database: UserDatabase) {
    private val json = Json(snapshotJson) { allowStructuredMapKeys = true }
    private val generation = PlanGenerationRepository(database)

    fun importJson(userId: String, payload: String, expectedRevision: Long): PlanningInputs {
        require(userId.isNotBlank() && expectedRevision >= 0)
        val source = json.decodeFromString(PlanningInputPackage.serializer(), payload)
        require(source.data.catalog.recipes.none { it.testOnly }) { "Fixture editorial content cannot be imported into production" }
        require(source.data.prices.none { it.testOnly }) { "Fixture prices cannot be imported into production" }
        return database.runInTransaction(Callable {
            val dao = database.snapshots()
            val previous = dao.version("planning_import", userId, source.ref.id, source.ref.version)
            if (previous != null) {
                require(previous.payloadSchema == 1)
                val receipt = json.decodeFromString(PlanningImportReceipt.serializer(), previous.payload)
                if (receipt.source != source) throw IdempotencyConflict("Planning package version reused with different content")
                // Replay never reactivates an old bundle after a later import.
                return@Callable receipt.activated
            }
            val latest = dao.latest("planning_import", userId, source.ref.id)
            require(latest == null || source.ref.version > latest.revision) { "Planning package downgrade rejected" }
            val current = generation.currentInputs(userId)
            if ((current?.ref?.version ?: 0) != expectedRevision) throw RevisionConflict("Stale planning import revision")
            val activated = generation.activate(PlanningInputs(
                VersionRef("active", Math.addExact(expectedRevision, 1)), userId, source.policy, source.data,
            ), expectedRevision)
            dao.insert(UserSnapshotEntity("planning_import", userId, source.ref.id, source.ref.version,
                json.encodeToString(PlanningImportReceipt.serializer(), PlanningImportReceipt(source, activated))))
            activated
        })
    }
}
