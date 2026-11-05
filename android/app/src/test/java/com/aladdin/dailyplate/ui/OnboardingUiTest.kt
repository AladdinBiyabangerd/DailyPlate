package com.aladdin.dailyplate.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.MainActivity
import com.aladdin.dailyplate.data.local.OnboardingStore
import com.aladdin.dailyplate.domain.readyOnboarding
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class OnboardingUiTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun backRecreationAndConfirmationRetainAnswers() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val draft = readyOnboarding()
        OnboardingStore(context).queueSave(draft).get()
        compose.onNodeWithText("Başlayaq").performScrollTo().performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("✓ Sağlam yemək").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Davam et").performScrollTo().performClick()
        compose.onNodeWithText("65,2").assertExists()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10000) { compose.onAllNodesWithText("65,2").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("65,2").assertExists()
        compose.onAllNodesWithText("Geri").onLast().performScrollTo().performClick()
        compose.onNodeWithText("✓ Sağlam yemək").assertExists()
        repeat(4) { compose.onNodeWithText("Davam et").performScrollTo().performClick() }
        compose.onNodeWithText("Planımı hazırla").performScrollTo().performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Profil saxlanıb. Menyu hələ yaradılmayıb.").fetchSemanticsNodes().isNotEmpty() }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Profil saxlanıb. Menyu hələ yaradılmayıb.").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Profil saxlanıb. Menyu hələ yaradılmayıb.").assertExists()
        compose.onNodeWithText("Sağlam yemək").assertExists()
        capture("confirmed-az")
        compose.onNodeWithText("Davam et").performScrollTo().performClick()
        compose.onNodeWithText("Profil").performClick()
        compose.onNodeWithText("Profil və seçimlərim").performScrollTo().performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Profil saxlanıb. Menyu hələ yaradılmayıb.").fetchSemanticsNodes().isNotEmpty() }
    }
    @Test fun reproductiveQuestionsFollowSexSelectionWhenNavigatingBack() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        OnboardingStore(context).queueSave(readyOnboarding().change("sex", "MALE").copy(step = 1)).get()
        compose.onNodeWithText("Başlayaq").performScrollTo().performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("65,2").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Davam et").performScrollTo().performClick()
        compose.onNodeWithText("Hamiləlik").assertDoesNotExist()
        compose.onNodeWithText("Südvermə").assertDoesNotExist()
        compose.onNodeWithText("Qidalanma pozuntusu ilə bağlı risk").performScrollTo().assertIsDisplayed()
        capture("activity-male-health")
        compose.onNodeWithText("Geri").performScrollTo().performClick()
        compose.onNodeWithText("Qadın").performScrollTo().performClick()
        compose.onNodeWithText("Davam et").performScrollTo().performClick()
        compose.onNodeWithText("Hamiləlik").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Südvermə").performScrollTo().assertIsDisplayed()
        capture("activity-female-health")
        compose.onNodeWithText("Geri").performScrollTo().performClick()
        compose.onNodeWithText("Kişi").performScrollTo().performClick()
        compose.onNodeWithText("Davam et").performScrollTo().performClick()
        compose.onNodeWithText("Hamiləlik").assertDoesNotExist()
        compose.onNodeWithText("Südvermə").assertDoesNotExist()
    }
    @Test fun storageRestartAndDoubleConfirmationKeepOneProfileVersion() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val store = OnboardingStore(context)
        val draft = readyOnboarding().copy(id = "idempotent-ui-test", step = 4)
        store.queueSave(draft).get()
        Assert.assertEquals(draft, OnboardingStore(context).load())
        val worker = java.util.concurrent.Executors.newSingleThreadExecutor()
        try {
            val one = worker.submit<com.aladdin.dailyplate.domain.UserProfile> { store.confirm(draft, "az") }.get()
            val two = worker.submit<com.aladdin.dailyplate.domain.UserProfile> { store.confirm(draft, "az") }.get()
            Assert.assertEquals(one, two); Assert.assertEquals(1L, one.ref.version)
        } finally { worker.shutdown() }
    }
    @Test fun renderGoalInThreeLanguagesAtLargeFont() = renderAllSteps(1.5f, "large")
    @Test fun renderAllStepsAtNormalFont() = renderAllSteps(1f, "normal")
    private fun renderAllSteps(scale: Float, size: String) {
        val context = ApplicationProvider.getApplicationContext<Application>()
        for (language in listOf("az", "en", "ru")) {
            if (language != "az") {
                compose.onNode(hasContentDescription("Dil") or hasContentDescription("Language") or hasContentDescription("Язык")).performScrollTo().performClick()
                compose.onNodeWithText(language.uppercase()).performClick()
            }
            val config = android.content.res.Configuration(context.resources.configuration).apply { fontScale = scale }
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            OnboardingStore(context).queueSave(readyOnboarding()).get()
            compose.activityRule.scenario.recreate()
            // Resource strings are read from their actual localized context.
            val c = context.createConfigurationContext(android.content.res.Configuration(config).apply { setLocale(java.util.Locale.forLanguageTag(language)) })
            compose.onNodeWithText(c.getString(com.aladdin.dailyplate.R.string.begin)).performScrollTo().performClick()
            compose.waitUntil(10000) { compose.onAllNodesWithText(c.getString(com.aladdin.dailyplate.R.string.ob_goal)).fetchSemanticsNodes().isNotEmpty() }
            val headings = listOf(com.aladdin.dailyplate.R.string.ob_goal, com.aladdin.dailyplate.R.string.ob_body,
                com.aladdin.dailyplate.R.string.ob_activity, com.aladdin.dailyplate.R.string.ob_allergies,
                com.aladdin.dailyplate.R.string.ob_preferences)
            headings.forEachIndexed { step, heading ->
                compose.onNodeWithText(c.getString(heading)).performScrollTo()
                capture("step-$step-$language-$size")
                if (step == 4) {
                    val removedLabels = when (language) {
                        "az" -> listOf("Avadanlıq (; ilə ayır)", "Evdəki ərzaq — könüllü")
                        "en" -> listOf("Equipment (separate with ;)", "Food at home — optional")
                        "ru" -> listOf("Оборудование (через ;)", "Продукт дома — необязательно")
                        else -> error("Unexpected test language")
                    }
                    removedLabels.forEach { compose.onNodeWithText(it).assertDoesNotExist() }
                }
                if (step == 3) {
                    compose.onNodeWithText(c.getString(com.aladdin.dailyplate.R.string.ob_extra_allergies)).performScrollTo()
                    capture("allergen-details-$language-$size")
                    compose.onNodeWithText(c.getString(com.aladdin.dailyplate.R.string.ob_dislikes)).performScrollTo().performClick()
                    compose.onNodeWithText(c.getString(com.aladdin.dailyplate.R.string.ob_disliked_foods)).performScrollTo()
                    capture("dislikes-$language-$size")
                    compose.onNodeWithText(c.getString(com.aladdin.dailyplate.R.string.ob_dislikes)).performScrollTo().performClick()
                }
                val action = c.getString(if (step == 4) com.aladdin.dailyplate.R.string.ob_prepare else com.aladdin.dailyplate.R.string.continue_action)
                compose.onNodeWithText(action).performScrollTo()
                capture("step-$step-$language-bottom-$size")
                if (step < 4) compose.onNodeWithText(action).performClick()
            }
            repeat(5) { compose.onNodeWithText(c.getString(com.aladdin.dailyplate.R.string.back)).performScrollTo().performClick() }

        }
    }
    private fun capture(name: String) {
        compose.mainClock.advanceTimeBy(500)
        compose.waitForIdle()
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val file = File("build/reports/k07/$name.png")
            requireNotNull(file.parentFile).mkdirs()
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
}
