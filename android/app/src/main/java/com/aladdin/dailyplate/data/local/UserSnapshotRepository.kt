package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.VersionRef
import java.util.concurrent.Callable
import kotlinx.serialization.KSerializer

class RevisionConflict(message: String) : IllegalStateException(message)
class IdempotencyConflict(message: String) : IllegalStateException(message)

/** Append-only revision history. Call from a worker thread; Room rejects main-thread IO. */
class UserSnapshotRepository<T> internal constructor(
    private val database: UserDatabase,
    private val kind: String,
    private val serializer: KSerializer<T>,
    private val userIdOf: (T) -> String,
    private val refOf: (T) -> VersionRef,
) {
    fun latest(userId: String, id: String): T? =
        database.snapshots().latest(kind, userId, id)?.decode()

    fun version(userId: String, ref: VersionRef): T? =
        database.snapshots().version(kind, userId, ref.id, ref.version)?.decode()

    /** expectedRevision=0 creates an object; subsequent writes must increment by one. */
    fun save(value: T, expectedRevision: Long): T = database.runInTransaction(Callable {
        val userId = userIdOf(value)
        val ref = refOf(value)
        require(userId.isNotBlank())
        require(expectedRevision >= 0 && ref.version == Math.addExact(expectedRevision, 1))
        val dao = database.snapshots()
        val current = dao.latest(kind, userId, ref.id)
        require(current == null || current.payloadSchema == 1) { "Unsupported current payload schema" }
        val payload = snapshotJson.encodeToString(serializer, value)
        if (current?.revision == ref.version && current.payload == payload) return@Callable current.decode()
        if ((current?.revision ?: 0) != expectedRevision) {
            throw RevisionConflict("Stale $kind revision for ${ref.id}")
        }
        dao.insert(UserSnapshotEntity(kind, userId, ref.id, ref.version, payload))
        value
    })

    private fun UserSnapshotEntity.decode(): T {
        require(payloadSchema == 1) { "Unsupported user snapshot payload schema: $payloadSchema" }
        return snapshotJson.decodeFromString(serializer, payload)
    }
}
