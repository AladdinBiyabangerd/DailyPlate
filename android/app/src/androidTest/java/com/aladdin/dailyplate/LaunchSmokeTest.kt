package com.aladdin.dailyplate

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class LaunchSmokeTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchAndRecreateKeepTheEntrySurfaceVisible() {
        assertEntryVisible()
        compose.activityRule.scenario.recreate()
        assertEntryVisible()
    }

    private fun assertEntryVisible() {
        compose.onNodeWithText("DailyPlate").assertIsDisplayed()
        compose.onNodeWithText("Xoş gəldin!").assertIsDisplayed()
        compose.onNodeWithText("Nə yeyəcəyini birlikdə planlayaq.").assertIsDisplayed()
    }
}
