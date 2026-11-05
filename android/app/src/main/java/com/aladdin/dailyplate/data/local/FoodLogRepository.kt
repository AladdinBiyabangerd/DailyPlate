package com.aladdin.dailyplate.data.local

import com.aladdin.dailyplate.domain.FoodLog
import com.aladdin.dailyplate.domain.VersionRef
import java.util.concurrent.Callable

/** Event deduplication and snapshot insertion commit together, including after process restarts. */
class FoodLogRepository internal constructor(private val database: UserDatabase) {
    private val history = UserSnapshotRepository(database, "food_log", FoodLog.serializer(), FoodLog::userId, FoodLog::ref)

    /** Latest revision of each entry; immutable snapshots supply repeat drafts. */
    fun recent(userId: String): List<FoodLog> = database.snapshots().all("food_log", userId)
        .distinctBy { it.id }.map { snapshotJson.decodeFromString(FoodLog.serializer(), it.payload) }
        .sortedByDescending { java.time.Instant.parse(it.consumedAt.instant) }

    fun latest(userId: String, id: String): FoodLog? = history.latest(userId, id)
    fun version(userId: String, ref: VersionRef): FoodLog? = history.version(userId, ref)

    fun record(log: FoodLog): FoodLog = database.runInTransaction(Callable {
        require(log.ref.version == 1L)
        val dao = database.snapshots()
        dao.event(log.userId, log.clientEventId)?.let { event ->
            val original = requireNotNull(history.version(log.userId, VersionRef(event.logId, 1)))
            if (original != log) throw IdempotencyConflict("Food event reused with different content")
            return@Callable original
        }
        history.save(log, 0)
        dao.insertEvent(FoodEventEntity(log.userId, log.clientEventId, log.ref.id))
        log
    })

    fun revise(log: FoodLog, expectedRevision: Long): FoodLog = database.runInTransaction(Callable {
        val original = requireNotNull(history.version(log.userId, VersionRef(log.ref.id, 1)))
        require(original.clientEventId == log.clientEventId)
        history.save(log, expectedRevision)
    })
}
