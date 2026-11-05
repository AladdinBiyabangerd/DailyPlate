package com.aladdin.dailyplate.data.local

import android.content.Context
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class PlanGenerationTest : WeeklyPlanningFixture() {
    private lateinit var database: UserDatabase
    private lateinit var repositories: UserRepositories
    private val p = profile()
    private val q = request(p)
    private val bundle = PlanningInputs(VersionRef("active", 1), p.userId, policy, data())

    @Before fun open() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), UserDatabase::class.java)
            .allowMainThreadQueries().build()
        repositories = UserRepositories(database)
        repositories.profiles.save(p, 0)
        repositories.planGeneration.activate(bundle, 0)
    }
    @After fun close() = repositories.close()

    @Test fun successAndExactInputsSurviveRepositoryRecreationAndProfileChange() {
        val result = repositories.planGeneration.generateTest(p.userId, q)
        assertEquals(GenerationStatus.SUCCESS, result.status)
        val restored = PlanGenerationRepository(database)
        val receipt = restored.receipt(p.userId, q.requestId)!!
        assertEquals(bundle, receipt.inputs)
        assertEquals(p, receipt.profile)
        assertEquals(result, receipt.result)
        repositories.profiles.save(p.copy(ref = p.ref.copy(version = 2)), 1)
        assertEquals(result, restored.generateTest(p.userId, q))
        assertNull(restored.receipt("other", q.requestId))
        assertTrue(repositories.foodLogs.recent(p.userId).isEmpty())
        assertThrows(IdempotencyConflict::class.java) { restored.generateTest(p.userId, q.copy(seed = 5)) }
        assertThrows(IdempotencyConflict::class.java) { restored.generate(p.userId, q) }
    }

    @Test fun staleProfilePolicyAndCatalogNeverPersistSuccess() {
        repositories.profiles.save(p.copy(ref = p.ref.copy(version = 2)), 1)
        val staleProfile = repositories.planGeneration.generateTest(p.userId, q)
        assertEquals(GenerationStatus.STALE_INPUT, staleProfile.status)
        assertNull(staleProfile.plan)
        val fresh = q.copy(candidate = q.candidate.copy(profile = p.ref.copy(version = 2)))
        for ((index, candidate) in listOf(
            fresh.candidate.copy(policy = policy.ref.copy(version = 2)),
            fresh.candidate.copy(catalog = bundle.data.catalog.ref.copy(version = 2))).withIndex()) {
            assertEquals(GenerationStatus.STALE_INPUT, repositories.planGeneration.generateTest(p.userId,
                fresh.copy(candidate = candidate, requestId = "stale-$index")).status)
        }
    }

    @Test fun productionGateAndFailureReplayRemainDurable() {
        val result = repositories.planGeneration.generate(p.userId, q)
        assertNotEquals(GenerationStatus.SUCCESS, result.status)
        assertFalse(result.testOnly)
        repositories.planGeneration.activate(bundle.copy(ref = VersionRef("active", 2), policy = null), 1)
        assertEquals(result, PlanGenerationRepository(database).generate(p.userId, q))
        assertThrows(RevisionConflict::class.java) { repositories.planGeneration.activate(bundle, 0) }
    }

    @Test fun databaseCloseAndReopenRetainsEnvelope() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "generation-restart-test.db"
        context.deleteDatabase(name)
        fun disk() = Room.databaseBuilder(context, UserDatabase::class.java, name).allowMainThreadQueries().build()
        try {
            val first = UserRepositories(disk())
            val expected = try {
                first.profiles.save(p, 0)
                first.planGeneration.activate(bundle, 0)
                first.planGeneration.generateTest(p.userId, q).also {
                    first.planAcceptance.acceptTest(p.userId, q.requestId, "disk-plan")
                }
            } finally { first.close() }
            val reopened = UserRepositories(disk())
            try {
                assertEquals(expected, reopened.planGeneration.generateTest(p.userId, q))
                assertEquals(bundle, reopened.planGeneration.receipt(p.userId, q.requestId)!!.inputs)
                assertEquals(reopened.plans.latest(p.userId, "disk-plan"),
                    reopened.planAcceptance.acceptTest(p.userId, q.requestId, "disk-plan"))
            } finally { reopened.close() }
        } finally { context.deleteDatabase(name) }
    }

    @Test fun changesDuringGenerationAreRejectedBeforeWriting() {
        for (change in 0..2) {
            val currentProfile = repositories.profiles.latest(p.userId, p.ref.id)!!
            val request = q.copy(requestId = "race-$change", candidate = q.candidate.copy(profile = currentProfile.ref))
            val result = repositories.planGeneration.generateTest(p.userId, request) {
                if (change == 0) repositories.profiles.save(p.copy(ref = p.ref.copy(version = 2)), 1)
                else {
                    val current = repositories.planGeneration.currentInputs(p.userId)!!
                    repositories.planGeneration.activate(current.copy(ref = current.ref.copy(version = current.ref.version + 1),
                        policy = if (change == 1) null else current.policy,
                        data = if (change == 2) current.data.copy(catalog = current.data.catalog.copy(
                            ref = VersionRef("new-catalog", 1))) else current.data), current.ref.version)
                }
            }
            assertEquals(GenerationStatus.STALE_INPUT, result.status)
            assertNull(result.plan)
        }
    }

    @Test fun writeFailureLeavesNoReceiptAndRetryCanSucceed() {
        database.openHelper.writableDatabase.execSQL("""
            CREATE TRIGGER reject_generation BEFORE INSERT ON user_snapshots
            WHEN NEW.kind = 'plan_generation'
            BEGIN SELECT RAISE(ABORT, 'injected failure'); END
        """.trimIndent())
        assertThrows(android.database.sqlite.SQLiteConstraintException::class.java) {
            repositories.planGeneration.generateTest(p.userId, q)
        }
        assertNull(repositories.planGeneration.receipt(p.userId, q.requestId))
        database.openHelper.writableDatabase.execSQL("DROP TRIGGER reject_generation")
        assertEquals(GenerationStatus.SUCCESS, repositories.planGeneration.generateTest(p.userId, q).status)
    }
    @Test fun explicitAcceptanceMapsFrozenResultAndReplaysInitialVersion() {
        val result = repositories.planGeneration.generateTest(p.userId, q)
        assertNull(repositories.plans.latest(p.userId, "accepted"))
        assertThrows(IllegalArgumentException::class.java) {
            repositories.planAcceptance.accept(p.userId, q.requestId, "accepted")
        }
        val plan = repositories.planAcceptance.acceptTest(p.userId, q.requestId, "accepted")
        val generated = result.plan!!
        assertEquals(generated.meals.size, plan.items.size)
        generated.meals.zip(plan.items).forEach { (meal, item) ->
            assertEquals(meal.day, item.date)
            assertEquals(meal.recipe, item.recipe)
            assertEquals(meal.portionGrams, item.portionGrams)
            assertEquals(meal.nutrients, item.nutrientSnapshot)
            assertEquals(1.0, item.remainingPlannedServings, 0.0)
            assertEquals(PlanItemStatus.PLANNED, item.status)
            assertTrue(item.consumedLogIds.isEmpty())
        }
        assertEquals(generated.weeklyNutrients, plan.nutrientSnapshot)
        assertEquals(generated.targetKcal, plan.energyEstimate!!.dailyKcal, 0.0)
        assertEquals(generated.shoppingCost.lines.size, plan.shopping.size)
        generated.shoppingCost.lines.zip(plan.shopping).forEach { (line, item) ->
            assertEquals(line.requiredEdibleGrams.toDouble(), item.quantity, 0.0)
            assertEquals(line.product.foodId, item.foodId)
        }
        val unaccepted = q.copy(requestId = "profile-stale")
        repositories.planGeneration.generateTest(p.userId, unaccepted)
        repositories.profiles.save(p.copy(ref = p.ref.copy(version = 2)), 1)
        assertThrows(RevisionConflict::class.java) {
            repositories.planAcceptance.acceptTest(p.userId, unaccepted.requestId, "stale-plan")
        }
        assertNull(repositories.plans.latest(p.userId, "stale-plan"))
        assertEquals(plan, PlanAcceptanceRepository(database).acceptTest(p.userId, q.requestId, "accepted"))
        assertThrows(IdempotencyConflict::class.java) {
            repositories.planAcceptance.acceptTest(p.userId, q.requestId, "different")
        }
        assertNull(repositories.planAcceptance.receipt("other", q.requestId))
        assertTrue(repositories.foodLogs.recent(p.userId).isEmpty())
    }

    @Test fun acceptanceRejectsStaleInputsAndOccupiedPlanWithoutChangingHistory() {
        repositories.planGeneration.generateTest(p.userId, q)
        val original = repositories.planAcceptance.acceptTest(p.userId, q.requestId, "occupied")
        val second = q.copy(requestId = "second")
        repositories.planGeneration.generateTest(p.userId, second)
        assertThrows(RevisionConflict::class.java) {
            repositories.planAcceptance.acceptTest(p.userId, second.requestId, "occupied")
        }
        repositories.planGeneration.activate(bundle.copy(ref = VersionRef("active", 2)), 1)
        assertThrows(RevisionConflict::class.java) {
            repositories.planAcceptance.acceptTest(p.userId, second.requestId, "new")
        }
        assertNull(repositories.plans.latest(p.userId, "new"))
        assertEquals(original, repositories.plans.latest(p.userId, "occupied"))
        assertNull(repositories.planAcceptance.receipt(p.userId, second.requestId))
    }

    @Test fun acceptanceFailureRollsBackPlanAndCanRetry() {
        repositories.planGeneration.generateTest(p.userId, q)
        database.openHelper.writableDatabase.execSQL("""
            CREATE TRIGGER reject_acceptance BEFORE INSERT ON user_snapshots
            WHEN NEW.kind = 'plan_acceptance'
            BEGIN SELECT RAISE(ABORT, 'injected failure'); END
        """.trimIndent())
        assertThrows(android.database.sqlite.SQLiteConstraintException::class.java) {
            repositories.planAcceptance.acceptTest(p.userId, q.requestId, "accepted")
        }
        assertNull(repositories.plans.latest(p.userId, "accepted"))
        assertNull(repositories.planAcceptance.receipt(p.userId, q.requestId))
        database.openHelper.writableDatabase.execSQL("DROP TRIGGER reject_acceptance")
        assertNotNull(repositories.planAcceptance.acceptTest(p.userId, q.requestId, "accepted"))
    }

    @Test fun failedOrForeignGenerationCannotBeAccepted() {
        repositories.planGeneration.generate(p.userId, q)
        assertThrows(IllegalArgumentException::class.java) {
            repositories.planAcceptance.accept(p.userId, q.requestId, "failed")
        }
        assertThrows(IllegalArgumentException::class.java) {
            repositories.planAcceptance.acceptTest("other", q.requestId, "foreign")
        }
        assertNull(repositories.plans.latest(p.userId, "failed"))
    }

}
