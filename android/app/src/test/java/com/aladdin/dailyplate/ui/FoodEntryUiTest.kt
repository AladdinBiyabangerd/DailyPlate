package com.aladdin.dailyplate.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.setContent
import com.aladdin.dailyplate.MainActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.entry.confirm
import com.aladdin.dailyplate.ui.entry.FoodEntryScreen
import com.aladdin.dailyplate.ui.theme.*
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class FoodEntryUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val base get() = ApplicationProvider.getApplicationContext<Application>()
    private fun click(label: String) {
        val node = compose.onAllNodesWithText(label).onFirst()
        if (label != "Əlavə et") node.performScrollTo()
        node.performClick()
    }
    private fun enter(label: String, text: String) = compose.onNodeWithText(label).performScrollTo().performTextInput(text)
    private fun capture(name: String) {
        compose.mainClock.advanceTimeBy(500); compose.waitForIdle()
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val file = File("build/reports/k10/$name.png")
            requireNotNull(file.parentFile).mkdirs()
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
    @Test fun approvedEntryAndResultLayouts() {
        var language by mutableStateOf("az")
        var scale by mutableFloatStateOf(1f)
        var result by mutableStateOf(false)
        var selected by mutableStateOf(false)
        compose.runOnUiThread { compose.activity.setContent {
            val config = android.content.res.Configuration(base.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(language)); fontScale = scale
            }
            val context = base.createConfigurationContext(config)
            CompositionLocalProvider(LocalContext provides context, LocalConfiguration provides config,
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(base.resources.displayMetrics.density, scale)) {
                QidaTheme { androidx.compose.material3.Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.background) {
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                        verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                        if (!result) com.aladdin.dailyplate.ui.entry.EntryLanding { }
                        else {
                            QidaHeading(context.getString(R.string.fe_title))
                            com.aladdin.dailyplate.ui.entry.FoodResultCard(
                                com.aladdin.dailyplate.domain.Food(
                                    com.aladdin.dailyplate.domain.VersionRef("preview", 1),
                                    com.aladdin.dailyplate.domain.LocalizedText("Düyü"), emptyList(),
                                    "source", "DB", "cooked", com.aladdin.dailyplate.domain.NutrientSnapshot(emptyMap()),
                                    emptySet(), null, "v1", null, com.aladdin.dailyplate.domain.ContentStatus.DRAFT),
                                when(language) { "en" -> "Cooked long-grain rice without added salt"; "ru" -> "Варёный длиннозёрный рис без добавления соли"; else -> "Duz əlavə edilmədən bişmiş uzun düyü" }) { selected = true }
                        }
                    }
                } }
            }
        } }
        for (lang in listOf("az", "en", "ru")) for (size in listOf(1f, 1.5f)) {
            compose.runOnIdle { language = lang; scale = size; result = false }
            capture("approved-entry-$lang-$size")
            compose.runOnIdle { result = true }
            capture("approved-result-$lang-$size")
            val context = base.createConfigurationContext(android.content.res.Configuration(base.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(lang))
            })
            compose.onNodeWithText(context.getString(R.string.fe_unknown_value)).assertExists()
            click(context.getString(R.string.fe_choose))
            compose.runOnIdle { org.junit.Assert.assertTrue(selected); selected = false }
        }
    }

    @Test fun todaySummaryAzLarge() = renderToday("az", false)
    @Test fun todaySummaryEnLarge() = renderToday("en", false)
    @Test fun todaySummaryRuLarge() = renderToday("ru", false)
    @Test fun todayEditorAzLarge() = renderToday("az", true)
    @Test fun todayEditorEnLarge() = renderToday("en", true)
    @Test fun todayEditorRuLarge() = renderToday("ru", true)
    @Test fun todayRecipeWithoutGramsUsesActualBatchShare() = renderToday("az", true, true)
    private fun renderToday(language: String, editor: Boolean, recipe: Boolean = false) {
        val log = com.aladdin.dailyplate.domain.entry.PortionDraft("Home meal",
            com.aladdin.dailyplate.domain.RecordedAt("2026-11-05T10:00:00Z", "Asia/Baku"),
            com.aladdin.dailyplate.domain.EntryMethod.MANUAL, quantity = 150.0,
            unit = com.aladdin.dailyplate.domain.entry.EntryUnit.GRAM, source = "label",
            nutrientsPerBasis = com.aladdin.dailyplate.domain.NutrientSnapshot(mapOf(
                "energy" to com.aladdin.dailyplate.domain.NutrientAmount(100.0, com.aladdin.dailyplate.domain.NutrientUnit.KCAL))))
        val saved = log.confirm("guest", "today-preview").let { entry ->
            if (!recipe) entry else entry.copy(confirmedPortionGrams = null,
                actualRecipeSnapshot = com.aladdin.dailyplate.domain.ActualRecipeSnapshot(
                    com.aladdin.dailyplate.domain.VersionRef("recipe", 1), null,
                    listOf(com.aladdin.dailyplate.domain.Ingredient(com.aladdin.dailyplate.domain.VersionRef("food", 1), 100.0)),
                    4.0, 0.25, null, null, com.aladdin.dailyplate.domain.InstructionStatus.NEEDS_REVIEW))
        }
        compose.runOnUiThread { compose.activity.setContent {
            val config = android.content.res.Configuration(base.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(language)); fontScale = 1.5f
            }
            val context = base.createConfigurationContext(config)
            CompositionLocalProvider(LocalContext provides context, LocalConfiguration provides config,
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(base.resources.displayMetrics.density, 1.5f)) {
                key(language, editor) {
                    QidaTheme { androidx.compose.material3.Surface {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                            verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                            if (editor) com.aladdin.dailyplate.ui.today.LogEditor(saved, false, {}, {}, {})
                            else com.aladdin.dailyplate.ui.today.TodaySummary(listOf(saved), 100.0, "2026-11-05", "Asia/Baku")
                        }
                    } }
                }
            }
        } }
        compose.waitForIdle()
        capture("k12-${if (recipe) "recipe-" else ""}${if (editor) "editor" else "summary"}-$language-large")
    }

    @Test fun savedRecordSurvivesRestartAndCanBeEditedAndDeleted() {
        val c = base.createConfigurationContext(android.content.res.Configuration(base.resources.configuration).apply {
            setLocale(Locale.forLanguageTag("az"))
        })
        fun t(id: Int) = c.getString(id)
        click("Hesabım var"); click("Davam et"); click("Əlavə et")
        compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.fe_manual)).fetchSemanticsNodes().isNotEmpty() }
        click(t(R.string.fe_manual))
        enter(t(R.string.fe_name), "K12 yemək"); enter(t(R.string.fe_amount), "150")
        click(t(R.string.fe_prepare)); click(t(R.string.fe_prepare)); click(t(R.string.pc_confirm))
        compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.pc_saved)).fetchSemanticsNodes().isNotEmpty() }
        compose.activityRule.scenario.recreate()
        click(t(R.string.td_edit))
        compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.td_multiple)).fetchSemanticsNodes().isNotEmpty() }
        capture("k12-edit-az")
        compose.onNodeWithText(t(R.string.td_multiple)).performScrollTo().performTextReplacement("2")
        click(t(R.string.fe_prepare)); click(t(R.string.pc_confirm))
        compose.waitUntil(10000) { compose.onAllNodesWithTag("today-hero").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("today-hero").performScrollTo().assertIsDisplayed()
        capture("k12-today-az")
        compose.onNodeWithTag("today-hero").performClick()
        compose.onNodeWithText("300.0 " + t(R.string.fe_grams)).performScrollTo().assertIsDisplayed()
        click(t(R.string.td_delete))
        compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.td_delete_question)).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText(t(R.string.td_delete_question)).performScrollTo().assertIsDisplayed()
        capture("k12-delete-confirm-az")
        compose.onAllNodesWithText(t(R.string.td_delete)).onLast().performScrollTo().performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.empty_today)).fetchSemanticsNodes().isNotEmpty() }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.empty_today)).fetchSemanticsNodes().isNotEmpty() }
        capture("k12-empty-az")
    }

    @Test fun failedWriteRetriesSameEventAndOnlyThenShowsSuccess() {
        val config = android.content.res.Configuration(base.resources.configuration).apply { setLocale(Locale.forLanguageTag("az")) }
        val context = base.createConfigurationContext(config)
        var attempts = 0
        val events = mutableListOf<com.aladdin.dailyplate.domain.FoodLog>()
        compose.runOnUiThread { compose.activity.setContent {
            CompositionLocalProvider(LocalContext provides context, LocalConfiguration provides config) {
                QidaTheme { androidx.compose.material3.Surface {
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                        verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                        com.aladdin.dailyplate.ui.entry.PortionDraftEditor(
                            com.aladdin.dailyplate.domain.entry.PortionDraft("Home meal",
                                com.aladdin.dailyplate.domain.RecordedAt("2026-11-05T10:00:00Z", "Asia/Baku"),
                                com.aladdin.dailyplate.domain.EntryMethod.MANUAL, quantity = 150.0,
                                unit = com.aladdin.dailyplate.domain.entry.EntryUnit.GRAM,
                                nutrientsPerBasis = com.aladdin.dailyplate.domain.NutrientSnapshot(emptyMap())), {}, {},
                            record = { log ->
                                events.add(log); attempts++
                                if (attempts == 1) throw java.io.IOException("disk failure")
                                log
                            })
                    }
                } }
            }
        } }
        click(context.getString(R.string.fe_prepare))
        org.junit.Assert.assertEquals(0, attempts)
        click(context.getString(R.string.pc_confirm))
        compose.waitUntil(10000) { compose.onAllNodesWithText(context.getString(R.string.pc_error)).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText(context.getString(R.string.pc_saved)).assertDoesNotExist()
        capture("confirmation-write-error-az")
        click(context.getString(R.string.pc_retry))
        compose.waitUntil(10000) { compose.onAllNodesWithText(context.getString(R.string.pc_saved)).fetchSemanticsNodes().isNotEmpty() }
        org.junit.Assert.assertEquals(2, attempts); org.junit.Assert.assertEquals(events[0], events[1])
        capture("confirmation-saved-az")
    }
    @Test fun appNavigationRestoresSelectedDraftWithoutWritingFoodLog() {
        val az = base.createConfigurationContext(android.content.res.Configuration(base.resources.configuration).apply {
            setLocale(Locale.forLanguageTag("az"))
        })
        click("Hesabım var")
        click("Davam et")
        click("Əlavə et")
        compose.waitUntil(10000) { compose.onAllNodesWithText(az.getString(R.string.fe_entry_title)).fetchSemanticsNodes().isNotEmpty() }
        capture("app-search-az")
        click(az.getString(R.string.fe_manual))
        enter(az.getString(R.string.fe_name), "Evdə yemək")
        enter(az.getString(R.string.fe_amount), "150")
        click(az.getString(R.string.fe_prepare))
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText(az.getString(R.string.fe_portion)).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Evdə yemək").assertExists()
        capture("app-portion-restored-az")
    }
    @Test fun manualUnknownDraftAndSearchClearInAllLanguagesAtLargeText() {
        // This is a layout/input matrix, not a cold catalog-import timing test.
        val catalogWorker = java.util.concurrent.Executors.newSingleThreadExecutor()
        try { catalogWorker.submit {
            com.aladdin.dailyplate.data.local.CatalogRepository.open(base).use { it.productionFoods() }
        }.get() } finally { catalogWorker.shutdown() }
        var language by androidx.compose.runtime.mutableStateOf("az")
        var scale by androidx.compose.runtime.mutableFloatStateOf(1f)
        compose.runOnUiThread { compose.activity.setContent {
            val config = android.content.res.Configuration(base.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(language)); fontScale = scale
            }
            val context = base.createConfigurationContext(config)
            CompositionLocalProvider(LocalContext provides context, LocalConfiguration provides config,
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(base.resources.displayMetrics.density, scale)) {
                androidx.compose.runtime.key(language, scale) {
                    QidaTheme { androidx.compose.material3.Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.background) {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                            verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) { FoodEntryScreen {} }
                    } }
                }
            }
        }
        }
        for (s in listOf(1f, 1.5f)) for (lang in listOf("az", "en", "ru")) {
            compose.runOnIdle { language = lang; scale = s }
            val c = base.createConfigurationContext(android.content.res.Configuration(base.resources.configuration).apply { setLocale(Locale.forLanguageTag(lang)) })
            fun t(id: Int) = c.getString(id)
            click(t(R.string.fe_search))
            compose.waitUntil(30000) { compose.onAllNodesWithText(t(R.string.loading)).fetchSemanticsNodes().isEmpty() }
            compose.onNodeWithText(t(R.string.fe_no_results)).performScrollTo().assertIsDisplayed()
            enter(t(R.string.fe_query), "Missing")
            click(t(R.string.fe_clear))
            compose.onNodeWithText("Missing").assertDoesNotExist()
            capture("search-$lang-$s")
            click(t(R.string.fe_previous))
            compose.onNodeWithText(t(R.string.fe_no_previous)).performScrollTo().assertIsDisplayed()
            click(t(R.string.fe_manual))
            enter(t(R.string.fe_name), "Home meal")
            enter(t(R.string.fe_amount), "125,5")
            compose.onNodeWithText(t(R.string.fe_name)).performScrollTo()
            capture("manual-$lang-$s")
            click(t(R.string.fe_prepare))
            compose.onNodeWithText(t(R.string.fe_portion)).performScrollTo().assertIsDisplayed()
            capture("portion-$lang-$s")
            click(t(R.string.fe_prepare))
            compose.onNodeWithText(t(R.string.fe_ready)).performScrollTo().assertIsDisplayed()
            capture("ready-$lang-$s")
        }
        val worker = java.util.concurrent.Executors.newSingleThreadExecutor()
        try {
            val count = worker.submit<Int> {
                com.aladdin.dailyplate.data.local.UserRepositories.open(base).use { it.foodLogs.recent("local-guest").size }
            }.get()
            org.junit.Assert.assertEquals(0, count)
        } finally { worker.shutdown() }
    }
}
