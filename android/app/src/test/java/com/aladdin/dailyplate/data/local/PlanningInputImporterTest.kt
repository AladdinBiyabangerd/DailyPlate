package com.aladdin.dailyplate.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.VersionRef
import com.aladdin.dailyplate.domain.planning.CandidateCatalog
import com.aladdin.dailyplate.domain.planning.WeeklyPlanningData
import com.aladdin.dailyplate.domain.planning.WeeklyPlanningFixture
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlinx.serialization.json.Json

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class PlanningInputImporterTest : WeeklyPlanningFixture() {
    private lateinit var database: UserDatabase
    private lateinit var repositories: UserRepositories
    private val json = Json(snapshotJson) { allowStructuredMapKeys = true }
    private fun pack(version: Long = 1) = PlanningInputPackage(1, VersionRef("planning-import", version), null,
        WeeklyPlanningData(CandidateCatalog(VersionRef("empty", version), emptyList(), emptyMap()),
            emptyMap(), emptyList(), null))
    private fun payload(pack: PlanningInputPackage) = json.encodeToString(PlanningInputPackage.serializer(), pack)

    @Before fun open() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), UserDatabase::class.java)
            .allowMainThreadQueries().build()
        repositories = UserRepositories(database)
    }
    @After fun close() = repositories.close()

    @Test fun replacementAndReplayPreserveExactSelectionAndOwner() {
        val first = repositories.planningImports.importJson("one", payload(pack()), 0)
        val next = repositories.planningImports.importJson("one", payload(pack(2)), 1)
        assertEquals(first, PlanningInputImporter(database).importJson("one", payload(pack()), 0))
        assertEquals(next, repositories.planGeneration.currentInputs("one"))
        assertNull(repositories.planGeneration.currentInputs("two"))
        val other = repositories.planningImports.importJson("two", payload(pack()), 0)
        assertEquals("two", other.userId)
        assertEquals(pack(2).data, next.data)
        assertNull(next.policy)
        assertTrue(repositories.plans.current("one").isEmpty())
        assertTrue(repositories.foodLogs.recent("one").isEmpty())
    }

    @Test fun conflictingStaleDowngradedAndMalformedImportsPreservePreviousInputs() {
        val initial = repositories.planningImports.importJson("one", payload(pack(2)), 0)
        assertThrows(IdempotencyConflict::class.java) {
            repositories.planningImports.importJson("one", payload(pack(2).copy(data = pack(3).data)), 1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            repositories.planningImports.importJson("one", payload(pack()), 1)
        }
        assertThrows(RevisionConflict::class.java) {
            repositories.planningImports.importJson("one", payload(pack(3)), 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            repositories.planningImports.importJson("one", payload(pack(3)).replace("\"schemaVersion\":1", "\"schemaVersion\":2"), 1)
        }
        assertEquals(initial, repositories.planGeneration.currentInputs("one"))
        assertEquals(2L, repositories.planningImports.importJson("one", payload(pack(3)), 1).ref.version)
    }

    @Test fun fixtureEditorialAndFixturePricesCannotBeImported() {
        val initial = repositories.planningImports.importJson("one", payload(pack()), 0)
        assertThrows(IllegalArgumentException::class.java) {
            repositories.planningImports.importJson("one", payload(pack(2).copy(data = data())), 1)
        }
        assertThrows(IllegalArgumentException::class.java) {
            repositories.planningImports.importJson("one", payload(pack(2).copy(
                data = pack(2).data.copy(prices = data().prices))), 1)
        }
        assertEquals(initial, repositories.planGeneration.currentInputs("one"))
    }

    @Test fun diskReopenRetainsReceiptWithoutReactivatingOlderInputs() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val name = "planning-input-import-test.db"
        context.deleteDatabase(name)
        fun disk() = UserRepositories(Room.databaseBuilder(context, UserDatabase::class.java, name)
            .allowMainThreadQueries().build())
        try {
            val original = disk().use { r ->
                val first = r.planningImports.importJson("one", payload(pack()), 0)
                r.planningImports.importJson("one", payload(pack(2)), 1)
                first
            }
            disk().use { r ->
                assertEquals(original, r.planningImports.importJson("one", payload(pack()), 0))
                assertEquals(pack(2).data, r.planGeneration.currentInputs("one")!!.data)
            }
        } finally { context.deleteDatabase(name) }
    }

    @Test fun receiptFailureRollsBackActivationAndAllowsRetry() {
        database.openHelper.writableDatabase.execSQL("""
            CREATE TRIGGER reject_import BEFORE INSERT ON user_snapshots
            WHEN NEW.kind = 'planning_import' BEGIN SELECT RAISE(ABORT, 'injected receipt failure'); END
        """.trimIndent())
        assertThrows(Exception::class.java) { repositories.planningImports.importJson("one", payload(pack()), 0) }
        assertNull(repositories.planGeneration.currentInputs("one"))
        database.openHelper.writableDatabase.execSQL("DROP TRIGGER reject_import")
        assertEquals(1L, repositories.planningImports.importJson("one", payload(pack()), 0).ref.version)
    }
}
