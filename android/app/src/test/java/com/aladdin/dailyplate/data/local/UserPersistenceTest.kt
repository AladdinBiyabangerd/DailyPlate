package com.aladdin.dailyplate.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.diary.resizePortion
import com.aladdin.dailyplate.domain.entry.repeatDraft
import com.aladdin.dailyplate.domain.entry.confirm
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class UserPersistenceTest {
    private lateinit var database: UserDatabase
    private lateinit var repositories: UserRepositories

    @Before fun open() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), UserDatabase::class.java)
            .allowMainThreadQueries().build()
        repositories = UserRepositories(database)
    }

    @After fun close() = repositories.close()

    @Test fun editedDeletedLogPreservesOriginalAndEventLedger() {
        val original = repositories.foodLogs.record(log())
        val changed = original.resizePortion(0.5)
        repositories.foodLogs.revise(changed, original.ref.version)
        assertEquals(changed, repositories.foodLogs.recent(original.userId).single())
        assertEquals(original, repositories.foodLogs.version(original.userId, original.ref))
        assertThrows(RevisionConflict::class.java) { repositories.foodLogs.revise(original.resizePortion(0.25), 1) }
        repositories.foodLogs.delete(changed.userId, changed.ref)
        assertTrue(repositories.foodLogs.recent(original.userId).isEmpty())
        assertEquals(original, repositories.foodLogs.record(original))
        assertTrue(repositories.foodLogs.recent(original.userId).isEmpty())
        assertThrows(IllegalArgumentException::class.java) { repositories.foodLogs.revise(changed.copy(ref = changed.ref.copy(version = 4)), 3) }
    }

    @Test fun nullsUnitsDatesAndVersionsSurviveDatabaseRoundTrip() {
        val original = profile()
        repositories.profiles.save(original, 0)
        assertEquals(original, repositories.profiles.latest(original.userId, original.ref.id))
        assertNull(repositories.profiles.latest(original.userId, original.ref.id)!!.calculationSex)
        assertNull(repositories.profiles.latest(original.userId, original.ref.id)!!.allergies)
        val savedLog = repositories.foodLogs.record(log())
        val restored = repositories.foodLogs.version(savedLog.userId, savedLog.ref)!!
        assertEquals(savedLog, restored)
        assertNull(restored.nutrientSnapshot.values.getValue("protein").value)
        assertEquals(0.0, restored.nutrientSnapshot.values.getValue("sodium").value!!, 0.0)
        assertEquals(NutrientUnit.MILLIGRAM, restored.nutrientSnapshot.values.getValue("sodium").unit)
        assertEquals(recordedAt, restored.consumedAt)
        assertEquals(7L, restored.recipe!!.version)
    }

    @Test fun revisionsAreAppendOnlyAndScopedToOwner() {
        val original = profile()
        repositories.profiles.save(original, 0)
        val next = original.copy(ref = original.ref.copy(version = 2), allergies = emptySet())
        repositories.profiles.save(next, 1)
        assertEquals(original, repositories.profiles.version(original.userId, original.ref))
        assertEquals(next, repositories.profiles.latest(original.userId, original.ref.id))
        assertNull(repositories.profiles.latest("other-user", original.ref.id))
        assertThrows(RevisionConflict::class.java) {
            repositories.profiles.save(next.copy(goal = Goal.GAIN), 1)
        }
        assertEquals(next, repositories.profiles.save(next, 1))
    }

    @Test fun twoConcurrentWritersCannotSilentlyOverwriteEachOther() {
        val original = profile()
        repositories.profiles.save(original, 0)
        val ready = CountDownLatch(1)
        val pool = Executors.newFixedThreadPool(2)
        try {
            val writes = listOf(Goal.LOSE, Goal.GAIN).map { goal -> pool.submit(Callable {
                ready.await()
                try {
                    repositories.profiles.save(original.copy(ref = original.ref.copy(version = 2), goal = goal), 1)
                    true
                } catch (_: RevisionConflict) { false }
            }) }
            ready.countDown()
            assertEquals(1, writes.count { it.get() })
            assertEquals(original, repositories.profiles.version(original.userId, original.ref))
        } finally { pool.shutdownNow() }
    }

    @Test fun duplicateFoodEventsRemainDeduplicatedAfterReopenAndRevision() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "food-log-reopen-test.db"
        context.deleteDatabase(name)
        fun openFile() = UserRepositories(Room.databaseBuilder(context, UserDatabase::class.java, name)
            .addMigrations(UserDatabase.MIGRATION_1_2).allowMainThreadQueries().build())
        try {
            val confirmed = log().repeatDraft("Repeated meal", recordedAt).confirm("guest-1", "confirmed-event")
            openFile().use { repo ->
                assertEquals(log(), repo.foodLogs.record(log()))
                repo.foodLogs.record(confirmed)
            }
            openFile().use { repo ->
                assertEquals(confirmed, repo.foodLogs.record(confirmed))
                assertEquals(confirmed, repo.foodLogs.latest("guest-1", "confirmed-event"))
                assertEquals(log(), repo.foodLogs.record(log()))
                val revised = log().copy(ref = VersionRef("log-1", 2), confirmedPortionGrams = 100.0)
                repo.foodLogs.revise(revised, 1)
                assertEquals(log(), repo.foodLogs.record(log()))
                assertEquals(revised, repo.foodLogs.latest("guest-1", "log-1"))
                assertThrows(IdempotencyConflict::class.java) {
                    repo.foodLogs.record(log().copy(confirmedPortionGrams = 999.0))
                }
                assertEquals(log("other-user"), repo.foodLogs.record(log("other-user")))
            }
        } finally { context.deleteDatabase(name) }
    }

    @Test fun auxiliaryContractsPersistWithoutChangingTheirMeaning() {
        val custom = RecipeCustomization(VersionRef("custom", 1), "guest-1", recipeRef,
            listOf(IngredientChange(foodRef, IngredientChangeType.REMOVE, null)), 4.0, 1.0,
            nutrients, setOf("protein"), InstructionStatus.NEEDS_REVIEW, null, emptyList(), recordedAt)
        repositories.customizations.save(custom, 0)
        assertEquals(custom, repositories.customizations.version(custom.userId, custom.ref))
        val photo = PhotoMealDraft(VersionRef("photo", 1), "guest-1", "photo-id", "item-a", recipeRef,
            custom.ref, null, emptyList(), null, null, ConfirmationStatus.DRAFT)
        repositories.photoDrafts.save(photo, 0)
        assertEquals(photo, repositories.photoDrafts.version(photo.userId, photo.ref))
        val weight = WeightEntry(VersionRef("weight", 1), "guest-1", recordedAt, 70.25)
        repositories.weights.save(weight, 0)
        assertEquals(weight, repositories.weights.version(weight.userId, weight.ref))
        val reminder = ReminderSettings(VersionRef("reminder", 1), "guest-1", "snack-2", true,
            WallTime("15:30"), "Asia/Baku", QuietHours(WallTime("22:00"), WallTime("08:00")),
            null, PermissionState.DENIED)
        repositories.reminders.save(reminder, 0)
        assertEquals(reminder, repositories.reminders.version(reminder.userId, reminder.ref))
        val bookmark = RecipeBookmark(VersionRef("bookmark", 1), "guest-1", recipeRef)
        repositories.bookmarks.save(bookmark, 0)
        assertEquals(bookmark, repositories.bookmarks.version(bookmark.userId, bookmark.ref))
        val entitlement = SubscriptionEntitlement(VersionRef("store", 1), "guest-1", "product", "store",
            SubscriptionStatus.PENDING, null, null)
        repositories.entitlements.save(entitlement, 0)
        assertEquals(entitlement, repositories.entitlements.version(entitlement.userId, entitlement.ref))
        val draft = EditDraft(VersionRef("edit", 1), "guest-1", EditContext.PREPARED_MEAL,
            recipeRef, VersionRef("plan-1", 1), custom.ref, custom.ingredientChanges, recordedAt)
        repositories.editDrafts.save(draft, 0)
        assertEquals(draft, repositories.editDrafts.version(draft.userId, draft.ref))
    }
    @Test fun recentEntriesUseLatestRevisionOwnerAndConsumptionOrderWithoutRepeatWrites() {
        val original = log()
        repositories.foodLogs.record(original)
        val revised = original.copy(ref = original.ref.copy(version = 2),
            consumedAt = RecordedAt("2026-01-01T10:00:00Z", "UTC"))
        repositories.foodLogs.revise(revised, 1)
        val newer = original.copy(ref = VersionRef("new-entry", 1), clientEventId = "new-event",
            consumedAt = RecordedAt("2026-11-05T10:00:00Z", "Asia/Baku"))
        repositories.foodLogs.record(newer)
        repositories.foodLogs.record(original.copy(userId = "other"))
        assertEquals(listOf(newer, revised), repositories.foodLogs.recent(original.userId))
        val draft = revised.repeatDraft("Previous meal", newer.consumedAt)
        assertEquals(newer.consumedAt, draft.consumedAt)
        assertEquals(revised, draft.previous)
        assertEquals(listOf(newer, revised), repositories.foodLogs.recent(original.userId))
        assertEquals(original, repositories.foodLogs.version(original.userId, original.ref))
    }

}
