package com.aladdin.dailyplate.domain.editorial

import com.aladdin.dailyplate.domain.*
import org.junit.Assert.*
import org.junit.Test

class EditorialWorkflowTest {
    private val workflow = EditorialWorkflow(testRoles)
    private fun draft(content: EditorialContent = editorialRecipe(), metadata: EditorialMetadata = completeMetadata,
                      actor: String = "author") = workflow.create(content, metadata, actor, editorTime, "TEST creation")
    private fun move(s: EditorialSnapshot, a: EditorialAction, actor: String) =
        workflow.apply(s, a, actor, editorTime, "TEST transition")

    @Test fun allContentKindsFollowFiveStagesWithReviewIdentityAndDate() {
        listOf(editorialRecipe(), editorialPolicy(), editorialCard()).forEach { content ->
            val d = draft(content)
            val pending = move(d, EditorialAction.SUBMIT, "author")
            val approved = move(pending, EditorialAction.APPROVE, "reviewer")
            val published = move(approved, EditorialAction.PUBLISH, "publisher")
            val archived = move(published, EditorialAction.ARCHIVE, "publisher")
            assertEquals(listOf(EditorialStage.DRAFT, EditorialStage.IN_REVIEW, EditorialStage.APPROVED,
                EditorialStage.PUBLISHED, EditorialStage.ARCHIVED), listOf(d, pending, approved, published, archived).map { it.stage })
            assertEquals("reviewer", published.review!!.reviewer)
            assertEquals(editorTime, published.review.reviewedAt)
            assertEquals("author", published.authorId)
            assertEquals(5L, archived.revision)
            assertTrue(published.testOnly)
            published.content.requireDraftPayload()
        }
    }

    @Test fun unauthorizedRolesAndSkippedTransitionsAreRejected() {
        assertThrows(IllegalStateException::class.java) { draft(actor = "unknown") }
        val d = draft()
        for (action in listOf(EditorialAction.SUBMIT, EditorialAction.APPROVE, EditorialAction.PUBLISH, EditorialAction.ARCHIVE)) {
            assertThrows(IllegalStateException::class.java) { move(d, action, "unknown") }
        }
        assertThrows(IllegalArgumentException::class.java) { move(d, EditorialAction.PUBLISH, "publisher") }
        assertThrows(IllegalArgumentException::class.java) { move(d, EditorialAction.APPROVE, "reviewer") }
        assertThrows(IllegalArgumentException::class.java) { move(d, EditorialAction.ARCHIVE, "publisher") }
    }

    @Test fun missingChecklistItemsCannotBeApprovedOrPublished() {
        val cases = listOf(completeMetadata.copy(sourceRefs = emptyList()), completeMetadata.copy(textRights = null),
            completeMetadata.copy(translationChecks = mapOf("az" to "test")), completeMetadata.copy(ingredientCheck = null),
            completeMetadata.copy(allergenCheck = null), completeMetadata.copy(instructionCheck = null),
            completeMetadata.copy(nutritionCheck = null), completeMetadata.copy(declaredServings = null),
            completeMetadata.copy(storage = null), completeMetadata.copy(containsRice = null))
        cases.forEach { metadata ->
            val pending = move(draft(metadata = metadata), EditorialAction.SUBMIT, "author")
            assertThrows(IllegalArgumentException::class.java) { move(pending, EditorialAction.APPROVE, "reviewer") }
            assertThrows(IllegalArgumentException::class.java) {
                move(pending.copy(stage = EditorialStage.APPROVED, review = Review("reviewer", editorTime, "test")),
                    EditorialAction.PUBLISH, "publisher")
            }
        }
    }

    @Test fun anyPolicyContributorCannotSelfApproveEvenWithReviewerRole() {
        val d = draft(editorialPolicy(), actor = "both")
        val pending = move(d, EditorialAction.SUBMIT, "both")
        assertThrows(IllegalArgumentException::class.java) { move(pending, EditorialAction.APPROVE, "both") }
        val other = draft(editorialPolicy())
        val edited = workflow.apply(other, EditorialAction.EDIT, "both", editorTime, "TEST edit",
            editorialPolicy(), completeMetadata)
        assertThrows(IllegalArgumentException::class.java) {
            move(move(edited, EditorialAction.SUBMIT, "author"), EditorialAction.APPROVE, "both")
        }
        val incomplete = editorialPolicy().let { it.copy(policy = it.policy.copy(energyBounds = emptyList())) }
        assertThrows(IllegalArgumentException::class.java) {
            move(move(draft(incomplete), EditorialAction.SUBMIT, "author"), EditorialAction.APPROVE, "reviewer")
        }
    }

