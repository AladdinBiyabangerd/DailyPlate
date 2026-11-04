package com.aladdin.dailyplate.domain.editorial

import com.aladdin.dailyplate.domain.*
import kotlinx.serialization.Serializable

@Serializable
enum class EditorialKind { RECIPE, POLICY, EVIDENCE }
@Serializable
enum class EditorialStage { DRAFT, IN_REVIEW, APPROVED, PUBLISHED, ARCHIVED }
enum class EditorialRole { AUTHOR, CLINICAL_REVIEWER, PUBLISHER }
@Serializable
enum class EditorialAction { CREATE, EDIT, SUBMIT, RETURN_TO_DRAFT, APPROVE, PUBLISH, ARCHIVE, ROLLBACK }

/** Payload status stays DRAFT: only the editorial envelope carries test workflow approval. */
@Serializable
sealed class EditorialContent {
    abstract val ref: VersionRef
    abstract val kind: EditorialKind

    @Serializable
    data class RecipeContent(val recipe: Recipe) : EditorialContent() {
        override val ref get() = recipe.ref
        override val kind get() = EditorialKind.RECIPE
    }
    @Serializable
    data class PolicyContent(val policy: NutritionPolicy) : EditorialContent() {
        override val ref get() = policy.ref
        override val kind get() = EditorialKind.POLICY
    }
    @Serializable
    data class EvidenceContent(val card: EvidenceCard) : EditorialContent() {
        override val ref get() = card.ref
        override val kind get() = EditorialKind.EVIDENCE
    }

    fun atVersion(version: Long): EditorialContent = when (this) {
        is RecipeContent -> copy(recipe = recipe.copy(ref = ref.copy(version = version)))
        is PolicyContent -> copy(policy = policy.copy(ref = ref.copy(version = version)))
        is EvidenceContent -> copy(card = card.copy(ref = ref.copy(version = version)))
    }

    fun requireDraftPayload() {
        when (this) {
            is RecipeContent -> require(recipe.status == ContentStatus.DRAFT && recipe.review == null)
            is PolicyContent -> require(policy.status == PolicyStatus.DRAFT && policy.review == null && policy.approvedAt == null)
            is EvidenceContent -> require(card.status == ContentStatus.DRAFT && card.review == null)
        }
    }
}

@Serializable
data class StorageGuidance(
    val id: String, val cooling: LocalizedText, val refrigeration: LocalizedText,
    val freezing: LocalizedText, val reheating: LocalizedText, val sourceRefs: List<String>,
    val riceSpecificSource: String? = null,
)

@Serializable
data class PhotoCalibration(val grams: Double, val evidenceRef: String) {
    init { require(grams.isFinite() && grams > 0); require(evidenceRef.isNotBlank()) }
}

/** Evidence belongs to this exact content version; changes must supply fresh metadata. */
@Serializable
data class EditorialMetadata(
    val sourceRefs: List<String> = emptyList(),
    val textRights: String? = null,
    val photoRights: Map<String, String> = emptyMap(),
    val translationChecks: Map<String, String> = emptyMap(),
    val ingredientCheck: String? = null,
    val allergenCheck: String? = null,
    val instructionCheck: String? = null,
    val nutritionCheck: String? = null,
    val declaredServings: Double? = null,
    val storage: StorageGuidance? = null,
    val containsRice: Boolean? = null,
    val yieldEvidence: String? = null,
    val photoCalibrations: Map<String, PhotoCalibration> = emptyMap(),
    val clinicalScopeCheck: String? = null,
    val nextReviewDate: CalendarDate? = null,
)

@Serializable
data class EditorialAudit(
    val revision: Long, val action: EditorialAction, val actorId: String,
    val at: RecordedAt, val reason: String, val restoredRevision: Long? = null,
)

@Serializable
data class EditorialSnapshot(
    val revision: Long, val content: EditorialContent, val metadata: EditorialMetadata,
    val stage: EditorialStage, val authorId: String, val editorId: String,
    val contributors: Set<String>,
    val review: Review?, val audit: EditorialAudit,
) {
    // K05 can exercise the full workflow without conferring real G2/G3 approval.
    val testOnly: Boolean get() = true
}
