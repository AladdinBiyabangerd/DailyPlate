package com.aladdin.dailyplate.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import com.aladdin.dailyplate.MainActivity
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.data.local.*
import com.aladdin.dailyplate.domain.readyOnboarding
import com.aladdin.dailyplate.ui.menu.*
import com.aladdin.dailyplate.ui.theme.*
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.*
import java.io.File
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MenuUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun unavailableInputsAreHonestLocalizedAndCreateNoPlanOrLog() {
        val context = compose.activity.applicationContext
        val worker = Executors.newSingleThreadExecutor()
        val profile = try {
            worker.submit<com.aladdin.dailyplate.domain.UserProfile> {
                val draft = readyOnboarding().copy(id = "menu-ui")
                OnboardingStore(context).apply { queueSave(draft).get() }.confirm(draft, "az")
            }.get()
        } finally { worker.shutdown() }
        for (language in listOf("az", "en", "ru")) {
            val config = android.content.res.Configuration(compose.activity.resources.configuration).apply {
                setLocale(java.util.Locale.forLanguageTag(language)); fontScale = 1.5f
            }
            val localized = compose.activity.createConfigurationContext(config)
            compose.activityRule.scenario.onActivity { activity -> activity.setContent {
                CompositionLocalProvider(LocalContext provides localized, LocalConfiguration provides config,
                    LocalDensity provides Density(LocalDensity.current.density, 1.5f)) {
                    QidaTheme {
                        androidx.compose.material3.Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.background) {
                            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                                verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                                QidaHeading(localized.getString(R.string.menu))
                                MenuProfileSummary(profile)
                                MenuUnavailable()
                            }
                        }
                    }
                }
            } }
            compose.onNodeWithText(localized.getString(R.string.mn_slots, profile.mealsPerDay)).assertIsDisplayed()
            compose.onNodeWithText(localized.getString(R.string.mn_unavailable)).performScrollTo().assertIsDisplayed()
            compose.runOnIdle {
                val view = compose.activity.window.decorView
                val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                view.draw(Canvas(bitmap))
                val file = File(System.getenv("MENU_SCREENSHOT_DIR") ?: "build/reports/menu-ui", "unavailable-$language.png")
                file.parentFile!!.mkdirs()
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            }
        }
        val verify = Executors.newSingleThreadExecutor()
        try { verify.submit {
            val data = MenuWorkflow(context).load()
            Assert.assertFalse(data.inputsAvailable)
            Assert.assertTrue(data.plans.isEmpty())
            UserRepositories.open(context).use { Assert.assertTrue(it.foodLogs.recent("local-guest").isEmpty()) }
        }.get() } finally { verify.shutdown() }
    }
    @Test fun recipeViewingAndCancellationDoNotLogAndHalfPortionRequiresConfirmation() {
        for (language in listOf("az", "en", "ru")) {
            val config = android.content.res.Configuration(compose.activity.resources.configuration).apply {
                setLocale(java.util.Locale.forLanguageTag(language)); fontScale = 1.5f
            }
            val localized = compose.activity.createConfigurationContext(config)
            var confirmed: com.aladdin.dailyplate.domain.planning.PlanConsumption? = null
            val detail = RecipeDetailTest().detail().let { d -> d.copy(recipe = d.recipe.copy(
                names = com.aladdin.dailyplate.domain.LocalizedText("Tərəvəzli toyuq və düyü", "Chicken with vegetables and rice", "Курица с овощами и рисом"),
                instructions = listOf("TEST — preparation instructions"))) }
            compose.activityRule.scenario.onActivity { activity -> activity.setContent {
                CompositionLocalProvider(LocalContext provides localized, LocalConfiguration provides config,
                    LocalDensity provides Density(LocalDensity.current.density, 1.5f)) {
                    QidaTheme {
                        androidx.compose.material3.Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.background) {
                            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.large),
                                verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                                androidx.compose.runtime.key(language) { RecipeDetailContent(detail) { confirmed = it } }
                            }
                        }
                    }
                }
            } }
            compose.onNodeWithText(detail.recipe.names.recipeLabel(language)).assertIsDisplayed()
            capture("recipe-$language")
            Assert.assertNull(confirmed)
            compose.onNodeWithText(localized.getString(R.string.rd_ate)).performScrollTo().performClick()
            compose.onNodeWithText(localized.getString(R.string.rd_personal)).performScrollTo().performTextInput("0,5")
            compose.onNodeWithText(localized.getString(R.string.fe_prepare)).performScrollTo().performClick()
            compose.onNodeWithText(localized.getString(R.string.rd_confirm_portion, 0.5)).performScrollTo().assertIsDisplayed()
            Assert.assertNull(confirmed)
            compose.onNodeWithText(localized.getString(R.string.pc_confirm)).performScrollTo().assertIsDisplayed()
            capture("recipe-portion-$language")
            compose.onNodeWithText(localized.getString(R.string.mn_cancel)).performScrollTo().performClick()
            Assert.assertNull(confirmed)
            compose.onNodeWithText(localized.getString(R.string.rd_ate)).performScrollTo().performClick()
            compose.onNodeWithText(localized.getString(R.string.rd_personal)).performScrollTo().performTextInput("0.5")
            compose.onNodeWithText(localized.getString(R.string.fe_prepare)).performScrollTo().performClick()
            compose.onNodeWithText(localized.getString(R.string.pc_confirm)).performScrollTo().performClick()
            compose.runOnIdle { Assert.assertEquals(0.5, confirmed!!.consumedServings, 0.0) }
        }
    }

    private fun capture(name: String) = compose.runOnIdle {
        val view = compose.activity.window.decorView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        val file = File(System.getenv("MENU_SCREENSHOT_DIR") ?: "build/reports/menu-ui", "$name.png")
        file.parentFile!!.mkdirs()
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

}