    @Test fun yieldAndPhotoChecksAreIndependentAndOptionalForServingEstimate() {
        val base = editorialRecipe()
        assertTrue(PublicationRequirements.missing(base, completeMetadata).isEmpty())
        val cooked = base.copy(recipe = base.recipe.copy(yieldCookedGrams = 400.0,
            nutrientsPer100g = base.recipe.nutrientsTotal))
        assertTrue("yield" in PublicationRequirements.missing(cooked, completeMetadata))
        assertTrue(PublicationRequirements.missing(cooked, completeMetadata.copy(yieldEvidence = "TEST measured batch")).isEmpty())
        val photo = base.copy(recipe = base.recipe.copy(portionRange = PortionRange(50.0, 200.0, 10.0),
            portionPhotos = mapOf("medium" to "test-photo")))
        assertTrue("photo_calibration" in PublicationRequirements.missing(photo, completeMetadata.copy(yieldEvidence = "TEST yield")))
        val calibrated = completeMetadata.copy(photoRights = mapOf("test-photo" to "TEST rights"),
            photoCalibrations = mapOf("medium" to PhotoCalibration(100.0, "TEST same plate/angle measurement")))
        assertTrue(PublicationRequirements.missing(photo, calibrated).isEmpty())
        assertTrue("yield" in PublicationRequirements.missing(cooked, calibrated))
        assertTrue("photo_calibration" in PublicationRequirements.missing(photo,
            calibrated.copy(photoCalibrations = mapOf("medium" to PhotoCalibration(300.0, "TEST")))))
    }

    @Test fun riceRequiresSeparateSafetySourceAndEvidenceRequiresReviewSchedule() {
        assertTrue("rice_storage" in PublicationRequirements.missing(editorialRecipe(), completeMetadata.copy(containsRice = true)))
        val rice = completeMetadata.copy(containsRice = true,
            storage = completeMetadata.storage!!.copy(riceSpecificSource = "TEST official rice guidance"))
        assertTrue(PublicationRequirements.missing(editorialRecipe(), rice).isEmpty())
        val card = draft(editorialCard(), completeMetadata.copy(nextReviewDate = CalendarDate("2026-11-03")))
        assertThrows(IllegalArgumentException::class.java) {
            move(move(card, EditorialAction.SUBMIT, "author"), EditorialAction.APPROVE, "reviewer")
        }
        val badCard = editorialCard().let { it.copy(card = it.card.copy(sourceSection = "", limitations = LocalizedText(""))) }
        assertTrue(PublicationRequirements.missing(badCard, completeMetadata).containsAll(setOf("source_section", "scope")))
    }

    @Test fun editAndRollbackResetApprovalAndVersionBoundEvidence() {
        val d = draft()
        val published = move(move(move(d, EditorialAction.SUBMIT, "author"), EditorialAction.APPROVE, "reviewer"),
            EditorialAction.PUBLISH, "publisher")
        val edited = workflow.apply(published, EditorialAction.EDIT, "author", editorTime, "TEST new recipe",
            editorialRecipe(), EditorialMetadata())
        assertEquals(8L, edited.content.ref.version)
        assertNull(edited.review)
        val rollback = workflow.apply(edited, EditorialAction.ROLLBACK, "author", editorTime, "TEST restore", restored = d)
        assertEquals(9L, rollback.content.ref.version)
        assertEquals(EditorialStage.DRAFT, rollback.stage)
        assertEquals(EditorialMetadata(), rollback.metadata)
        assertNull(rollback.review)
        assertEquals(1L, rollback.audit.restoredRevision)
        assertEquals(EditorialStage.PUBLISHED, published.stage)
        assertThrows(IllegalArgumentException::class.java) {
            workflow.apply(d, EditorialAction.SUBMIT, "author", editorTime.copy(instant = "2026-11-03T00:00:00Z"), "TEST")
        }
    }

    @Test fun publishedPayloadCannotSmuggleApprovalIntoEditor() {
        val c = editorialRecipe()
        assertThrows(IllegalArgumentException::class.java) {
            draft(c.copy(recipe = c.recipe.copy(status = ContentStatus.PUBLISHED,
                review = Review("forged", editorTime, "forged"))))
        }
    }

    @Test fun reviewerCanReturnWorkAndCommandParametersCannotChangeContentDuringPublication() {
        val d = draft()
        val pending = move(d, EditorialAction.SUBMIT, "author")
        val approved = move(pending, EditorialAction.APPROVE, "reviewer")
        val returned = move(approved, EditorialAction.RETURN_TO_DRAFT, "reviewer")
        assertEquals(EditorialStage.DRAFT, returned.stage)
        assertNull(returned.review)
        assertEquals(d.content, returned.content)
        assertThrows(IllegalArgumentException::class.java) { move(returned, EditorialAction.PUBLISH, "publisher") }
        assertThrows(IllegalStateException::class.java) { move(approved, EditorialAction.PUBLISH, "reviewer") }
        assertThrows(IllegalArgumentException::class.java) {
            workflow.apply(approved, EditorialAction.PUBLISH, "publisher", editorTime, "TEST injection",
                replacement = editorialRecipe(), metadata = completeMetadata)
        }
    }
}
