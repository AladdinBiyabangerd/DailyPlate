package com.aladdin.dailyplate.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import com.aladdin.dailyplate.MainActivity
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.data.local.UserRepositories
import com.aladdin.dailyplate.data.local.OnboardingStore
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.ui.menu.*
import com.aladdin.dailyplate.ui.theme.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.*
import java.io.File
import java.time.LocalDate
import java.util.Locale
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class WeeklyMenuUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private var localized: android.content.Context? = null
    private val today = LocalDate.parse("2026-11-05")
    private fun t(id: Int) = localized!!.getString(id)
    private fun fixture(count: Int): Pair<MealPlan, Map<VersionRef, Recipe>> {
        val d = RecipeDetailTest().detail()
        val items = (0 until count).map { n -> d.item.copy(id = "slot-$n", date = CalendarDate(today.toString()),
            mealSlot = n.toString(), recipe = VersionRef("fixture-recipe-$n", 1),
            customization = if (n == 1) VersionRef("fixture-custom", 1) else null,
            nutrientSnapshot = if (n == 1) NutrientSnapshot(emptyMap()) else d.item.nutrientSnapshot,
            status = when (n) { 1 -> PlanItemStatus.PARTIALLY_CONSUMED; 2 -> PlanItemStatus.CONSUMED; 3 -> PlanItemStatus.SKIPPED; else -> PlanItemStatus.PLANNED },
            remainingPlannedServings = when (n) { 1 -> 0.5; 2, 3 -> 0.0; else -> 1.0 }) }
        val recipes = items.associate { it.recipe to d.recipe.copy(ref = it.recipe,
            names = LocalizedText("TEST · Tərəvəzli toyuq və düyü ${it.mealSlot}",
                "TEST · Chicken with vegetables and rice ${it.mealSlot}",
                "TEST · Курица с овощами и рисом ${it.mealSlot}"), time = PreparationTime(25, 10, 15)) }
        return d.plan.copy(items = items) to recipes
    }
    private fun show(language: String, scale: Float, content: @Composable () -> Unit) {
        val config = android.content.res.Configuration(compose.activity.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(language)); fontScale = scale
        }
        localized = compose.activity.createConfigurationContext(config)
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            CompositionLocalProvider(LocalContext provides localized!!, LocalConfiguration provides config,
                LocalDensity provides Density(LocalDensity.current.density, scale)) {
                QidaTheme { Surface(color = MaterialTheme.colorScheme.background) {
                    key(language, scale, content) {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                            verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                            Spacer(Modifier.height(QidaSpacing.small).testTag("d03-top"))
                            QidaHeading(t(R.string.menu))
                            content()
                            Spacer(Modifier.height(QidaSpacing.small).testTag("d03-bottom"))
                        }
                    }
                } }
            }
        } }
        compose.waitForIdle()
    }
    private fun capture(state: String, language: String, scale: Float, middle: Boolean = false) {
        for (position in if (middle) listOf("top", "middle", "bottom") else listOf("top", "bottom")) {
            if (position == "middle") compose.onNodeWithTag("menu-recipe-slot-2").performScrollTo()
            else compose.onNodeWithTag("d03-$position").performScrollTo()
            compose.runOnIdle {
                val view = compose.activity.window.decorView
                val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                view.draw(Canvas(bitmap))
                val file = File("build/reports/k10/d03-$state-$language-$scale-$position.png")
                file.parentFile!!.mkdirs()
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                bitmap.recycle()
            }
        }
    }

    @Test fun datedMenusPreserveSlotsStatusesUnknownValuesAndReadOnlyActions() {
        for (language in listOf("az", "en", "ru")) for (scale in listOf(1f, 1.5f)) {
            for (count in 3..5) {
                val (plan, recipes) = fixture(count)
                var opened: MealPlanItem? = null
                var ate: MealPlanItem? = null
                show(language, scale) { DatedMenu(plan, recipes, today, openRecipe = { opened = it }, eat = { ate = it }) }
                compose.onNodeWithTag("menu-day-$today").assertIsSelected()
                compose.onAllNodesWithText(t(R.string.today)).assertCountEquals(1)
                compose.onAllNodesWithText(t(R.string.wm_energy_unknown)).assertCountEquals(1)
                compose.onAllNodesWithText(t(R.string.wm_time_unknown)).assertCountEquals(1)
                for (n in 0 until count) {
                    compose.onNodeWithTag("menu-recipe-slot-$n").performScrollTo().assertIsDisplayed()
                    if (n in listOf(2, 3)) compose.onNodeWithTag("menu-eat-slot-$n").assertDoesNotExist()
                    else compose.onNodeWithTag("menu-eat-slot-$n").performScrollTo().assertIsDisplayed()
                }
                capture(when (count) { 3 -> "three-meals"; 4 -> "four-meals"; else -> "five-meals" }, language, scale, count > 3)
                compose.onNodeWithTag("menu-recipe-slot-0").performScrollTo().performClick()
                compose.runOnIdle { assertEquals(plan.items[0], opened); assertNull(ate) }
                compose.onNodeWithTag("menu-eat-slot-1").performScrollTo().performClick()
                compose.runOnIdle { assertEquals(plan.items[1], ate) }
                val next = today.plusDays(1)
                compose.onNodeWithTag("menu-day-$next").performScrollTo().performClick()
                compose.onNodeWithTag("menu-day-$next").assertIsSelected()
                compose.onNodeWithTag("menu-day-$today").assertIsNotSelected()
                compose.onNodeWithText(t(R.string.wm_empty_day)).performScrollTo().assertIsDisplayed()
                capture("empty-day", language, scale)
                // Another populated day proves selection filters cards instead of changing the plan.
                val other = plan.copy(items = plan.items + plan.items[0].copy(id = "other-day", date = CalendarDate(next.toString())))
                show(language, scale) { DatedMenu(other, recipes, today) }
                compose.onNodeWithTag("menu-day-$next").performScrollTo().performClick()
                compose.onNodeWithTag("menu-recipe-slot-0").assertDoesNotExist()
                compose.onNodeWithTag("menu-recipe-other-day").performScrollTo().assertIsDisplayed()
                capture("selected-day", language, scale)
            }
            var confirmed: com.aladdin.dailyplate.domain.planning.PlanConsumption? = null
            val d = RecipeDetailTest().detail()
            show(language, scale) { RecipeDetailContent(d, startEating = true) { confirmed = it } }
            compose.onNodeWithText(t(R.string.pd_preview)).performScrollTo().assertIsDisplayed()
            assertNull(confirmed)
            capture("ate", language, scale)
            compose.onNodeWithText(t(R.string.pd_preview)).performScrollTo().performClick()
            assertNull(confirmed)
            compose.onNodeWithText(t(R.string.mn_cancel)).performScrollTo().performClick()
            assertNull(confirmed)
        }
        val worker = Executors.newSingleThreadExecutor()
        try { worker.submit {
            UserRepositories.open(compose.activity.applicationContext).use {
                assertTrue(it.plans.current("local-guest").isEmpty())
                assertTrue(it.foodLogs.recent("local-guest").isEmpty())
            }
        }.get() } finally { worker.shutdown() }
    }

    @Test fun unavailableEmptyLoadingErrorAndMissingProfileHaveHonestRecovery() {
        // Real profile and repository loader; no fabricated accepted plan is inserted.
        val worker = Executors.newSingleThreadExecutor()
        try { worker.submit {
            val draft = readyOnboarding().copy(id = "weekly-menu-ui")
            OnboardingStore(compose.activity.applicationContext).apply { queueSave(draft).get() }.confirm(draft, "az")
        }.get() } finally { worker.shutdown() }
        for (language in listOf("az", "en", "ru")) for (scale in listOf(1f, 1.5f)) {
            var retried = false
            var profile = false
            show(language, scale) { MenuScreen { profile = true } }
            compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.mn_unavailable)).fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText(t(R.string.mn_unavailable)).performScrollTo().assertIsDisplayed()
            capture("empty-plan", language, scale)
            compose.onNodeWithText(t(R.string.mn_choices)).performScrollTo().performClick()
            assertTrue(profile)
            show(language, scale) { MenuUnavailable(hasPlan = true) }
            compose.onNodeWithText(t(R.string.wm_empty)).assertDoesNotExist()
            capture("unavailable", language, scale)
            show(language, scale) { MenuLoadState(SurfaceState.Loading) }
            compose.onNodeWithText(t(R.string.loading)).performScrollTo().assertIsDisplayed()
            capture("loading", language, scale)
            show(language, scale) { MenuLoadState(SurfaceState.Error) { retried = true } }
            capture("error", language, scale)
            compose.onNodeWithText(t(R.string.retry)).performScrollTo().performClick()
            assertTrue(retried)
            show(language, scale) { MenuMissingProfile { profile = true } }
            capture("missing-profile", language, scale)
        }
    }
}
