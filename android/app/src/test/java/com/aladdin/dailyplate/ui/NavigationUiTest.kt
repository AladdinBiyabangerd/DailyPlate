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
        compose.onNodeWithText("Davam et").performScrollTo().performClick()
        for (label in listOf("Menyu", "Əlavə et", "İrəliləyiş", "Profil")) {
            compose.onNodeWithText(label).assertHeightIsAtLeast(48.dp).performClick()
            compose.onNode(isSelected() and hasText("✓\n$label")).assertExists()
        }
        compose.onNodeWithText("EN").performScrollTo().performClick()
        compose.onNode(isSelected() and hasText("✓\nProfile")).assertExists()
        compose.activityRule.scenario.recreate()
        compose.onNode(isSelected() and hasText("✓\nProfile")).assertExists()
        compose.onNodeWithText("Back").performClick()
        compose.onNode(isSelected() and hasText("✓\nProgress")).assertExists()
        compose.onNodeWithText("Profile").performClick()
        compose.onNodeWithText("RU").performScrollTo().performClick()
        compose.onNode(isSelected() and hasText("✓\nПрофиль")).assertExists()
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = android.graphics.Bitmap.createBitmap(view.width, view.height, android.graphics.Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bitmap))
            val file = java.io.File("build/reports/k06/profile-ru.png")
            requireNotNull(file.parentFile).mkdirs()
            file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
}
