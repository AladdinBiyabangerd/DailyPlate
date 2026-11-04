package com.aladdin.dailyplate.data.local

import android.content.Context
import com.aladdin.dailyplate.domain.RecordedAt
import com.aladdin.dailyplate.domain.editorial.*
import java.util.concurrent.Callable

/** Headless test editor. No production export/activation capability; G2/G3 stay closed. */
class TestEditorRepository internal constructor(
    private val database: EditorialDatabase,
    rolesOf: (String) -> Set<EditorialRole>,
) : AutoCloseable {
    private val workflow = EditorialWorkflow(rolesOf)

    fun latest(kind: EditorialKind, id: String): EditorialSnapshot? = database.editorial().latest(kind.name, id)?.decode()

    fun history(kind: EditorialKind, id: String): List<EditorialSnapshot> =
        database.editorial().history(kind.name, id).map { it.decode() }

    fun revision(kind: EditorialKind, id: String, revision: Long): EditorialSnapshot? =
        history(kind, id).find { it.revision == revision }

    fun create(content: EditorialContent, metadata: EditorialMetadata, actor: String,
               at: RecordedAt, reason: String): EditorialSnapshot = database.runInTransaction(Callable {
        if (latest(content.kind, content.ref.id) != null) throw RevisionConflict("Content already exists")
        append(workflow.create(content, metadata, actor, at, reason))
    })

    fun apply(kind: EditorialKind, id: String, expectedRevision: Long, action: EditorialAction,
              actor: String, at: RecordedAt, reason: String, replacement: EditorialContent? = null,
              metadata: EditorialMetadata? = null, restoredRevision: Long? = null): EditorialSnapshot =
        database.runInTransaction(Callable {
            val current = requireNotNull(latest(kind, id)) { "Unknown content" }
            if (current.revision != expectedRevision) throw RevisionConflict("Stale editorial revision")
            val restored = restoredRevision?.let { requireNotNull(revision(kind, id, it)) { "Unknown revision" } }
            append(workflow.apply(current, action, actor, at, reason, replacement, metadata, restored))
        })

    private fun append(snapshot: EditorialSnapshot): EditorialSnapshot {
        database.editorial().insert(EditorialEntity(snapshot.content.kind.name, snapshot.content.ref.id,
            snapshot.revision, snapshotJson.encodeToString(EditorialSnapshot.serializer(), snapshot)))
        // Decode before returning: callers never retain mutable collection aliases into history.
        return requireNotNull(latest(snapshot.content.kind, snapshot.content.ref.id))
    }

    private fun EditorialEntity.decode(): EditorialSnapshot = snapshotJson.decodeFromString(EditorialSnapshot.serializer(), payload)
    override fun close() = database.close()

    companion object {
        /** Test roles are injected locally. Real identity/role provisioning is not implemented. */
        fun open(context: Context, rolesOf: (String) -> Set<EditorialRole>): TestEditorRepository =
            TestEditorRepository(EditorialDatabase.openTestEditor(context), rolesOf)
    }
}
