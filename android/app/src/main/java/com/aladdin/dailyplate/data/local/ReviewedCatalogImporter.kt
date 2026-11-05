package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.*
import java.util.concurrent.Callable
import kotlinx.serialization.Serializable

/** Complete replacement from a trusted local publisher; no test-editor envelopes accepted.
 * This transport does not authenticate a network publisher or open G3/G4.
 */
@Serializable
data class ReviewedCatalogPackage(
    val schemaVersion: Int, val ref: VersionRef, val recipes: List<ReviewedRecipeContent>,
) {
    init {
        require(schemaVersion == 1 && ref.id == "reviewed-catalog")
        require(recipes.map { it.ref.id }.distinct().size == recipes.size)
    }
}

internal class ReviewedCatalogImporter(private val database: LibraryDatabase) {
    fun importJson(payload: String, at: RecordedAt): VersionRef {
        val pack = snapshotJson.decodeFromString(ReviewedCatalogPackage.serializer(), payload)
        return database.runInTransaction(Callable {
            val dao = database.snapshots()
            val history = dao.all("reviewed_catalog")
            val previous = history.firstOrNull()
            val serialized = snapshotJson.encodeToString(ReviewedCatalogPackage.serializer(), pack)
            val existing = dao.version("reviewed_catalog", pack.ref.id, pack.ref.version)
            if (existing != null) {
                require(existing.payload == serialized) { "Immutable reviewed catalog revision conflict" }
                // Replay never restores an older active selection.
                return@Callable pack.ref
            }
            require(previous == null || pack.ref.version > previous.version) { "Reviewed catalog downgrade rejected" }
            val oldRecords = history.flatMap {
                snapshotJson.decodeFromString(ReviewedCatalogPackage.serializer(), it.payload).recipes
            }
            for (record in pack.recipes) {
                val older = oldRecords.filter { it.ref.id == record.ref.id }
                require(older.none { it.ref.version > record.ref.version }) { "Recipe downgrade rejected" }
                older.filter { it.ref == record.ref }.forEach {
                    require(it.payloadSha256 == record.payloadSha256 && it.payloadJson == record.payloadJson &&
                        it.publicationId == record.publicationId && it.publisherId == record.publisherId &&
                        it.publishedAt == record.publishedAt) { "Reviewed recipe version reused" }
                    require(it.state != ReviewedContentState.WITHDRAWN || it == record) { "Withdrawn version cannot reactivate" }
                }
                // Withdrawal still verifies payload integrity/evidence/recompute before accepting the replacement.
                val checked = if (record.state == ReviewedContentState.WITHDRAWN) {
                    require(java.time.Instant.parse(requireNotNull(record.withdrawnAt).instant) <= java.time.Instant.parse(at.instant))
                    record.copy(state = ReviewedContentState.PUBLISHED, withdrawnAt = null, withdrawalReason = null)
                        .requireUsable(record.publishedAt)
                } else record.requireUsable(at)
                ReviewedRecipeRecompute.verify(checked) { ref ->
                    dao.version("food", ref.id, ref.version)?.let {
                        snapshotJson.decodeFromString(Food.serializer(), it.payload)
                    }
                }
            }
            // Single immutable row is the atomic active selection; highest revision wins.
            dao.insert(LibrarySnapshotEntity("reviewed_catalog", pack.ref.id, pack.ref.version, serialized))
            pack.ref
        })
    }
}
