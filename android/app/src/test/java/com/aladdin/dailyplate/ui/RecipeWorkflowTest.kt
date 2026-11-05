package com.aladdin.dailyplate.ui

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.data.local.*
import com.aladdin.dailyplate.ui.menu.*
import com.aladdin.dailyplate.domain.planning.PlanConsumption
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, manifest = Config.NONE)
class RecipeWorkflowTest {
    @Test fun pendingExactCommandSurvivesRestartAndCommittedRetryDoesNotDuplicate() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val d = RecipeDetailTest().detail()
        val command = d.command(0.5, "TEST", "durable-event", recordedAt)
        UserRepositories.open(context).use { it.plans.create(d.plan) }
        // Simulate process death after durable command write, before the database transaction.
        val prefs = context.getSharedPreferences("recipe-consumption", 0)
        assertTrue(prefs.edit().putString("pending", Json.encodeToString(PlanConsumption.serializer(), command)).commit())
        assertEquals(command, RecipeWorkflow(context).pending())
        RecipeWorkflow(context).resume()
        assertNull(RecipeWorkflow(context).pending())
        // Also retry after a committed transaction, before preferences were cleared.
        assertTrue(prefs.edit().putString("pending", Json.encodeToString(PlanConsumption.serializer(), command)).commit())
        RecipeWorkflow(context).resume()
        UserRepositories.open(context).use {
            assertEquals(1, it.foodLogs.recent(d.plan.userId).size)
            assertEquals(0.5, it.plans.latest(d.plan.userId, d.plan.ref.id)!!.items.first().remainingPlannedServings, 0.0)
        }
    }
}
