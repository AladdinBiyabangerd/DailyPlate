package com.aladdin.dailyplate.domain.editorial

import com.aladdin.dailyplate.domain.*
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Production wire payload, independent of K05's synthetic EditorialSnapshot workflow. */
@Serializable
data class ReviewedRecipePayload(val recipe: Recipe, val metadata: EditorialMetadata)

@Serializable
enum class ReviewedContentState { PUBLISHED, WITHDRAWN }

/** A trusted publisher's exact-version record, not a signature or proof of clinical effectiveness.
 * SHA-256 covers the UTF-8 payload bytes (including metadata); producers must preserve those bytes.
 * No adapter from EditorialSnapshot is provided. Test editor publication cannot grant production review.
 * Authenticity/authorization and the latest withdrawal selection belong to the importing boundary.
 */
@Serializable
data class ReviewedRecipeContent(
    val schemaVersion: Int,
    val ref: VersionRef,
    val payloadJson: String,
    val payloadSha256: String,
    val publicationId: String,
    val publisherId: String,
    val publishedAt: RecordedAt,
    val state: ReviewedContentState,
    val withdrawnAt: RecordedAt? = null,
    val withdrawalReason: String? = null,
) {
    init {
        require(schemaVersion == 1)
        require(publicationId.isNotBlank() && publisherId.isNotBlank())
        require(payloadSha256.matches(Regex("[0-9a-f]{64}")))
        require((state == ReviewedContentState.WITHDRAWN) == (withdrawnAt != null))
        require(if (state == ReviewedContentState.WITHDRAWN) !withdrawalReason.isNullOrBlank() else withdrawalReason == null)
        withdrawnAt?.let { require(!Instant.parse(it.instant).isBefore(Instant.parse(publishedAt.instant))) }
    }

    /** Call at each new use; a historical snapshot may remain readable after withdrawal/expiry.
     * Completeness and integrity do not activate G3, G4, policy or automatic menu generation.
     */
    fun requireUsable(at: RecordedAt): ReviewedRecipePayload {
        require(state == ReviewedContentState.PUBLISHED) { "Reviewed recipe withdrawn" }
        val now = Instant.parse(at.instant)
        require(!now.isBefore(Instant.parse(publishedAt.instant))) { "Publication is in the future" }
        val digest = MessageDigest.getInstance("SHA-256").digest(payloadJson.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it.toInt() and 0xff) }
        require(digest == payloadSha256) { "Reviewed payload hash mismatch" }
        val payload = Json.decodeFromString(ReviewedRecipePayload.serializer(), payloadJson)
        val recipe = payload.recipe
        require(recipe.ref == ref) { "Reviewed content version mismatch" }
        require(recipe.status == ContentStatus.PUBLISHED && recipe.review != null)
        require(!Instant.parse(recipe.review.reviewedAt.instant).isAfter(Instant.parse(publishedAt.instant)))
        require(PublicationRequirements.missing(EditorialContent.RecipeContent(recipe), payload.metadata).isEmpty()) {
            "Incomplete reviewed recipe publication evidence"
        }
        payload.metadata.nextReviewDate?.let {
            require(LocalDate.parse(it.iso).isAfter(now.atZone(ZoneId.of(at.timezone)).toLocalDate())) {
                "Reviewed recipe requires renewed review"
            }
        }
        return payload
    }
}
