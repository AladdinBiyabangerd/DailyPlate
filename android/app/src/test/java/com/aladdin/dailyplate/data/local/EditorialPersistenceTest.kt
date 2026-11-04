package com.aladdin.dailyplate.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.editorial.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class EditorialPersistenceTest {
    private val context get() = ApplicationProvider.getApplicationContext<Context>()
    private fun open(name: String) = Room.databaseBuilder(context, EditorialDatabase::class.java, name)
        .allowMainThreadQueries().build()
    private fun move(repo: TestEditorRepository, snapshot: EditorialSnapshot, action: EditorialAction, actor: String) =
        repo.apply(snapshot.content.kind, snapshot.content.ref.id, snapshot.revision, action, actor, editorTime, "TEST transition")

    @Test fun testEditorWorkflowSurvivesReopenAndRollbackPreservesPublishedHistoryAndFoodLog() {
        val name = "editorial-reopen.db"
        context.deleteDatabase(name)
        try {
            Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java).allowMainThreadQueries().build().useDatabase { users ->
                val personal = UserRepositories(users)
                val originalLog = personal.foodLogs.record(log())
                val recipe = editorialRecipe().let { it.copy(recipe = it.recipe.copy(ref = recipeRef)) }
                var published: EditorialSnapshot? = null
                TestEditorRepository(open(name), testRoles).use { repo ->
                    val draft = repo.create(recipe, completeMetadata, "author", editorTime, "TEST draft")
                    val submitted = move(repo, draft, EditorialAction.SUBMIT, "author")
                    val approved = move(repo, submitted, EditorialAction.APPROVE, "reviewer")
                    published = move(repo, approved, EditorialAction.PUBLISH, "publisher")
                    assertThrows(RevisionConflict::class.java) { move(repo, draft, EditorialAction.SUBMIT, "author") }
                }
                TestEditorRepository(open(name), testRoles).use { repo ->
                    val p = requireNotNull(published)
                    assertEquals(p, repo.latest(EditorialKind.RECIPE, recipeRef.id))
                    val archived = move(repo, p, EditorialAction.ARCHIVE, "publisher")
                    val rollback = repo.apply(EditorialKind.RECIPE, recipeRef.id, archived.revision, EditorialAction.ROLLBACK,
                        "author", editorTime, "TEST rollback", restoredRevision = 1)
                    assertEquals(8L, rollback.content.ref.version)
                    assertEquals(EditorialStage.DRAFT, rollback.stage)
                    assertNull(rollback.review)
                    assertEquals(EditorialMetadata(), rollback.metadata)
                    assertEquals(p, repo.revision(EditorialKind.RECIPE, recipeRef.id, p.revision))
                    assertEquals((1L..6L).toList(), repo.history(EditorialKind.RECIPE, recipeRef.id).map { it.audit.revision })
                    assertEquals(originalLog, personal.foodLogs.version(originalLog.userId, originalLog.ref))
                    assertThrows(IllegalArgumentException::class.java) {
                        repo.apply(EditorialKind.RECIPE, recipeRef.id, rollback.revision, EditorialAction.ROLLBACK,
                            "author", editorTime, "TEST bad target", restoredRevision = 999)
                    }
                    assertEquals(rollback, repo.latest(EditorialKind.RECIPE, recipeRef.id))
                }
            }
        } finally { context.deleteDatabase(name) }
    }

    @Test fun failedWriteAndRejectedCommandsLeaveNoPartialStateOrAudit() {
        Room.inMemoryDatabaseBuilder(context, EditorialDatabase::class.java).allowMainThreadQueries().build().useDatabase { db ->
            val repo = TestEditorRepository(db, testRoles)
            val d = repo.create(editorialRecipe(), completeMetadata, "author", editorTime, "TEST draft")
            assertThrows(IllegalStateException::class.java) { move(repo, d, EditorialAction.SUBMIT, "unknown") }
            assertThrows(IllegalArgumentException::class.java) { move(repo, d, EditorialAction.PUBLISH, "publisher") }
            db.openHelper.writableDatabase.execSQL("CREATE TRIGGER reject_editorial BEFORE INSERT ON editorial_history BEGIN SELECT RAISE(ABORT, 'TEST disk failure'); END")
            assertThrows(android.database.sqlite.SQLiteException::class.java) { move(repo, d, EditorialAction.SUBMIT, "author") }
            assertEquals(listOf(d), repo.history(EditorialKind.RECIPE, d.content.ref.id))
            db.openHelper.writableDatabase.execSQL("DROP TRIGGER reject_editorial")
            assertEquals(2L, move(repo, d, EditorialAction.SUBMIT, "author").revision)
        }
    }

    @Test fun policyAndClaimVersionsPersistWithoutActivatingProductionCatalog() {
        Room.inMemoryDatabaseBuilder(context, EditorialDatabase::class.java).allowMainThreadQueries().build().useDatabase { db ->
            val repo = TestEditorRepository(db, testRoles)
            listOf(editorialPolicy(), editorialCard()).forEach { content ->
                val d = repo.create(content, completeMetadata, "author", editorTime, "TEST create")
                val p = move(repo, move(repo, move(repo, d, EditorialAction.SUBMIT, "author"),
                    EditorialAction.APPROVE, "reviewer"), EditorialAction.PUBLISH, "publisher")
                assertEquals(p, repo.latest(content.kind, content.ref.id))
                assertEquals(d, repo.revision(content.kind, content.ref.id, 1))
                p.content.requireDraftPayload()
                assertTrue(p.testOnly)
            }
            Room.inMemoryDatabaseBuilder(context, LibraryDatabase::class.java).allowMainThreadQueries().build().useDatabase { library ->
                val catalog = CatalogRepository(library)
                assertTrue(catalog.productionRecipes().isEmpty())
                assertTrue(catalog.productionFoods().isEmpty())
                assertNull(catalog.policy(editorialPolicy().ref))
                assertNull(catalog.evidence(editorialCard().ref))
            }
        }
    }
}
