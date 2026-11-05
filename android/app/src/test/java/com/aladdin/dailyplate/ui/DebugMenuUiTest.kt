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
class DebugMenuUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private var localized: android.content.Context? = null
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

    @Test fun sevenOfflinePhotosAndDatedMealsInEveryLanguageAndTextScale() {
        for (language in listOf("az", "en", "ru")) for (scale in listOf(1f, 1.5f)) {
            show(language, scale) { DebugWeeklyMenu(LocalDate.parse("2026-11-05")) }
            val names = localized!!.resources.getStringArray(R.array.demo_meals)
            assertEquals(7, names.size)
            for (index in 0..6) {
                compose.onNodeWithTag("demo-photo-$index").performScrollTo().assertIsDisplayed()
                    .assertContentDescriptionEquals(names[index])
                compose.onNodeWithTag("demo-meal-$index").performScrollTo().assertIsDisplayed()
                compose.runOnIdle {
                    val view = compose.activity.window.decorView
                    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                    view.draw(Canvas(bitmap))
                    val file = File("build/reports/demo/demo-$language-$scale-meal-$index.png")
                    file.parentFile!!.mkdirs()
                    file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                    bitmap.recycle()
                }
            }
        }
    }
}
