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
    private fun click(label: String) = compose.onAllNodesWithText(label).onFirst().performScrollTo().performClick()
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
    @Test fun manualUnknownDraftAndSearchClearInAllLanguagesAtLargeText() {
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
                    QidaTheme { androidx.compose.material3.Surface {
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
            compose.waitUntil(10000) { compose.onAllNodesWithText(t(R.string.fe_no_results)).fetchSemanticsNodes().isNotEmpty() }
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
