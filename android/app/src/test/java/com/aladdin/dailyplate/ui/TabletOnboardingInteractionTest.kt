package com.aladdin.dailyplate.ui

import android.app.Application
import androidx.activity.compose.setContent
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
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w800dp-h1280dp-port-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TabletOnboardingInteractionTest {
    @get:Rule val compose = createAndroidComposeRule<OnboardingPreviewActivity>()
    private fun az(id: Int): String = compose.activity.createConfigurationContext(android.content.res.Configuration(compose.activity.resources.configuration).apply { setLocale(java.util.Locale.forLanguageTag("az")) }).getString(id)
    private fun click(text: String) = compose.onNodeWithText(text).performScrollTo().performClick()
    private fun install(state: PreviewState) { compose.activityRule.scenario.onActivity { it.setContent { OnboardingPreview(state) } }; compose.waitForIdle() }
    @Test fun connectedFlowPreservesAnswersAndStaysOffline() {
        assertTrue(com.google.firebase.FirebaseApp.getApps(compose.activity).isEmpty())
        click("Başlayaq"); click("Çəkini saxlamaq"); click("Davam et")
        listOf("Yaşın" to "28", "Boyun" to "172", "Çəkin" to "78,4").forEach { (label, value) -> compose.onNodeWithContentDescription(label).performScrollTo().performTextInput(value) }
        click("Kişi"); click("Davam et"); click("Yüngül fəal"); click("Davam et")
        repeat(2) { repeat(5) { i -> compose.onAllNodesWithText("Xeyr")[i].performScrollTo().performClick() }; click("Davam et") }
        click("Xeyr"); click("Davam et"); click("30 dəq"); click("Qənaətli"); click("5 dəfə"); click("Davam et")
        click("Davam et"); click("Google ilə davam et"); compose.onNodeWithText("Giriş hələ qoşulmayıb").assertExists(); click("Başa düşdüm")
        click(az(R.string.dp_privacy)); click("Başa düşdüm"); click("İndi yox"); compose.onNodeWithText("Gündə 5 yemək").assertExists()
        click("Başla"); compose.onNodeWithText("1 900 kkal").assertExists()
        for (title in listOf("Menyu", "Əlavə et", "İrəliləyiş", "Profil")) {
            compose.onAllNodesWithText(title).onLast().performClick(); click("Bu günə qayıt")
        }
        assertTrue(com.google.firebase.FirebaseApp.getApps(compose.activity).isEmpty())
    }
    @Test fun recreationAndOrientationKeepStepAndDecimalDraft() {
        click("Başlayaq"); click("Sağlam yemək"); click("Davam et")
        compose.onNodeWithContentDescription("Çəkin").performScrollTo().performTextInput("78,4")
        RuntimeEnvironment.setQualifiers("w1280dp-h800dp-land-mdpi")
        compose.activityRule.scenario.recreate(); compose.waitForIdle()
        compose.onNodeWithTag("tablet-landscape").assertExists()
        compose.onNodeWithContentDescription("Çəkin").assertTextEquals("78,4")
        compose.onNodeWithText("Özünü tanıt").assertExists()
        RuntimeEnvironment.setQualifiers("w800dp-h1280dp-port-mdpi")
        compose.activityRule.scenario.recreate(); compose.waitForIdle()
        compose.onNodeWithTag("tablet-portrait").assertExists(); compose.onNodeWithContentDescription("Çəkin").assertTextEquals("78,4")
    }
    @Test fun validationChecklistAndAllergyDetailsRemainFunctional() {
        val state = TabletRenderCases.fixture("allergy", "nuts-error"); install(state); click("Davam et")
        compose.runOnIdle { assertEquals(R.string.dp_nuts_detail, state.error) }
        compose.onNode(hasSetTextAction() and hasText("Qoz-fındıq növünü dəqiqləşdir")).performScrollTo().performTextInput("badam")
        click("Davam et"); compose.runOnIdle { assertEquals(PreviewPage.Preferences, state.page); state.go(PreviewPage.Review) }
        click("Məqsəd"); click("Sağlam yemək"); click("Davam et")
        compose.runOnIdle { assertEquals(PreviewPage.Review, state.page); assertFalse(state.editing); assertEquals("badam", state["nuts"]) }
    }
    @Test @Config(qualifiers = "w1280dp-h800dp-land-mdpi") fun helpRiskAndDiaryRoutesAreSafeInLandscape() {
        val state = TabletRenderCases.fixture("activity", "empty"); install(state)
        click(az(R.string.dp_activity_help)); compose.onNodeWithText("Öz gününlə müqayisə et").assertExists(); click("Başa düşdüm")
        compose.runOnIdle { TabletRenderCases.complete().forEach { (key, value) -> state.set(key, value) }; state.set("loss", "yes"); state.go(PreviewPage.Health2) }
        click("Davam et"); compose.onNodeWithText("Fərdi dəstək daha uyğundur").assertExists()
        click("Hədəfsiz davam et"); compose.runOnIdle { assertEquals(PreviewPage.Allergy, state.page); state.go(PreviewPage.Today) }
        compose.onNodeWithText("1 900 kkal").assertDoesNotExist(); compose.onNodeWithText("Gündəliyin hazırdır").assertExists()
    }
    @Test @Config(qualifiers = "w900dp-h650dp-land-mdpi") fun narrowLandscapeUsesReachableStackInsteadOfCrampedThreeColumns() {
        val state = PreviewState(TabletRenderCases.complete()).apply { page = PreviewPage.Preferences }; install(state)
        compose.onNodeWithTag("tablet-landscape").assertExists(); click("5 dəfə"); click("Davam et")
        compose.runOnIdle { assertEquals(PreviewPage.Review, state.page); assertEquals("5", state["meals"]) }
    }
    @Test @Config(qualifiers = "w600dp-h900dp-port-mdpi") fun compactTabletMeasurementsReflowAndKeepDecimalSteppers() {
        val state = PreviewState(TabletRenderCases.complete()).apply { page = PreviewPage.Body }; install(state)
        compose.onNodeWithContentDescription("Çəkin: artır").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Çəkin").assertTextEquals("79,4"); click("Davam et")
        compose.runOnIdle { assertEquals(PreviewPage.Activity, state.page) }
    }

}
