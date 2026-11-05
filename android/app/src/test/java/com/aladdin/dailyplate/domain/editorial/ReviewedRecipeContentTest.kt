package com.aladdin.dailyplate.domain.editorial

import com.aladdin.dailyplate.domain.*
import java.security.MessageDigest
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

/** Synthetic inputs exercise the production contract; they are never shipped as reviewed content. */
class ReviewedRecipeContentTest {
    private fun record(metadata: EditorialMetadata = completeMetadata): ReviewedRecipeContent {
        val recipe = editorialRecipe().recipe.copy(status = ContentStatus.PUBLISHED,
            review = Review("TEST independent reviewer", editorTime, "TEST review record"))
        val bytes = Json.encodeToString(ReviewedRecipePayload.serializer(), ReviewedRecipePayload(recipe, metadata))
        val hash = MessageDigest.getInstance("SHA-256").digest(bytes.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it.toInt() and 0xff) }
        return ReviewedRecipeContent(1, recipe.ref, bytes, hash, "TEST publication", "TEST publisher",
            editorTime, ReviewedContentState.PUBLISHED)
    }

    @Test fun exactVersionRoundTripsWithoutUsingTestEditorialSnapshot() {
        val source = record()
        val decoded = Json.decodeFromString(ReviewedRecipeContent.serializer(),
            Json.encodeToString(ReviewedRecipeContent.serializer(), source))
        assertEquals(source, decoded)
        assertEquals(source.ref, decoded.requireUsable(editorTime).recipe.ref)
        assertTrue(EditorialWorkflow(testRoles).create(editorialRecipe(), completeMetadata, "author",
            editorTime, "TEST").testOnly)
    }

    @Test fun alteredPayloadMetadataHashOrVersionCannotReuseReview() {
        val source = record()
        listOf(source.copy(payloadJson = source.payloadJson + " "), source.copy(payloadSha256 = "0".repeat(64)),
            source.copy(ref = source.ref.copy(version = source.ref.version + 1)),
            record(completeMetadata.copy(allergenCheck = null)),
            record(completeMetadata.copy(textRights = null)),
            record(completeMetadata.copy(translationChecks = emptyMap()))).forEach {
            assertThrows(IllegalArgumentException::class.java) { it.requireUsable(editorTime) }
        }
    }

    @Test fun withdrawalAndReviewExpiryBlockNewUseWithoutChangingHistoricalPayload() {
        val source = record()
        val withdrawn = source.copy(state = ReviewedContentState.WITHDRAWN,
            withdrawnAt = editorTime, withdrawalReason = "TEST recall")
        assertEquals(source.payloadJson, withdrawn.payloadJson)
        assertThrows(IllegalArgumentException::class.java) { withdrawn.requireUsable(editorTime) }
        val expired = record(completeMetadata.copy(nextReviewDate = CalendarDate(editorTime.instant.take(10))))
        assertThrows(IllegalArgumentException::class.java) { expired.requireUsable(editorTime) }
        assertThrows(IllegalArgumentException::class.java) {
            source.copy(publishedAt = editorTime.copy(instant = "2099-01-01T00:00:00Z")).requireUsable(editorTime)
        }
        assertThrows(IllegalArgumentException::class.java) {
            source.copy(publishedAt = editorTime.copy(instant = "2000-01-01T00:00:00Z")).requireUsable(editorTime)
        }
    }

    @Test fun unsupportedSchemaAndTestSnapshotWireFormatAreRejected() {
        assertThrows(IllegalArgumentException::class.java) { record().copy(schemaVersion = 2) }
        assertThrows(IllegalArgumentException::class.java) { record().copy(state = ReviewedContentState.WITHDRAWN) }
        val snapshot = EditorialWorkflow(testRoles).create(editorialRecipe(), completeMetadata, "author", editorTime, "TEST")
        assertThrows(IllegalArgumentException::class.java) {
            Json.decodeFromString(ReviewedRecipeContent.serializer(), Json.encodeToString(EditorialSnapshot.serializer(), snapshot))
        }
    }
}
