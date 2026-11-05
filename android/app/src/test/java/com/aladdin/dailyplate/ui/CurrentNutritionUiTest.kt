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
        compose.activityRule.scenario.onActivity { activity ->
            activity.setContentForNutritionTest(d, preview)
        }
        compose.onNodeWithText("NASEM 2023", substring = true).performScrollTo().assertIsDisplayed()
        capture("target-large")
        compose.onNodeWithText("Menyu enerji intervalı:", substring = true).performScrollTo().assertIsDisplayed()
        capture("bounds-large")
        compose.onNodeWithText("Çox aktiv:", substring = true).performScrollTo().assertIsDisplayed()
        capture("four-activities-large")
        compose.onNodeWithText("Bədən tərkibin BMI", substring = true).performScrollTo().assertIsDisplayed()
        capture("health-large")
    }
    private fun capture(name: String) {
        compose.waitForIdle()
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val file = File("build/reports/nutrition-integration/$name.png")
            file.parentFile!!.mkdirs()
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
}

private fun MainActivity.setContentForNutritionTest(draft: com.aladdin.dailyplate.domain.onboarding.OnboardingDraft,
                                                    preview: OnboardingPreview) {
    setContent {
        QidaTheme {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 1.5f)) {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(QidaSpacing.medium),
                    verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                    TargetExplanation(preview.profile, preview.assessment)
                    ProfileAnswerFields(draft.copy(step = 2), false) {}
                }
            }
        }
    }
}
