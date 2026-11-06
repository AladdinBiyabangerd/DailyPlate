package com.aladdin.dailyplate.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.*
import com.aladdin.dailyplate.domain.profile.*
import com.aladdin.dailyplate.domain.policy.NutritionPolicyEngine
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class FuturePlanRepositoryTest : WeeklyPlanningFixture() {
    private lateinit var database: UserDatabase
    private lateinit var repositories: UserRepositories
    private lateinit var future: FuturePlanRepository
    private val p = profile()
    private val q = request(p)
    private val clock = Clock.fixed(Instant.parse("2026-11-05T10:00:00Z"), ZoneOffset.UTC)
    private val effective = CalendarDate("2026-11-06")
    private val bundle = PlanningInputs(VersionRef("active", 1), p.userId, policy, data())
    private lateinit var original: MealPlan

    @Before fun open() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), UserDatabase::class.java)
            .allowMainThreadQueries().build()
        repositories = UserRepositories(database)
        future = FuturePlanRepository(database, clock)
        repositories.profiles.save(p, 0)
        repositories.planGeneration.activate(bundle, 0)
        assertEquals(GenerationStatus.SUCCESS, repositories.planGeneration.generateTest(p.userId, q).status)
        original = repositories.planAcceptance.acceptTest(p.userId, q.requestId, "plan")
    }
    @After fun close() = repositories.close()

    private fun current() = repositories.plans.latest(p.userId, original.ref.id)!!
    private fun change(field: String = "weight", value: String = "70.1"): UserProfile {
        val edit = ProfileEdit.preview(p, ProfileEdit.draft(p).change(field, value).change("measurements_confirmed", "yes"), "", false,
            "Profile updated", q.candidate.at, q.weekStart)
        return repositories.profileEdits.confirm(edit)
    }
    private fun generate(profile: UserProfile, id: String = "new-generation") {
        val result = repositories.planGeneration.generateTest(p.userId, request(profile).copy(requestId = id))
        assertEquals(result.ruleResults.filter { it.status != RuleStatus.PASS }.toString(), GenerationStatus.SUCCESS, result.status)
    }
    private fun command(id: String = "recalculate") = FuturePlanCommand(id, current().ref, "new-generation", effective)
    private fun consume(item: MealPlanItem, share: Double = 0.5, id: String = "actual"): MealPlan {
        val recipe = bundle.data.catalog.recipes.single { it.content.ref == item.recipe }.content
            as com.aladdin.dailyplate.domain.editorial.EditorialContent.RecipeContent
        val log = FoodLog(VersionRef(id, 1), "event-$id", p.userId,
            RecordedAt("${item.date.iso}T10:00:00Z", original.timezone), null, item.recipe,
            item.portionGrams!! * share, WeeklyNutrition.scale(item.nutrientSnapshot, share), EntryMethod.PLAN,
            item.id, SyncStatus.LOCAL, ActualRecipeSnapshot(item.recipe, null, recipe.recipe.ingredients,
                1.0, share, recipe.recipe.yieldCookedGrams, recipe.recipe.instructions, InstructionStatus.VERIFIED),
            false, NutrientBasis.RECIPE_CONFIRMED, null, true)
        return repositories.planConsumption.confirm(PlanConsumption(current().ref, item.id, share, log))
    }

    @Test fun previewAndAcceptancePreserveHistoryPartialPortionsLogsAndShoppingMarks() {
        val marked = original.copy(ref = original.ref.copy(version = 2), shopping = original.shopping.map {
            it.copy(homeQuantity = 1.0, purchasedQuantity = 2.0, planVersion = 2)
        } + ShoppingItem("already-bought", "raw", 50.0, "g", 0.0, 10.0, 2))
        UserSnapshotRepository(database, "plan", MealPlan.serializer(), MealPlan::userId, MealPlan::ref).save(marked, 1)
        val protectedItem = marked.items.first { it.date == effective }
        consume(protectedItem)
        val before = current()
        val logs = repositories.foodLogs.recent(p.userId)
        val edited = change()
        generate(edited)
        val record = future.previewTest(p.userId, command())
        assertEquals(GenerationStatus.SUCCESS, record.calculation.status)
        val preview = record.calculation.preview!!
        assertEquals(before, current()) // Viewing/cancelling performs no plan write.
        assertEquals(logs, repositories.foodLogs.recent(p.userId))
        val next = future.confirmTest(record)
        before.items.filter { it.date.iso < effective.iso || it.id == protectedItem.id }.forEach { old ->
            assertEquals(old, next.items.single { it.id == old.id })
        }
        assertEquals(before, repositories.plans.version(p.userId, before.ref))
        assertEquals(original, repositories.plans.version(p.userId, original.ref))
        assertEquals(edited.ref, next.userProfile)
        assertEquals(edited.ref, next.energyEstimate!!.profile)
        assertEquals(2.0, next.shopping.first { it.foodId == marked.shopping.first().foodId }.purchasedQuantity, 0.0)
        assertEquals(0.0, next.shopping.single { it.foodId == "already-bought" }.quantity, 0.0)
        assertEquals(10.0, next.shopping.single { it.foodId == "already-bought" }.purchasedQuantity, 0.0)
        assertTrue(record.calculation.shoppingDelta.any { it.foodId == "already-bought" && it.quantityDelta == -50.0 })
        val projection = dailyPlanProjection(next, logs, LocalDate.parse(effective.iso))
        assertEquals(protectedItem.nutrientSnapshot.values.getValue("energy_kcal").value!! / 2,
            projection.actual!!.values.getValue("energy_kcal").value!!, 0.0)
        assertNull(projection.remainingEnergy(next.energyEstimate.dailyKcal))
        assertEquals(logs, repositories.foodLogs.recent(p.userId))
        assertEquals(preview.after, next)
        assertEquals(next, FuturePlanRepository(database, clock).confirmTest(record))
        assertThrows(IllegalArgumentException::class.java) { future.confirm(record) }
        assertThrows(IdempotencyConflict::class.java) {
            future.confirmTest(record.copy(calculation = record.calculation.copy(shoppingDelta = emptyList())))
        }
        assertThrows(IdempotencyConflict::class.java) {
            future.previewTest(p.userId, record.command.copy(effectiveFrom = CalendarDate("2026-11-07")))
        }
    }

    @Test fun stalePlanProfileInputsAndDateNeverOverwritePreviewedState() {
        generate(change())
        val record = future.previewTest(p.userId, command())
        consume(current().items.last(), id = "late-log")
        assertThrows(RevisionConflict::class.java) { future.confirmTest(record) }
        assertNull(future.accepted(p.userId, record.command.operationId))
        assertEquals(1, repositories.foodLogs.recent(p.userId).size)
        val fresh = future.previewTest(p.userId, command("fresh"))
        assertThrows(IllegalArgumentException::class.java) {
            FuturePlanRepository(database, Clock.fixed(Instant.parse("2026-11-07T10:00:00Z"), ZoneOffset.UTC)).confirmTest(fresh)
        }
        repositories.planGeneration.activate(bundle.copy(ref = VersionRef("active", 2)), 1)
        assertThrows(RevisionConflict::class.java) { future.confirmTest(fresh) }
        assertThrows(RevisionConflict::class.java) { future.previewTest(p.userId, command("stale-input")) }
    }

    @Test fun concurrentPlanChangeDuringCalculationCannotPersistPreview() {
        generate(change())
        val command = command()
        assertThrows(RevisionConflict::class.java) {
            future.previewTest(p.userId, command) { consume(current().items.last(), id = "race") }
        }
        assertNull(future.record(p.userId, command.operationId))
    }

    @Test fun failureRollsBackAcceptanceAndRetryIsDurableAcrossDatabaseReopen() {
        generate(change())
        val value = future.previewTest(p.userId, command())
        val before = current()
        database.openHelper.writableDatabase.execSQL("""
            CREATE TRIGGER reject_future BEFORE INSERT ON user_snapshots
            WHEN NEW.kind = 'future_plan_acceptance'
            BEGIN SELECT RAISE(ABORT, 'injected failure'); END
        """.trimIndent())
        assertThrows(android.database.sqlite.SQLiteConstraintException::class.java) { future.confirmTest(value) }
        assertEquals(before, current())
        assertNull(future.accepted(p.userId, value.command.operationId))
        database.openHelper.writableDatabase.execSQL("DROP TRIGGER reject_future")
        val next = future.confirmTest(value)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "future-plan-reopen.db"
        context.deleteDatabase(name)
        // Copy the snapshot ledger to a real on-disk DB to verify serialization/restart.
        val rows = database.snapshots().all("plan", p.userId) +
            database.snapshots().all("plan_generation", p.userId) +
            database.snapshots().all("future_plan_preview", p.userId) +
            database.snapshots().all("future_plan_acceptance", p.userId)
        fun disk() = Room.databaseBuilder(context, UserDatabase::class.java, name).allowMainThreadQueries().build()
        try {
            val first = disk()
            rows.forEach { first.snapshots().insert(it) }
            first.close()
            val reopened = disk()
            try {
                assertEquals(next, FuturePlanRepository(reopened, clock).confirmTest(value))
            } finally { reopened.close() }
        } finally { context.deleteDatabase(name) }
    }

    @Test fun currentHardFiltersApplyToPreservedRemainingPortions() {
        val item = current().items.first { it.date == effective }
        consume(item)
        val edit = ProfileEdit.preview(p, ProfileEdit.draft(p).change("weight", "70.1").change("measurements_confirmed", "yes"), "", false,
            "New catalog dislike", q.candidate.at, q.weekStart)
        val edited = edit.after.copy(dislikedRecipeIds = setOf(item.recipe.id))
        repositories.profileEdits.confirm(edit.copy(after = edited,
            assessment = NutritionPolicyEngine.assessCurrent(edited, edited.updatedAt)))
        generate(edited)
        val value = future.previewTest(p.userId, command())
        assertNull(value.calculation.preview)
        assertTrue(value.calculation.rules.any { it.ruleId == "preserved_safety:${item.id}" && it.status == RuleStatus.FAIL })
        assertThrows(IllegalArgumentException::class.java) { future.confirmTest(value) }
        assertEquals(0.5, current().items.single { it.id == item.id }.remainingPlannedServings, 0.0)
    }

    @Test fun foreignOwnerFixtureProductionAndUnconfirmedTransitionAreRejected() {
        val edited = change()
        generate(edited)
        assertThrows(IllegalArgumentException::class.java) { future.preview("guest", command()) }
        assertThrows(IllegalArgumentException::class.java) { future.previewTest("foreign", command()) }
        assertNull(future.record("foreign", "recalculate"))
        assertEquals(original, current())
        val newer = edited.copy(ref = edited.ref.copy(version = 3))
        repositories.profiles.save(newer, 2)
        assertThrows(RevisionConflict::class.java) { future.previewTest(p.userId, command()) }
    }
    @Test fun explicitMealCountAndBudgetScopeChangeRebuildsOnlyFutureSlots() {
        val edit = ProfileEdit.preview(p, ProfileEdit.draft(p).change("meals", "4"), "", false,
            "Four meals with explicitly updated budget scope", q.candidate.at, q.weekStart)
        val edited = edit.after.copy(budget = edit.after.budget.copy(coveredMealSlots = (0..3).map { "slot:$it" }.toSet()))
        repositories.profileEdits.confirm(edit.copy(after = edited,
            assessment = NutritionPolicyEngine.assessCurrent(edited, edited.updatedAt)))
        generate(edited)
        val value = future.previewTest(p.userId, command().copy(effectiveFrom = original.week))
        assertEquals(GenerationStatus.SUCCESS, value.calculation.status)
        assertEquals(28, future.confirmTest(value).items.size)
        assertEquals(21, repositories.plans.version(p.userId, original.ref)!!.items.size)
    }

    @Test fun unavailableProductionGenerationRemainsARejectedPreviewWithoutPlanWrites() {
        val edited = change()
        val production = request(edited).copy(requestId = "production-failure")
        assertNotEquals(GenerationStatus.SUCCESS, repositories.planGeneration.generate(p.userId, production).status)
        val value = future.preview(p.userId, command().copy(generationRequestId = production.requestId))
        assertNull(value.calculation.preview)
        assertThrows(IllegalArgumentException::class.java) { future.confirm(value) }
        assertEquals(original, current())
        assertTrue(repositories.foodLogs.recent(p.userId).isEmpty())
    }

}
