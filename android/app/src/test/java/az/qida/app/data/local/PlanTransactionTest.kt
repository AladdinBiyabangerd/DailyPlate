package az.qida.app.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import az.qida.app.domain.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class PlanTransactionTest {
    private lateinit var database: UserDatabase
    private lateinit var repositories: UserRepositories

    @Before fun open() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), UserDatabase::class.java)
            .allowMainThreadQueries().build()
        repositories = UserRepositories(database)
        repositories.plans.create(plan())
        repositories.foodLogs.record(log())
    }
    @After fun close() = repositories.close()

    @Test fun swapAndShoppingCommitOnceWithoutMutatingRecordedFood() {
        val (change, next) = swap()
        assertEquals(next, repositories.plans.apply(change, next))
        assertEquals(next, repositories.plans.apply(change, next))
        assertEquals(plan(), repositories.plans.version("guest-1", VersionRef("plan-1", 1)))
        assertEquals(next, repositories.plans.latest("guest-1", "plan-1"))
        assertEquals(change, repositories.plans.change("guest-1", change.operationId))
        assertEquals(log(), repositories.foodLogs.latest("guest-1", "log-1"))
        assertThrows(IdempotencyConflict::class.java) {
            repositories.plans.apply(change, next.copy(nutrientSnapshot = NutrientSnapshot(emptyMap())))
        }
    }

    @Test fun failureAfterPlanInsertionRollsBackItemsShoppingAndOperation() {
        val (change, next) = swap()
        database.openHelper.writableDatabase.execSQL("""
            CREATE TRIGGER reject_operation BEFORE INSERT ON user_snapshots
            WHEN NEW.kind = 'plan_change'
            BEGIN SELECT RAISE(ABORT, 'injected write failure'); END
        """.trimIndent())
        assertThrows(android.database.sqlite.SQLiteConstraintException::class.java) {
            repositories.plans.apply(change, next)
        }
        assertEquals(plan(), repositories.plans.latest("guest-1", "plan-1"))
        assertNull(repositories.plans.version("guest-1", next.ref))
        assertNull(repositories.plans.change("guest-1", change.operationId))
        assertEquals(log(), repositories.foodLogs.latest("guest-1", "log-1"))
        database.openHelper.writableDatabase.execSQL("DROP TRIGGER reject_operation")
        assertEquals(next, repositories.plans.apply(change, next))
    }

    @Test fun stalePreviewAndUnexpectedProfileChangeAreRejected() {
        val (change, next) = swap()
        assertThrows(IllegalArgumentException::class.java) {
            repositories.plans.apply(change, next.copy(userProfile = VersionRef("profile", 2)))
        }
        assertEquals(plan(), repositories.plans.latest("guest-1", "plan-1"))
        repositories.plans.apply(change, next)
        val (stale, staleNext) = swap(operationId = "stale-operation")
        assertThrows(RevisionConflict::class.java) { repositories.plans.apply(stale, staleNext) }
        assertEquals(next, repositories.plans.latest("guest-1", "plan-1"))
    }
}
