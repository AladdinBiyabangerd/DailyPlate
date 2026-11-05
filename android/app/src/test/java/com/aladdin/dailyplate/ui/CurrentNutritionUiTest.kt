package com.aladdin.dailyplate.ui

import android.app.Application
import androidx.activity.compose.setContent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import com.aladdin.dailyplate.R
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.aladdin.dailyplate.MainActivity
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.policy.*
import com.aladdin.dailyplate.domain.onboarding.OnboardingPreview
import com.aladdin.dailyplate.ui.onboarding.ProfileAnswerFields
import com.aladdin.dailyplate.ui.profile.TargetExplanation
import com.aladdin.dailyplate.ui.theme.*
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CurrentNutritionUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun renderCurrentActivityAndTargetAtLargeText() {
        val d = readyOnboarding().change("sex", "MALE").change("activity", "low_active")
            .change("measurements_confirmed", "yes").let { start ->
                NutritionHealthQuestion.entries.fold(start) { draft, key -> draft.change(key.name.lowercase(), "no") }
            }
        val preview = OnboardingPreview.create(d, "az", RecordedAt("2026-11-05T10:00:00Z", "Asia/Baku"), CalendarDate("2026-11-05"))
        Assert.assertNotNull(preview.assessment.dailyKcal)
        for (language in listOf("az", "en", "ru")) {
            val config = android.content.res.Configuration(compose.activity.resources.configuration).apply {
                setLocale(java.util.Locale.forLanguageTag(language))
                fontScale = 1.5f
            }
            val localized = compose.activity.createConfigurationContext(config)
            compose.activityRule.scenario.onActivity { activity ->
                activity.setContentForNutritionTest(d, preview, localized)
            }
            compose.onNodeWithText(localized.getString(R.string.pf_current_basis)).performScrollTo().assertIsDisplayed()
            capture("target-$language-large")
            compose.onNodeWithText(localized.getString(R.string.pf_eer_source)).performScrollTo().assertIsDisplayed()
            // Exact numeric values are domain-tested. Here verify the actual localized interval label.
            compose.onNodeWithText(localized.getString(R.string.pf_energy_interval).substringBefore("%"), substring = true)
                .performScrollTo().assertIsDisplayed()
            capture("bounds-$language-large")
            compose.onNodeWithText(localized.getString(R.string.ob_very_active)).performScrollTo().assertIsDisplayed()
            capture("four-activities-$language-large")
            compose.onNodeWithText(localized.getString(R.string.ob_body_composition)).performScrollTo().assertIsDisplayed()
            capture("health-$language-large")
        }
    }
    @Test fun reviewAndSuspensionRenderAtLargeTextAndReportsHaveDistinctActions() {
        for (language in listOf("az", "en", "ru")) {
            val config = android.content.res.Configuration(compose.activity.resources.configuration).apply {
                setLocale(java.util.Locale.forLanguageTag(language)); fontScale = 1.5f
            }
            val localized = compose.activity.createConfigurationContext(config)
            var reported: NutritionSuspension? = null
            compose.activityRule.scenario.onActivity { activity ->
                activity.setContent {
                    QidaTheme {
                        CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 1.5f),
                            LocalContext provides localized, LocalConfiguration provides config) {
                            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.medium),
                                verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                                com.aladdin.dailyplate.ui.profile.NutritionReviewCard(
                                    com.aladdin.dailyplate.domain.profile.NutritionReviewState(true, 3, -0.5, 70.0),
                                    false, false, {}, { reported = it })
                                com.aladdin.dailyplate.ui.profile.NutritionReviewCard(
                                    com.aladdin.dailyplate.domain.profile.NutritionReviewState(false, 0, null, null),
                                    true, false, {}, {})
                            }
                        }
                    }
                }
            }
            compose.onNodeWithText(localized.getString(R.string.nr_days, 3)).assertIsDisplayed()
            capture("np16-review-$language-large")
            compose.onNodeWithText(localized.getString(R.string.nr_symptom)).performScrollTo().performClick()
            compose.runOnIdle { Assert.assertEquals(NutritionSuspension.NEW_SYMPTOM, reported) }
            compose.onNodeWithText(localized.getString(R.string.nr_unexplained)).performScrollTo().performClick()
            compose.runOnIdle { Assert.assertEquals(NutritionSuspension.UNEXPLAINED_WEIGHT_CHANGE, reported) }
            compose.onNodeWithText(localized.getString(R.string.nr_support)).performScrollTo().assertIsDisplayed()
            capture("np16-suspended-$language-large")
        }
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val file = File(System.getenv("NUTRITION_SCREENSHOT_DIR") ?: "build/reports/nutrition-integration", "$name.png")
            file.parentFile!!.mkdirs()
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
}

private fun MainActivity.setContentForNutritionTest(draft: com.aladdin.dailyplate.domain.onboarding.OnboardingDraft,
                                                    preview: OnboardingPreview, localized: android.content.Context) {
    setContent {
        QidaTheme {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 1.5f),
                LocalContext provides localized, LocalConfiguration provides localized.resources.configuration) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.medium),
                    verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                    TargetExplanation(preview.profile, preview.assessment)
                    ProfileAnswerFields(draft.copy(step = 2), false) {}
                }
            }
        }
    }
}
