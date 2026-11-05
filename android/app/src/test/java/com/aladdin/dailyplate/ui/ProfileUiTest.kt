package com.aladdin.dailyplate.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.MainActivity
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.data.local.*
import com.aladdin.dailyplate.domain.readyOnboarding
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ProfileUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val context get() = ApplicationProvider.getApplicationContext<Application>()
    private fun seed() {
        val d = readyOnboarding().copy(id = "k09-ui")
        val store = OnboardingStore(context)
        store.queueSave(d).get()
        val worker = Executors.newSingleThreadExecutor()
        try { worker.submit { store.confirm(d, "az") }.get() } finally { worker.shutdown() }
    }
    private fun await(text: String) = compose.waitUntil(10000) { compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
    private fun click(text: String) {
        val node = compose.onAllNodesWithText(text).onFirst()
        if (text !in setOf("Profil", "Profile", "Профиль")) node.performScrollTo()
        node.performClick()
    }

    @Test fun cancelThenConfirmPersistsNewVersionAndExplanationAfterRecreation() {
        seed()
        click("Başlayaq")
        await("Profil saxlanıb. Menyu hələ yaradılmayıb.")
        click("Davam et"); click("Profil")
        await("Profil versiyası 1")
        click("Nə istəyirsən?")
        click("Arıqlamaq")
        compose.onNodeWithText("Dəyişiklik səbəbi").performScrollTo().performTextInput("Məqsədimi yeniləyirəm")
        click("Dəyişikliklərə bax")
        await("Profil: 1 → 2")
        capture("preview-top-az")
        click("Dəyişiklikləri ləğv et")
        await("Profil versiyası 1")
        click("Nə istəyirsən?"); click("Çəki almaq")
        compose.onNodeWithText("Dəyişiklik səbəbi").performScrollTo().performTextInput("Yeni məqsəd")
        click("Dəyişikliklərə bax"); click("Təsdiqlə və profili saxla")
        await("Profil versiyası 2")
        compose.activityRule.scenario.recreate()
        await("Profil versiyası 2")
        click("Nəyə əsasən?")
        compose.onNodeWithText("Dəyişiklik səbəbi: Yeni məqsəd").performScrollTo().assertIsDisplayed()
        compose.onAllNodesWithText("Hesab üçün tələb olunan cavablar tam deyil.").onFirst().performScrollTo().assertIsDisplayed()
        capture("explanation-az")
    }

    @Test fun renderEditorAndExplanationInEveryLanguageAndLargeText() {
        seed()
        click("Başlayaq"); await("Profil saxlanıb. Menyu hələ yaradılmayıb.")
        click("Davam et"); click("Profil"); await("Profil versiyası 1")
        for (scale in listOf(1f, 1.5f)) for (language in listOf("az", "en", "ru")) {
            compose.onNode(hasContentDescription("Dil") or hasContentDescription("Language") or hasContentDescription("Язык"))
                .performScrollTo().performClick()
            compose.onAllNodesWithText(language.uppercase()).onLast().performClick()
            val config = android.content.res.Configuration(context.resources.configuration).apply { fontScale = scale }
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            compose.activityRule.scenario.recreate()
            val c = context.createConfigurationContext(android.content.res.Configuration(config).apply { setLocale(java.util.Locale.forLanguageTag(language)) })
            fun text(id: Int) = c.getString(id)
            await(c.getString(R.string.pf_version, 1))
            capture("profile-$language-$scale")
            click(text(R.string.pf_basis))
            compose.onNodeWithText(text(R.string.pf_no_formula)).performScrollTo().assertIsDisplayed()
            capture("explanation-$language-$scale")
            click(text(R.string.ob_profile)); await(text(R.string.pf_edit))
            compose.onNodeWithText(text(R.string.pf_edit)).performScrollTo()
            capture("editor-$language-$scale")
            compose.onNodeWithText(text(R.string.pf_reason)).performScrollTo()
            capture("editor-bottom-$language-$scale")
            click(text(R.string.pf_cancel))

        }
    }
    private fun capture(name: String) {
        compose.mainClock.advanceTimeBy(500); compose.waitForIdle()
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val file = File("build/reports/k09/$name.png")
            requireNotNull(file.parentFile).mkdirs()
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
}
