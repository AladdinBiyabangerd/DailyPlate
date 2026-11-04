package com.aladdin.dailyplate.domain.editorial

import com.aladdin.dailyplate.domain.*
import java.time.Instant
import java.time.LocalDate

/** Roles come from the trusted editor session, never from a command payload. */
class EditorialWorkflow(private val rolesOf: (String) -> Set<EditorialRole>) {
    fun create(content: EditorialContent, metadata: EditorialMetadata, actor: String,
               at: RecordedAt, reason: String): EditorialSnapshot {
        authorize(actor, EditorialRole.AUTHOR)
        require(reason.isNotBlank())
        content.requireDraftPayload()
        return EditorialSnapshot(1, content, metadata, EditorialStage.DRAFT, actor, actor,
            setOf(actor), null, EditorialAudit(1, EditorialAction.CREATE, actor, at, reason))
    }

    fun apply(current: EditorialSnapshot, action: EditorialAction, actor: String, at: RecordedAt,
              reason: String, replacement: EditorialContent? = null,
              metadata: EditorialMetadata? = null, restored: EditorialSnapshot? = null): EditorialSnapshot {
        require(reason.isNotBlank())
        require(!Instant.parse(at.instant).isBefore(Instant.parse(current.audit.at.instant)))
        require(action != EditorialAction.CREATE)
        val role = when (action) {
            EditorialAction.APPROVE, EditorialAction.RETURN_TO_DRAFT -> EditorialRole.CLINICAL_REVIEWER
            EditorialAction.PUBLISH, EditorialAction.ARCHIVE -> EditorialRole.PUBLISHER
            else -> EditorialRole.AUTHOR
        }
        authorize(actor, role)
        require((action == EditorialAction.EDIT) == (replacement != null))
        require((action == EditorialAction.EDIT) == (metadata != null))
        require((action == EditorialAction.ROLLBACK) == (restored != null))
        val revision = Math.addExact(current.revision, 1)
        val audit = EditorialAudit(revision, action, actor, at, reason, restored?.revision)
        val next = current.copy(revision = revision, audit = audit)
        return when (action) {
            EditorialAction.EDIT, EditorialAction.ROLLBACK -> {
                val source = replacement ?: requireNotNull(restored).content
                require(source.kind == current.content.kind && source.ref.id == current.content.ref.id)
                restored?.let { require(it.revision < current.revision) }
                source.requireDraftPayload()
                next.copy(content = source.atVersion(Math.addExact(current.content.ref.version, 1)),
                    metadata = metadata ?: EditorialMetadata(), stage = EditorialStage.DRAFT,
                    editorId = actor, contributors = current.contributors + actor, review = null)
            }
            EditorialAction.SUBMIT -> {
                require(current.stage == EditorialStage.DRAFT)
                next.copy(stage = EditorialStage.IN_REVIEW)
            }
            EditorialAction.RETURN_TO_DRAFT -> {
                require(current.stage == EditorialStage.IN_REVIEW || current.stage == EditorialStage.APPROVED)
                next.copy(stage = EditorialStage.DRAFT, review = null)
            }
            EditorialAction.APPROVE -> {
                require(current.stage == EditorialStage.IN_REVIEW)
                require(actor !in current.contributors) { "A contributor cannot approve their own content" }
                requireComplete(current, at)
                next.copy(stage = EditorialStage.APPROVED,
                    review = Review(actor, at, "${current.content.kind}:${current.content.ref.id}:$revision"))
            }
            EditorialAction.PUBLISH -> {
                require(current.stage == EditorialStage.APPROVED && current.review != null)
                requireComplete(current, at)
                next.copy(stage = EditorialStage.PUBLISHED)
            }
            EditorialAction.ARCHIVE -> {
                require(current.stage == EditorialStage.PUBLISHED)
                next.copy(stage = EditorialStage.ARCHIVED)
            }
            EditorialAction.CREATE -> error("Use create")
        }
    }

    private fun requireComplete(snapshot: EditorialSnapshot, at: RecordedAt) {
        val missing = PublicationRequirements.missing(snapshot.content, snapshot.metadata)
        require(missing.isEmpty()) { "Incomplete publication checks: $missing" }
        snapshot.metadata.nextReviewDate?.let {
            val today = Instant.parse(at.instant).atZone(java.time.ZoneId.of(at.timezone)).toLocalDate()
            require(LocalDate.parse(it.iso).isAfter(today)) { "Next review must be in the future" }
        }
    }

    private fun authorize(actor: String, role: EditorialRole) {
        require(actor.isNotBlank())
        check(role in rolesOf(actor)) { "Actor lacks $role role" }
    }
}
