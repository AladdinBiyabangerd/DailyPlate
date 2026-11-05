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
import com.aladdin.dailyplate.ui.menu.*
import com.aladdin.dailyplate.ui.theme.*
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
class RecipeDetailDesignUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private var localized: android.content.Context? = null
    private fun t(id: Int) = localized!!.getString(id)
    private fun fixture(state: String): RecipeDetailData {
        val d = RecipeDetailTest().detail()
        val names = listOf(
            LocalizedText("TEST · Toyuq döş əti", "TEST · Chicken breast", "TEST · Куриная грудка"),
            LocalizedText("TEST · Uzun dənəli düyü", "TEST · Long-grain rice", "TEST · Длиннозёрный рис"),
            LocalizedText("TEST · Kök", "TEST · Carrot", "TEST · Морковь"))
        val foods = names.mapIndexed { i, name -> Food(VersionRef("test-food-$i", 1), name, emptyList(),
            "TEST", "TEST", "TEST · raw", NutrientSnapshot(emptyMap()), emptySet(), null, "TEST", null, ContentStatus.DRAFT) }
        val recipe = d.recipe.copy(names = LocalizedText("TEST · Tərəvəzli toyuq və düyü",
            "TEST · Chicken with vegetables and rice", "TEST · Курица с овощами и рисом"),
            ingredients = foods.mapIndexed { i, f -> Ingredient(f.ref, 150.0 + i * 10) },
            instructions = listOf("TEST · 1. Prepare the ingredients / Ərzaqları hazırla / Подготовьте ингредиенты.",
                "TEST · 2. Cook until ready / Hazır olana qədər bişir / Готовьте до готовности."))
        val item = d.item.copy(status = when(state) {
            "consumed" -> PlanItemStatus.CONSUMED; "skipped" -> PlanItemStatus.SKIPPED
            "partial" -> PlanItemStatus.PARTIALLY_CONSUMED; else -> PlanItemStatus.PLANNED
        }, remainingPlannedServings = when(state) { "consumed", "skipped" -> 0.0; "partial" -> 0.5; else -> 1.0 })
        val base = d.copy(recipe = recipe, item = item, plan = d.plan.copy(items = listOf(item)), foods = foods.associateBy { it.ref })
        if (state == "no-basis") return base.copy(recipe = recipe.copy(yieldCookedGrams = null))
        if (state != "customized") return base
        val custom = RecipeCustomization(VersionRef("TEST-custom", 2), d.plan.userId, recipe.ref,
            listOf(IngredientChange(foods[0].ref, IngredientChangeType.SET_GRAMS, 225.0)), 4.0, 1.0,
            NutrientSnapshot(emptyMap()), setOf("energy_kcal"), InstructionStatus.NEEDS_REVIEW, null, emptyList(),
            RecordedAt("2026-11-05T10:00:00Z", "Asia/Baku"))
        val changed = item.copy(customization = custom.ref, nutrientSnapshot = custom.nutrientSnapshot)
        return base.copy(item = changed, plan = base.plan.copy(items = listOf(changed)), customization = custom)
    }
    private fun show(language: String, scale: Float, detail: RecipeDetailData, confirm: (com.aladdin.dailyplate.domain.planning.PlanConsumption) -> Unit) {
        val config = android.content.res.Configuration(compose.activity.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(language)); fontScale = scale
        }
        localized = compose.activity.createConfigurationContext(config)
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            CompositionLocalProvider(LocalContext provides localized!!, LocalConfiguration provides config,
                LocalDensity provides Density(LocalDensity.current.density, scale)) {
                QidaTheme { Surface(color = MaterialTheme.colorScheme.background) {
                    key(language, scale, detail) {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                            verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                            Spacer(Modifier.height(QidaSpacing.small).testTag("d04-top"))
                            QidaHeading(t(R.string.menu))
                            RecipeDetailContent(detail, confirm = confirm)
                            QidaButton(t(R.string.back), {})
                            Spacer(Modifier.height(QidaSpacing.small).testTag("d04-bottom"))
                        }
                    }
                } }
            }
        } }
        compose.waitForIdle()
    }
    private fun capture(state: String, language: String, scale: Float) {
        val positions = listOf("top", "summary", "ingredient-0", "ingredient-1", "ingredients", "steps") +
            (if (state == "expanded") listOf("step-0", "step-1", "info") else emptyList()) + "bottom"
        for (position in positions) {
            val tag = if (position == "ingredients") "d04-ingredient-2" else "d04-$position"
            compose.onNodeWithTag(tag).performScrollTo()
            compose.runOnIdle {
                val view = compose.activity.window.decorView
                val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                view.draw(Canvas(bitmap))
                val file = File("build/reports/k10/d04-$state-$language-$scale-$position.png")
                file.parentFile!!.mkdirs()
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                bitmap.recycle()
            }
        }
    }
    @Test fun exactVersionReadOnlyDetailsAndExistingPortionConfirmation() {
        for (language in listOf("az", "en", "ru")) for (scale in listOf(1f, 1.5f)) {
            for (state in listOf("normal", "expanded", "customized", "no-basis", "consumed", "skipped", "partial")) {
                var command: com.aladdin.dailyplate.domain.planning.PlanConsumption? = null
                val d = fixture(state)
                show(language, scale, d) { command = it }
                compose.onNodeWithTag("d04-steps").assertHeightIsAtLeast(QidaDimensions.touchTarget)
                if (state == "expanded" || state == "customized") {
                    compose.onNodeWithTag("d04-steps").performScrollTo().performClick()
                    compose.onNodeWithTag("d04-info").performScrollTo().performClick()
                }
                if (state == "customized") {
                    compose.onNodeWithText(t(R.string.wm_time_unknown)).assertExists()
                    compose.onNodeWithText(t(R.string.wm_energy_unknown)).assertExists()
                    compose.onNodeWithText(t(R.string.rd_no_steps)).assertExists()
                    compose.onNodeWithTag("d04-step-0").assertDoesNotExist()
                    compose.onNodeWithText(localized!!.getString(R.string.rdd_grams, 225.0)).assertExists()
                }
                val canEat = state !in listOf("no-basis", "consumed", "skipped")
                if (canEat) compose.onNodeWithTag("d04-eat").performScrollTo().assertHeightIsAtLeast(QidaDimensions.touchTarget)
                else compose.onNodeWithTag("d04-eat").assertDoesNotExist()
                capture(state, language, scale)
                assertNull(command)
                if (state == "normal" || state == "partial") {
                    compose.onNodeWithTag("d04-eat").performScrollTo().performClick()
                    compose.onNodeWithText(t(R.string.pd_recipe_title)).assertIsDisplayed()
                    compose.onNodeWithText(t(R.string.pd_preview)).performScrollTo().assertIsDisplayed()
                    assertNull(command)
                    compose.onNodeWithText(t(R.string.mn_cancel)).performScrollTo().performClick()
                    compose.onNodeWithTag("d04-eat").performScrollTo().assertIsDisplayed()
                    assertNull(command)
                }
            }
        }
    }
}
