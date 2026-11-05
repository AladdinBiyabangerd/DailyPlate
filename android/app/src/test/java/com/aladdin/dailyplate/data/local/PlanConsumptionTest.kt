package com.aladdin.dailyplate.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class PlanConsumptionTest {
    private lateinit var database: UserDatabase
    private lateinit var repositories: UserRepositories
    private val original = plan().let { p -> p.copy(items = p.items.map { i -> i.copy(
        nutrientSnapshot = NutrientSnapshot(mapOf("energy_kcal" to NutrientAmount(400.0, NutrientUnit.KCAL),
            "protein_g" to NutrientAmount(null, NutrientUnit.GRAM)))) }) }
    private fun command() = PlanConsumption(original.ref, "item-a", 0.5, log().copy(
        actualRecipeSnapshot = log().actualRecipeSnapshot!!.copy(consumedShare = 0.125),
        nutrientSnapshot = NutrientSnapshot(mapOf("energy_kcal" to NutrientAmount(200.0, NutrientUnit.KCAL),
            "protein_g" to NutrientAmount(null, NutrientUnit.GRAM)))))

    @Before fun open() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), UserDatabase::class.java)
            .allowMainThreadQueries().build()
        repositories = UserRepositories(database)
        repositories.plans.create(original)
    }
    @After fun close() = repositories.close()

    @Test fun viewingDoesNotLogAndHalfPortionIsNotCountedTwice() {
        val day = LocalDate.parse(date.iso)
        val preview = dailyPlanProjection(original, emptyList(), day)
        assertNull(preview.actual)
        assertEquals(400.0, preview.projected!!.values.getValue("energy_kcal").value!!, 0.0)
        assertTrue(repositories.foodLogs.recent(original.userId).isEmpty())
        val next = repositories.planConsumption.confirm(command())
        assertEquals(0.5, next.items.first().remainingPlannedServings, 0.0)
        assertEquals(setOf("log-1"), next.items.first().consumedLogIds)
        assertEquals(original.items[1], next.items[1])
        assertEquals(original, repositories.plans.version(original.userId, original.ref))
        val forecast = dailyPlanProjection(next, repositories.foodLogs.recent(original.userId), day)
        assertEquals(200.0, forecast.actual!!.values.getValue("energy_kcal").value!!, 0.0)
        assertEquals(200.0, forecast.remainingPlan!!.values.getValue("energy_kcal").value!!, 0.0)
        assertEquals(400.0, forecast.projected!!.values.getValue("energy_kcal").value!!, 0.0)
        assertNull(forecast.projected.values.getValue("protein_g").value)
        assertNull(forecast.remainingEnergy(1900.0))
        assertEquals(1700.0, forecast.copy(diaryComplete = true).remainingEnergy(1900.0)!!, 0.0)
    }

    @Test fun durableReceiptSurvivesNewRepositoryAndRejectsDifferentPayload() {
        val result = repositories.planConsumption.confirm(command())
        assertEquals(result, PlanConsumptionRepository(database).confirm(command()))
        assertEquals(1, repositories.foodLogs.recent(original.userId).size)
        assertThrows(IdempotencyConflict::class.java) {
            repositories.planConsumption.confirm(command().copy(consumedServings = 0.25))
        }
        assertThrows(RevisionConflict::class.java) {
            repositories.planConsumption.confirm(command().copy(log = command().log.copy(
                ref = VersionRef("log-2", 1), clientEventId = "event-2")))
        }
        assertEquals(result, repositories.plans.latest(original.userId, original.ref.id))
    }

    @Test fun receiptFailureRollsBackLogEventAndPlan() {
        database.openHelper.writableDatabase.execSQL("""
            CREATE TRIGGER reject_consumption BEFORE INSERT ON user_snapshots
            WHEN NEW.kind = 'plan_consumption'
            BEGIN SELECT RAISE(ABORT, 'injected write failure'); END
        """.trimIndent())
        assertThrows(android.database.sqlite.SQLiteConstraintException::class.java) {
            repositories.planConsumption.confirm(command())
        }
        assertEquals(original, repositories.plans.latest(original.userId, original.ref.id))
        assertTrue(repositories.foodLogs.recent(original.userId).isEmpty())
        assertNull(database.snapshots().event(original.userId, "event-1"))
        database.openHelper.writableDatabase.execSQL("DROP TRIGGER reject_consumption")
        repositories.planConsumption.confirm(command())
    }

    @Test fun editedAndDeletedLogDoNotResurrectConsumedPlanOrOldLogRevision() {
        val next = repositories.planConsumption.confirm(command())
        val revised = command().log.copy(ref = VersionRef("log-1", 2), nutrientSnapshot = NutrientSnapshot(
            mapOf("energy_kcal" to NutrientAmount(100.0, NutrientUnit.KCAL))))
        val day = LocalDate.parse(date.iso)
        val edited = dailyPlanProjection(next, listOf(command().log, revised), day)
        assertEquals(300.0, edited.projected!!.values.getValue("energy_kcal").value!!, 0.0)
        val deleted = dailyPlanProjection(next, listOf(command().log, revised.copy(deleted = true)), day)
        assertNull(deleted.actual)
        assertEquals(200.0, deleted.projected!!.values.getValue("energy_kcal").value!!, 0.0)
    }

    @Test fun invalidPortionAndForeignOwnerCannotWrite() {
        listOf(command().copy(consumedServings = 2.0), command().copy(log = command().log.copy(userId = "other")),
            command().copy(log = command().log.copy(actualRecipeSnapshot = log().actualRecipeSnapshot)),
            command().copy(log = command().log.copy(nutrientSnapshot = nutrients))).forEach { bad ->
            assertThrows(IllegalArgumentException::class.java) { bad.applyTo(original) }
        }
        assertEquals(original, repositories.plans.latest(original.userId, original.ref.id))
        assertTrue(repositories.foodLogs.recent(original.userId).isEmpty())
    }
}
