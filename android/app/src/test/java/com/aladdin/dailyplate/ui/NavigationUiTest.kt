package com.aladdin.dailyplate.ui

import android.app.Application
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.aladdin.dailyplate.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class NavigationUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun fiveSectionsBackLanguageAndRecreationPreserveStableDestination() {
        compose.onNodeWithText("Hesabım var").performScrollTo().performClick()
        compose.mainClock.advanceTimeBy(500)
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = android.graphics.Bitmap.createBitmap(view.width, view.height, android.graphics.Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bitmap))
            val file = java.io.File("build/reports/ui-parity/account-az-normal.png")
            requireNotNull(file.parentFile).mkdirs()
            file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
        compose.onNodeWithText("Davam et").performScrollTo().performClick()
        for (label in listOf("Menyu", "Əlavə et", "İrəliləyiş", "Profil")) {
            compose.onNodeWithText(label).assertHeightIsAtLeast(48.dp).performClick()
            compose.onNode(isSelected() and hasText(label)).assertExists()
        }
        compose.onNodeWithContentDescription("Dil").performScrollTo().performClick()
        compose.onNodeWithText("EN").performClick()
        compose.onNode(isSelected() and hasText("Profile")).assertExists()
        compose.activityRule.scenario.recreate()
        compose.onNode(isSelected() and hasText("Profile")).assertExists()
        compose.onNodeWithText("Back").performClick()
        compose.onNode(isSelected() and hasText("Progress")).assertExists()
        compose.onNodeWithText("Profile").performClick()
        compose.onNodeWithContentDescription("Language").performScrollTo().performClick()
        compose.onNodeWithText("RU").performClick()
        compose.onNode(isSelected() and hasText("Профиль")).assertExists()
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = android.graphics.Bitmap.createBitmap(view.width, view.height, android.graphics.Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bitmap))
            val file = java.io.File("build/reports/k06/profile-ru.png")
            requireNotNull(file.parentFile).mkdirs()
            file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
    @Test fun renderEverySectionInThreeLanguagesAtBothFontSizes() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<Application>()
        compose.onNodeWithText("Hesabım var").performScrollTo().performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.OnClick) { it() }
        compose.onNodeWithText("Davam et").performScrollTo().performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.OnClick) { it() }
        // This matrix checks layout, not pointer timing. A delayed press coroutine can
        // outlive the node when language changes/recreation replace the whole tree.
        // Invoke its real click semantics; the navigation behavior test above still taps.
        for (scale in listOf(1f, 1.5f)) {
            val config = android.content.res.Configuration(context.resources.configuration).apply { fontScale = scale }
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            compose.activityRule.scenario.recreate()
            for (language in listOf("az", "en", "ru")) {
                // Open Profile through its stable selected destination semantics.
                compose.onAllNodes(hasText("Profil") or hasText("Profile") or hasText("Профиль")).onLast().performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.OnClick) { it() }
                compose.onNode(hasContentDescription("Dil") or hasContentDescription("Language") or hasContentDescription("Язык")).performScrollTo().performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.OnClick) { it() }
                // The currently selected code also appears on the anchor; choose the menu item.
                compose.onAllNodesWithText(language.uppercase()).onLast().performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.OnClick) { it() }
                val localized = context.createConfigurationContext(android.content.res.Configuration(config).apply {
                    setLocale(java.util.Locale.forLanguageTag(language))
                })
                for (label in listOf(com.aladdin.dailyplate.R.string.today, com.aladdin.dailyplate.R.string.menu,
                    com.aladdin.dailyplate.R.string.add, com.aladdin.dailyplate.R.string.progress, com.aladdin.dailyplate.R.string.profile)) {
                    val text = localized.getString(label)
                    compose.onNodeWithText(text).assertHeightIsAtLeast(48.dp).performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.OnClick) { it() }
                    compose.onNode(isSelected() and hasText(text)).assertExists()
                    compose.mainClock.advanceTimeBy(500)
                    compose.waitForIdle()
                    compose.runOnIdle {
                        val view = compose.activity.window.decorView
                        val bitmap = android.graphics.Bitmap.createBitmap(view.width, view.height, android.graphics.Bitmap.Config.ARGB_8888)
                        view.draw(android.graphics.Canvas(bitmap))
                        val file = java.io.File("build/reports/ui-parity/section-$label-$language-$scale.png")
                        requireNotNull(file.parentFile).mkdirs()
                        file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
                    }
                }
            }
        }
    }
}
