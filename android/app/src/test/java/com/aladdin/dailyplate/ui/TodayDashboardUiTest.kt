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
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.entry.*
import com.aladdin.dailyplate.ui.menu.*
import com.aladdin.dailyplate.ui.today.*
import com.aladdin.dailyplate.ui.theme.*
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.*
import java.io.File
import java.time.ZoneId
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TodayDashboardUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun pictureCardsOpenExactExistingDetailsWithoutRecording() {
        for ((language, scale) in listOf("az" to 1f, "az" to 1.5f, "en" to 1.5f, "ru" to 1.5f)) {
            val detail = RecipeDetailTest().detail()
            val meal = TodayMeal(detail.plan.ref.id, detail.item, when (language) {
                "en" -> "Chicken with vegetables"; "ru" -> "Курица с овощами"; else -> "Tərəvəzli toyuq" })
            val log = PortionDraft(when (language) { "en" -> "Home meal"; "ru" -> "Домашняя еда"; else -> "Evdə hazırlanan yemək" },
                RecordedAt("2026-11-05T10:00:00Z", "Asia/Baku"), EntryMethod.MANUAL,
                quantity = 150.0, unit = EntryUnit.GRAM, source = "TEST label",
                nutrientsPerBasis = NutrientSnapshot(mapOf("energy" to NutrientAmount(100.0, NutrientUnit.KCAL))))
                .confirm("local-guest", "dashboard-log")
            val config = android.content.res.Configuration(compose.activity.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(language)); fontScale = scale
            }
            val localized = compose.activity.createConfigurationContext(config)
            var openedPlan: TodayMeal? = null
            var openedLog: String? = null
            var confirmations = 0
            var addCount = 0
            compose.activityRule.scenario.onActivity { activity -> activity.setContent {
                CompositionLocalProvider(LocalContext provides localized, LocalConfiguration provides config,
                    LocalDensity provides Density(LocalDensity.current.density, scale)) {
                    QidaTheme { Surface(color = MaterialTheme.colorScheme.background) {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                            verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                            key(language, scale) {
                                var route by remember { mutableStateOf("home") }
                                when (route) {
                                    "recipe" -> RecipeDetailContent(detail) { confirmations++ }
                                    "log" -> LogEditor(log, false, { route = "home" }, { confirmations++ }, { confirmations++ })
                                    else -> TodayDashboard(listOf(log), listOf(meal), "2026-11-05", ZoneId.of("Asia/Baku"),
                                        { addCount++ }, {}, { openedLog = it; route = "log" }, { openedPlan = it; route = "recipe" })
                                }
                            }
                        }
                    } }
                }
            } }
            compose.onNodeWithText(localized.getString(R.string.td_greeting)).performScrollTo().assertIsDisplayed()
            capture("today-dashboard-$language-$scale")
            compose.onNodeWithText(localized.getString(R.string.td_remaining_unknown)).performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("today-log-" + log.ref.id).performScrollTo()
            capture("today-dashboard-log-card-$language-$scale")
            compose.onNodeWithTag("today-log-" + log.ref.id).performClick()
            Assert.assertEquals(log.ref.id, openedLog)
            compose.onNodeWithText(localized.getString(R.string.pc_energy) + ": 150.0 kcal").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText(localized.getString(R.string.td_multiple)).performScrollTo().assertIsDisplayed()
            capture("today-log-detail-$language-$scale")
            compose.onNodeWithText(localized.getString(R.string.back)).performScrollTo().performClick()
            compose.onNodeWithTag("today-plan-" + meal.item.id).performScrollTo().performClick()
            Assert.assertEquals(meal, openedPlan)
            compose.onNodeWithText(localized.getString(R.string.rd_steps)).performScrollTo().assertIsDisplayed()
            Assert.assertEquals(0, confirmations)
            Assert.assertEquals(0, addCount)
        }
    }

    @Test fun emptyDayOffersAddWithoutInventingMealsOrEnergy() {
        var added = false
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            QidaTheme { Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                TodayDashboard(emptyList(), emptyList(), "2026-11-05", ZoneId.of("Asia/Baku"),
                    { added = true }, {}, { error("No log exists") }, { error("No plan exists") })
            } }
        } }
        compose.onNodeWithTag("today-hero").performScrollTo().performClick()
        Assert.assertTrue(added)
        capture("today-dashboard-empty")
    }

    private fun capture(name: String) {
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val file = File("build/reports/k10/$name.png")
            file.parentFile!!.mkdirs()
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
}
