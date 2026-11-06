package com.aladdin.dailyplate.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.inspector.WindowInspector
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import com.aladdin.dailyplate.OnboardingPreviewActivity
import com.aladdin.dailyplate.ui.preview.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TabletOnboardingRenderTest {
    @get:Rule val compose = createAndroidComposeRule<OnboardingPreviewActivity>()
    @Test @Config(qualifiers = "w800dp-h1280dp-port-mdpi") fun portrait() = matrix("portrait")
    @Test @Config(qualifiers = "w1280dp-h800dp-land-mdpi") fun landscape() = matrix("landscape")
    private val overflow = mutableListOf<String>()
    private fun matrix(orientation: String) {
        var model by mutableStateOf(PreviewState())
        var scale by mutableStateOf(1f)
        compose.activityRule.scenario.onActivity { it.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, scale)) {
                key(model) { OnboardingPreview(model) }
            }
        } }
        for (language in listOf("az", "en", "ru")) for (font in listOf(1f, 1.5f)) {
            for ((group, variants) in TabletRenderCases.groups) for (variant in variants) {
                compose.runOnIdle { model = TabletRenderCases.fixture(group, variant).apply { this.language = language }; scale = font }
                compose.waitForIdle(); compose.mainClock.advanceTimeBy(500)
                compose.onNodeWithTag("tablet-$orientation").assertExists()
                val name = "$group-$orientation-$variant-$language-${if (font == 1f) "100" else "150"}"
                capture(name); noTextOverflow(name)
                // Every off-screen CTA remains reachable at enlarged fonts without shrinking text.
                if (model.dialog == PreviewDialog.None && model.page != PreviewPage.Today) {
                    val title = when (model.page) {
                        PreviewPage.Welcome -> com.aladdin.dailyplate.R.string.dp_start_welcome
                        PreviewPage.Review -> if (model.missing().isEmpty()) com.aladdin.dailyplate.R.string.dp_next else com.aladdin.dailyplate.R.string.dp_complete_answers
                        PreviewPage.Warning -> com.aladdin.dailyplate.R.string.dp_diary_continue
                        PreviewPage.Account -> com.aladdin.dailyplate.R.string.dp_not_now
                        PreviewPage.Ready -> com.aladdin.dailyplate.R.string.dp_start
                        else -> com.aladdin.dailyplate.R.string.dp_next
                    }
                    val config = android.content.res.Configuration(compose.activity.resources.configuration).apply { setLocale(java.util.Locale.forLanguageTag(language)) }
                    val text = compose.activity.createConfigurationContext(config).getString(title)
                    compose.onNodeWithText(text).performScrollTo().assertIsDisplayed(); capture("$name-bottom")
                }
            }
        }
        assertTrue(com.google.firebase.FirebaseApp.getApps(compose.activity).isEmpty())
        File("build/reports/onboarding-tablet/overflow-$orientation.txt").writeText(overflow.joinToString("\n"))
        assertTrue("${overflow.size} overflowing text layouts: ${overflow.take(12)}", overflow.isEmpty())
    }
    private fun noTextOverflow(name: String) {
        val matcher = SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult)
        compose.onAllNodes(matcher, useUnmergedTree = true).fetchSemanticsNodes().forEach { node ->
            val results = mutableListOf<TextLayoutResult>()
            node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(results)
            results.forEach { layout ->
                // Paragraph allocation remains at maxWidth for shrink-wrapped text in Robolectric.
                // Test actual glyph-line extents, not that unused paragraph allocation. One pixel
                // tolerance accounts for integer layout rounding; vertical truncation is forbidden.
                val lineWidth = (0 until layout.lineCount).maxOfOrNull { layout.getLineRight(it) - layout.getLineLeft(it) } ?: 0f
                if (lineWidth > layout.size.width + 1 || layout.multiParagraph.height > layout.size.height + 1) {
                    overflow.add("$name: ${layout.layoutInput.text}; glyphWidth=$lineWidth size=${layout.size} paragraphHeight=${layout.multiParagraph.height}")
                }
            }
        }
    }
    private fun capture(name: String) {
        compose.waitForIdle()
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap); view.draw(canvas)
            WindowInspector.getGlobalWindowViews().filter { it !== view && it.isShown && it.isAttachedToWindow }.forEach { window ->
                canvas.drawColor(android.graphics.Color.argb(100, 18, 55, 67))
                val xy = IntArray(2); window.getLocationOnScreen(xy)
                canvas.save(); canvas.translate(if (xy[0] == 0) (view.width - window.width) / 2f else xy[0].toFloat(), if (xy[1] == 0) (view.height - window.height) / 2f else xy[1].toFloat())
                window.draw(canvas); canvas.restore()
            }
            val file = File("build/reports/onboarding-tablet/$name.png"); file.parentFile!!.mkdirs()
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }; bitmap.recycle(); assertTrue(file.length() > 1000)
        }
    }
}
