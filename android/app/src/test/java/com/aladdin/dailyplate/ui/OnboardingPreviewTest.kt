package com.aladdin.dailyplate.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.inspector.WindowInspector
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.aladdin.dailyplate.OnboardingPreviewActivity
import com.aladdin.dailyplate.R
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
@Config(sdk = [34], application = Application::class, qualifiers = "w390dp-h844dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class OnboardingPreviewTest {
    @get:Rule val compose = createAndroidComposeRule<OnboardingPreviewActivity>()
    private fun click(text: String) = compose.onNodeWithText(text).performScrollTo().performClick()
    @Test fun completeOfflineFlowHasNoConsumptionAndEveryShellActionExplainsScope() {
        assertTrue(com.google.firebase.FirebaseApp.getApps(compose.activity).isEmpty())
        click("Başlayaq"); click("Çəkini saxlamaq"); click("Davam et")
        compose.onNodeWithContentDescription("Yaşın").performScrollTo().performTextInput("28")
        compose.onNodeWithContentDescription("Boyun").performScrollTo().performTextInput("172")
        compose.onNodeWithContentDescription("Çəkin").performScrollTo().performTextInput("78,4")
        click("Kişi"); click("Davam et"); click("Yüngül fəal"); click("Davam et")
        repeat(2) {
            repeat(5) { index -> compose.onAllNodesWithText("Xeyr")[index].performScrollTo().performClick() }
            click("Davam et")
        }
        click("Xeyr"); click("Davam et")
        click("30 dəq"); click("Qənaətli"); click("4 dəfə"); click("Davam et")
        compose.onNodeWithText("Cavablar tamamdır").assertExists(); click("Davam et")
        click("Google ilə davam et"); compose.onNodeWithText("Giriş hələ qoşulmayıb").assertExists(); click("Başa düşdüm")
        click("İndi yox"); compose.onNodeWithText("Məqsəd: Çəkini saxlamaq").assertExists()
        compose.onNodeWithText("Gündə 4 yemək").assertExists()
        click("Seçimləri dəyiş"); click("Məqsəd"); click("Sağlam yemək"); click("Davam et")
        compose.onNodeWithText("Cavablar tamamdır").assertExists()
        click("Davam et"); click("İndi yox")
        compose.onNodeWithText("Məqsəd: Sağlam yemək").assertExists()
        click("Başla"); compose.onNodeWithText("1 900 kkal").assertExists()
        compose.onNodeWithText("Planlı yemək faktiki qəbul sayılmır.").assertExists()
        for (label in listOf("Menyu", "Əlavə et", "İrəliləyiş", "Profil")) {
            compose.onAllNodesWithText(label).onLast().performClick()
            compose.onNodeWithText("Bu bölmə növbəti mərhələdə hazırlanacaq. Hazırda yalnız “Bu gün” ekranının nümunə görünüşü var.").assertExists()
            click("Bu günə qayıt")
        }
        assertTrue(com.google.firebase.FirebaseApp.getApps(compose.activity).isEmpty())
    }
    @Test fun blankBodyDoesNotGetDefaultsAndRecreationRetainsGoal() {
        click("Başlayaq"); click("Sağlam yemək"); click("Davam et")
        click("Davam et"); compose.onNodeWithText("Yaşı tam müsbət rəqəmlə, boy və çəkini müsbət rəqəmlə yaz. Çəki üçün vergül işlədə bilərsən.").assertExists()
        compose.onNodeWithContentDescription("Geri").performScrollTo().performClick()
        compose.onNode(hasText("Sağlam yemək") and isSelected()).assertExists()
        compose.activityRule.scenario.recreate()
        compose.onNode(hasText("Sağlam yemək") and isSelected()).assertExists()
    }
    @Test fun allergyNoClearsSpecificsButPreservesSeparateGlutenAndChecklistReturns() {
        val state = PreviewState(completeAnswers()).apply { page = PreviewPage.Allergy; allergy("yes"); toggle("allergens", "nuts"); set("nuts", "badam"); set("gluten", "yes") }
        install(state)
        click("Xeyr")
        compose.runOnIdle { assertEquals("", state["allergens"]); assertEquals("", state["nuts"]); assertEquals("yes", state["gluten"]) }
        compose.runOnIdle { state.set("pregnancy", ""); state.go(PreviewPage.Review) }
        click("Sağlamlıq · 1/2")
        compose.onAllNodesWithText("Xeyr")[0].performScrollTo().performClick(); click("Davam et")
        compose.runOnIdle { assertEquals(PreviewPage.Review, state.page); assertFalse(state.editing); assertTrue(state.missing().isEmpty()) }
    }
    @Test fun unknownAllergyAndHealthRiskNeverShowEnergyOrMenuFixture() {
        val state = PreviewState(completeAnswers()).apply { set("allergy", ""); page = PreviewPage.Allergy }
        install(state); click("Davam et")
        compose.onNodeWithText("Allergiya məlumatı çatmır").assertExists(); click("Hədəfsiz davam et")
        compose.runOnIdle { state.go(PreviewPage.Today) }
        compose.onNodeWithText("Gündəliyin hazırdır").assertExists(); compose.onNodeWithText("1 900 kkal").assertDoesNotExist()
        compose.runOnIdle { state.allergy("no"); state.set("pregnancy", "yes"); state.go(PreviewPage.Ready) }
        compose.onNodeWithText("Günün nümunə menyusu").assertDoesNotExist()
    }
    @Test fun stateContractsPreserveUnknownAndDecimalCommaWithoutNutritionLogic() {
        val state = PreviewState()
        assertEquals(7, state.missing().size)
        state.set("age", "28,5"); assertFalse(state.validNumber("age"))
        state.set("age", "28"); state.set("weight", "78,4"); assertTrue(state.validNumber("age")); assertTrue(state.validNumber("weight"))
        assertTrue(state.diary)
        val full = PreviewState(completeAnswers()); assertTrue(full.missing().isEmpty()); assertFalse(full.diary)
        full.set("sex", "unspecified"); assertTrue(full.missing().isEmpty()); assertTrue(full.diary)
        full.set("sex", "male"); full.set("activity", "uncertain"); assertTrue(full.diary)
    }
    @Test fun explicitRiskNextShowsWarningAndReviewKeepsTheDraft() {
        val state = PreviewState(completeAnswers()).apply { page = PreviewPage.Health2; set("loss", "yes") }
        install(state); click("Davam et")
        compose.onNodeWithText("Fərdi dəstək daha uyğundur").assertExists()
        click("Cavabları nəzərdən keçir")
        compose.runOnIdle { assertEquals("yes", state["loss"]); assertEquals(PreviewPage.Health1, state.page) }
    }
    @Test fun specificAllergensRequireDetailsAndDecimalSteppersRetainInput() {
        val state = PreviewState(completeAnswers()).apply { page = PreviewPage.Allergy; allergy("yes"); toggle("allergens", "nuts") }
        install(state); click("Davam et")
        compose.runOnIdle { assertEquals(R.string.dp_nuts_detail, state.error); assertEquals(PreviewPage.Allergy, state.page) }
        compose.onNode(hasSetTextAction() and hasText("Qoz-fındıq növünü dəqiqləşdir")).performScrollTo().performTextInput("badam")
        click("Davam et")
        compose.runOnIdle { assertEquals(PreviewPage.Preferences, state.page); state.go(PreviewPage.Body) }
        compose.onNodeWithContentDescription("Çəkin: artır").performScrollTo().performClick()
        compose.onNodeWithText("79,4").assertExists()
        compose.onNodeWithContentDescription("Çəkin: azalt").performScrollTo().performClick()
        compose.onNodeWithText("78,4").assertExists()
    }
    @Test fun languageSelectionAndBodyAnswersSurviveActivityRecreation() {
        click("Başlayaq"); click("Sağlam yemək"); click("Davam et")
        compose.onNodeWithContentDescription("Çəkin").performScrollTo().performTextInput("78,4")
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("78,4").assertExists()
        compose.onNodeWithContentDescription("Geri").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Geri").performScrollTo().performClick()
        compose.onNodeWithText("🇦🇿 AZ⌄").performClick(); click("English")
        compose.onNodeWithText("Welcome!").assertExists()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Welcome!").assertExists()
    }
    private fun install(state: PreviewState) {
        compose.activityRule.scenario.onActivity { it.setContent { OnboardingPreview(state) } }
        compose.waitForIdle()
    }
    @Test fun renderApprovedStatesInThreeLanguagesAtBothFontScales() = renderMatrix(false)
    @Test @Config(qualifiers = "w320dp-h700dp-mdpi") fun renderCompactPhone() = renderMatrix(true)
    @Test @Config(qualifiers = "w430dp-h932dp-mdpi") fun renderLargePhone() = renderMatrix(true)
    private fun renderMatrix(phoneOnly: Boolean) {
        var model by mutableStateOf(PreviewState())
        var fontScale by mutableStateOf(1f)
        compose.activityRule.scenario.onActivity { activity -> activity.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale)) { key(model) { OnboardingPreview(model) } }
        } }
        val surfaces = linkedMapOf("welcome" to 3, "goal" to 2, "body" to 4, "activity" to 4, "health" to 5,
            "allergy" to 4, "meal_preferences" to 3, "incomplete" to 2, "account" to 3, "ready" to 2, "today" to 3)
        val width = compose.activity.resources.configuration.screenWidthDp
        for (language in if (phoneOnly) listOf("az") else listOf("az", "en", "ru")) for (scale in listOf(1f, 1.5f)) {
            for ((surface, count) in surfaces) for (index in 0 until if (phoneOnly) 1 else count) {
                compose.runOnIdle { model = fixture(surface, index).apply { this.language = language }; fontScale = scale }
                compose.waitForIdle(); compose.mainClock.advanceTimeBy(500)
                val prefix = if (phoneOnly) "phone-$width-" else ""
                val name = "$prefix$surface-$index-$language-${if(scale == 1f) "100" else "150"}"
                capture(name)
                if (model.dialog != PreviewDialog.None || model.page != PreviewPage.Today) {
                    val label = if (model.dialog != PreviewDialog.None) when(model.dialog) {
                        PreviewDialog.Language -> R.string.dp_close
                        PreviewDialog.AllergyMissing -> R.string.dp_complete_answer
                        PreviewDialog.Future -> R.string.dp_return_today
                        else -> R.string.dp_understood
                    } else when(model.page) {
                        PreviewPage.Welcome -> R.string.dp_start_welcome
                        PreviewPage.Review -> if(model.missing().isEmpty()) R.string.dp_next else R.string.dp_complete_answers
                        PreviewPage.Warning -> R.string.dp_diary_continue
                        PreviewPage.Account -> R.string.dp_not_now
                        PreviewPage.Ready -> R.string.dp_start
                        else -> R.string.dp_next
                    }
                    val cfg = android.content.res.Configuration(compose.activity.resources.configuration).apply { setLocale(java.util.Locale.forLanguageTag(language)) }
                    val text = compose.activity.createConfigurationContext(cfg).getString(label)
                    compose.onNodeWithText(text).performScrollTo()
                    capture("$name-bottom")
                }
            }
        }
    }
    private fun capture(name: String) {
        compose.waitForIdle()
        compose.runOnIdle {
            val view = compose.activity.window.decorView
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            // Compose dialogs use separate windows. Composite those actual native windows too.
            WindowInspector.getGlobalWindowViews().filter { it !== view && it.isShown && it.isAttachedToWindow }.forEach { window ->
                canvas.drawColor(android.graphics.Color.argb(100, 18, 55, 67))
                val xy = IntArray(2); window.getLocationOnScreen(xy)
                // Robolectric does not position dialog windows. Apply the platform CENTER gravity
                // when drawing their real decor; these are JVM renders, not device screenshots.
                val x = if (xy[0] == 0) (view.width - window.width) / 2f else xy[0].toFloat()
                val y = if (xy[1] == 0) (view.height - window.height) / 2f else xy[1].toFloat()
                canvas.save(); canvas.translate(x, y); window.draw(canvas); canvas.restore()
            }
            val file = File("build/reports/onboarding-preview/$name.png")
            file.parentFile!!.mkdirs(); file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }; bitmap.recycle()
            assertTrue(file.length() > 1000)
        }
    }
    private fun fixture(surface: String, index: Int): PreviewState {
        val base = if (surface in listOf("welcome", "goal", "body", "activity", "health", "allergy", "meal_preferences", "incomplete")) emptyMap() else completeAnswers()
        return PreviewState(base).apply {
            when(surface) {
                "welcome" -> { page = PreviewPage.Welcome; dialog = listOf(PreviewDialog.None, PreviewDialog.Language, PreviewDialog.Provider)[index] }
                "goal" -> { page = PreviewPage.Goal; if(index == 1) set("goal", "maintain") }
                "body" -> { page = PreviewPage.Body; if(index == 1) { set("age", "28"); set("height", "172"); set("weight", "78,4"); set("sex", "male") }; if(index == 2) error = R.string.dp_body_error; if(index == 3) set("sex", "unspecified") }
                "activity" -> { page = PreviewPage.Activity; if(index == 1) set("activity", "low_active"); if(index == 2) dialog = PreviewDialog.ActivityHelp; if(index == 3) set("activity", "uncertain") }
                "health" -> { page = if(index == 1) PreviewPage.Health2 else if(index == 4) PreviewPage.Warning else PreviewPage.Health1; if(index == 2) PreviewState.healthKeys.forEach { set(it, "no") }; if(index == 3) error = R.string.dp_health_note }
                "allergy" -> { page = PreviewPage.Allergy; if(index == 1) allergy("no"); if(index == 2) { allergy("yes"); toggle("allergens", "milk"); toggle("allergens", "egg") }; if(index == 3) dialog = PreviewDialog.AllergyMissing }
                "meal_preferences" -> { page = PreviewPage.Preferences; if(index == 1) { set("time", "30"); set("budget", "economical"); set("meals", "4") }; if(index == 2) error = R.string.dp_preferences_error }
                "incomplete" -> { page = PreviewPage.Review; if(index == 1) completeAnswers().forEach { (key, value) -> set(key, value) } }
                "account" -> { page = PreviewPage.Account; dialog = listOf(PreviewDialog.None, PreviewDialog.Provider, PreviewDialog.Privacy)[index] }
                "ready" -> { page = PreviewPage.Ready; forcedDiary = index == 1 }
                "today" -> { page = PreviewPage.Today; forcedDiary = index == 1; if(index == 2) future(R.string.dp_menu) }
            }
        }
    }
    private fun completeAnswers(): Map<String, String> = mapOf("goal" to "maintain", "age" to "28", "height" to "172", "weight" to "78,4", "sex" to "male", "activity" to "low_active",
        "allergy" to "no", "time" to "30", "budget" to "economical", "meals" to "3") + PreviewState.healthKeys.associateWith { "no" }
}
