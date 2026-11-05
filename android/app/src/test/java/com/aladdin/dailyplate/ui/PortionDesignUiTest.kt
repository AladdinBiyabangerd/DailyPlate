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
import com.aladdin.dailyplate.domain.planning.PlanConsumption
import com.aladdin.dailyplate.ui.entry.PortionDraftEditor
import com.aladdin.dailyplate.ui.menu.*
import com.aladdin.dailyplate.ui.theme.*
import kotlinx.coroutines.CompletableDeferred
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.*
import java.io.File
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PortionDesignUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val at = RecordedAt("2026-11-05T10:00:00Z", "Asia/Baku")
    private var context: android.content.Context? = null
    private fun t(id: Int) = context!!.getString(id)
    private fun click(id: Int) = compose.onNodeWithText(t(id)).performScrollTo().performClick()
    private fun enter(id: Int, value: String) = compose.onNodeWithText(t(id)).performScrollTo().performTextReplacement(value)

    private fun show(language: String, scale: Float, content: @Composable () -> Unit) {
        val config = android.content.res.Configuration(compose.activity.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(language)); fontScale = scale
        }
        val localized = compose.activity.createConfigurationContext(config)
        context = localized
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            CompositionLocalProvider(LocalContext provides localized, LocalConfiguration provides config,
                LocalDensity provides Density(LocalDensity.current.density, scale)) {
                QidaTheme { Surface(color = MaterialTheme.colorScheme.background) {
                    key(language, scale, content) {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                            verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                            Spacer(Modifier.height(QidaSpacing.small).testTag("d02-top"))
                            content()
                            Spacer(Modifier.height(QidaSpacing.small).testTag("d02-bottom"))
                        }
                    }
                } }
            }
        } }
        compose.waitForIdle()
    }

    private fun capture(surface: String, state: String, language: String, scale: Float) {
        val positions = if (surface == "recipe-portion" && state in listOf("edit", "selected"))
            listOf("top", "middle", "bottom") else listOf("top", "bottom")
        for (position in positions) {
            if (position == "middle") compose.onNodeWithText(t(R.string.pd_eaten)).performScrollTo()
            else compose.onNodeWithTag("d02-$position").performScrollTo()
            compose.runOnIdle {
                val view = compose.activity.window.decorView
                val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                view.draw(Canvas(bitmap))
                val file = File("build/reports/k10/d02-$surface-$state-$language-$scale-$position.png")
                file.parentFile!!.mkdirs()
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                bitmap.recycle()
            }
        }
    }

    @Test fun manualSearchAndRepeatPreserveUnitsAndRetrySameEventAcrossLanguagesAndTextSizes() {
        for (language in listOf("az", "en", "ru")) for (scale in listOf(1f, 1.5f)) {
            val name = when (language) { "en" -> "Homemade vegetable and chicken meal"; "ru" -> "Домашнее блюдо с овощами и курицей"; else -> "Evdə hazırlanan tərəvəzli toyuq yeməyi" }
            val draft = PortionDraft(name, at, EntryMethod.MANUAL, quantity = 250.0, unit = EntryUnit.GRAM,
                nutrientsPerBasis = NutrientSnapshot(emptyMap()))
            val events = mutableListOf<FoodLog>()
            val writing = CompletableDeferred<Unit>()
            show(language, scale) {
                var current by remember { mutableStateOf(draft) }
                PortionDraftEditor(current, {}, { current = it }, record = { log ->
                    events.add(log)
                    if (events.size == 1) throw java.io.IOException("TEST disk full")
                    writing.await(); log
                })
            }
            capture("portion", "edit", language, scale)
            enter(R.string.fe_amount, "-1"); click(R.string.pd_preview)
            compose.onNodeWithText(t(R.string.fe_invalid)).assertExists()
            capture("portion", "invalid", language, scale)
            enter(R.string.fe_amount, "250,5"); click(R.string.pd_preview)
            assertTrue(events.isEmpty())
            compose.onNodeWithText(t(R.string.fe_ready)).assertExists()
            capture("portion", "preview", language, scale)
            click(R.string.pc_confirm)
            compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.pc_error)).fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText(t(R.string.pc_saved)).assertDoesNotExist()
            capture("portion", "error", language, scale)
            click(R.string.pc_retry)
            compose.waitUntil(10000) { events.size == 2 }
            capture("portion", "loading", language, scale)
            writing.complete(Unit)
            compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.pc_saved)).fetchSemanticsNodes().isNotEmpty() }
            compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.td_total)).fetchSemanticsNodes().isNotEmpty() }
            capture("portion", "saved", language, scale)
            assertEquals(events[0], events[1]); assertEquals(250.5, events[1].confirmedPortionGrams!!, 0.0)
            assertTrue(events[1].nutrientSnapshot.values.isEmpty())
            val source = draft.copy(method = EntryMethod.SEARCH, food = VersionRef("fixture-source", 1),
                preparationState = "TEST cooked", source = "TEST source / version 1", unit = EntryUnit.KILOGRAM,
                quantity = 0.25, nutrientsPerBasis = NutrientSnapshot(mapOf("energy" to NutrientAmount(100.0, NutrientUnit.KCAL))))
            show(language, scale) { var current by remember { mutableStateOf(source) }; PortionDraftEditor(current, {}, { current = it }) }
            capture("portion", "search", language, scale)
            click(R.string.pd_preview)
            compose.onNodeWithText(t(R.string.pc_energy) + ": 250.0 kcal").performScrollTo().assertIsDisplayed()
            val repeat = events[1].repeatDraft(name, at)
            show(language, scale) { var current by remember { mutableStateOf(repeat) }; PortionDraftEditor(current, {}, { current = it }) }
            capture("portion", "previous", language, scale)
            enter(R.string.fe_amount, "0,5"); click(R.string.pd_preview)
            compose.onNodeWithText("125.25 " + t(R.string.fe_grams)).performScrollTo().assertIsDisplayed()
        }
    }

    @Test fun recipeUsesBatchShareMeasuredGramsPreviewAndExplicitConfirmation() {
        for (language in listOf("az", "en", "ru")) for (scale in listOf(1f, 1.5f)) {
            val detail = RecipeDetailTest().detail().let { value -> value.copy(recipe = value.recipe.copy(names =
                LocalizedText("TEST · Tərəvəzli toyuq və düyü", "TEST · Chicken with vegetables and rice", "TEST · Курица с овощами и рисом"))) }
            var saved: PlanConsumption? = null
            show(language, scale) { RecipePortionEditor(detail, {}, { saved = it }) }
            capture("recipe-portion", "edit", language, scale)
            click(R.string.pd_half)
            capture("recipe-portion", "selected", language, scale)
            enter(R.string.pd_count, "8")
            click(R.string.pd_exact)
            capture("recipe-portion", "exact", language, scale)
            enter(R.string.pd_grams, "0"); click(R.string.pd_preview)
            compose.onNodeWithText(t(R.string.rd_invalid)).assertExists()
            capture("recipe-portion", "invalid", language, scale)
            enter(R.string.pd_grams, "90"); click(R.string.pd_preview)
            assertNull(saved)
            capture("recipe-portion", "preview", language, scale)
            click(R.string.pc_confirm)
            compose.runOnIdle {
                assertEquals(0.5, saved!!.consumedServings, 0.0)
                assertEquals(0.0625, saved!!.log.actualRecipeSnapshot!!.consumedShare, 0.0)
                saved!!.applyTo(detail.plan)
            }
            val custom = RecipeCustomization(VersionRef("custom", 2), detail.plan.userId, detail.recipe.ref,
                emptyList(), 4.0, 1.0, detail.item.nutrientSnapshot, emptySet(), InstructionStatus.NEEDS_REVIEW,
                null, emptyList(), at)
            val noYield = detail.copy(customization = custom, item = detail.item.copy(customization = custom.ref))
            show(language, scale) { RecipePortionEditor(noYield, {}, { error("No measured grams") }) }
            click(R.string.pd_exact)
            capture("recipe-portion", "no-yield", language, scale)
            compose.onNodeWithText(t(R.string.pd_no_yield)).assertExists()
            val noBasis = detail.copy(recipe = detail.recipe.copy(yieldCookedGrams = null, nutrientsPer100g = null))
            show(language, scale) { RecipePortionEditor(noBasis, {}, { error("No basis") }) }
            capture("recipe-portion", "no-basis", language, scale)
            compose.onNodeWithText(t(R.string.pd_preview)).assertDoesNotExist()
        }
    }
}
