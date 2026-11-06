package com.aladdin.dailyplate.ui.preview

import android.content.res.Configuration
import android.os.LocaleList
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.ui.theme.QidaTheme
import java.util.Locale

/** Offline UI entry. No repository, Firebase, auth, purchase or nutrition services. */
@Composable fun OnboardingPreview(state: PreviewState = rememberSaveable(saver = PreviewState.Saver) { PreviewState() }) {
    val originalContext = LocalContext.current
    val originalConfiguration = LocalConfiguration.current
    val config = remember(originalConfiguration, state.language) {
        Configuration(originalConfiguration).apply { setLocales(LocaleList(Locale.forLanguageTag(state.language))) }
    }
    val context = remember(originalContext, config) { originalContext.createConfigurationContext(config) }
    CompositionLocalProvider(LocalContext provides context, LocalConfiguration provides config) {
        QidaTheme {
            BackHandler(state.dialog != PreviewDialog.None || state.page != PreviewPage.Welcome) { state.back() }
            BoxWithConstraints(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding()) {
              val layout = if (maxWidth >= 600.dp && maxHeight >= 600.dp) TabletLayout(maxWidth > maxHeight, maxWidth >= 1000.dp) else null
              CompositionLocalProvider(LocalTabletLayout provides layout) {
               Box(Modifier.fillMaxSize().imePadding()) {
                key(state.page) {
                    if (layout != null) TabletOnboarding(state) else when (state.page) {
                        PreviewPage.Welcome -> Welcome(state)
                        PreviewPage.Goal -> Goal(state)
                        PreviewPage.Body -> Body(state)
                        PreviewPage.Activity -> Activity(state)
                        PreviewPage.Health1 -> Health(state, false)
                        PreviewPage.Health2 -> Health(state, true)
                        PreviewPage.Warning -> Warning(state)
                        PreviewPage.Allergy -> Allergy(state)
                        PreviewPage.Preferences -> Preferences(state)
                        PreviewPage.Review -> Review(state)
                        PreviewPage.Account -> Account(state)
                        PreviewPage.Ready -> Ready(state)
                        PreviewPage.Today -> Today(state)
                    }
                }
                PreviewDialogs(state)
               }
              }
            }
        }
    }
}
@Composable private fun PreviewDialogs(state: PreviewState) {
    if (state.dialog == PreviewDialog.None) return
    val localizedContext = LocalContext.current
    val localizedConfiguration = LocalConfiguration.current
    val localizedDensity = LocalDensity.current
    val dialogMaxHeight = with(localizedDensity) { LocalWindowInfo.current.containerSize.height.toDp() } - 64.dp
    Dialog(onDismissRequest = { state.dialog = PreviewDialog.None }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
      CompositionLocalProvider(LocalContext provides localizedContext, LocalConfiguration provides localizedConfiguration, LocalDensity provides localizedDensity) {
        Surface(modifier = Modifier.widthIn(max = if (LocalTabletLayout.current != null) { if (state.dialog == PreviewDialog.ActivityHelp) { if (LocalTabletLayout.current!!.wide) 960.dp else 704.dp } else if (state.page == PreviewPage.Today) 560.dp else 640.dp } else 350.dp).fillMaxWidth(), shape = PreviewStyle.radius) {
            Column(Modifier.fillMaxWidth().heightIn(max = dialogMaxHeight).verticalScroll(rememberScrollState()).padding(if (LocalTabletLayout.current != null) 32.dp else 24.dp)) {
                when (state.dialog) {
                    PreviewDialog.Language -> {
                        Label(copy(R.string.dp_language), size = 24, bold = true); Gap()
                        listOf("az" to "Azərbaycan dili", "en" to "English", "ru" to "Русский").forEach { (code, name) ->
                            RadioChoice(name, state.language == code, Modifier.fillMaxWidth()) { state.language = code; state.dialog = PreviewDialog.None }; Gap(8.dp)
                        }
                        Secondary(R.string.dp_close) { state.dialog = PreviewDialog.None }
                    }
                    PreviewDialog.ActivityHelp -> {
                        Label(copy(R.string.dp_help_title), size = 24, bold = true); Gap(); Label(copy(R.string.dp_help_intro)); Gap()
                        val help = listOf(R.string.dp_inactive to R.string.dp_help_inactive, R.string.dp_low_active to R.string.dp_help_low_active,
                            R.string.dp_active to R.string.dp_help_active, R.string.dp_very_active to R.string.dp_help_very_active)
                        if (LocalTabletLayout.current?.wide == true) TabletGrid(help, columns = 2) { (title, text) ->
                            Column { Label(copy(title), size = 20, bold = true); Gap(8.dp); Label(copy(text)); Gap(24.dp) }
                        } else help.forEach { (title, text) ->
                            Label(copy(title), size = 20, bold = true); Gap(8.dp); Label(copy(text)); Gap(24.dp)
                        }
                        Notice(R.string.dp_help_notice); Gap(); Label(copy(R.string.dp_help_source), muted = true); Gap()
                        Primary(R.string.dp_understood, arrow = false) { state.dialog = PreviewDialog.None }
                    }
                    PreviewDialog.HealthSkip -> {
                        Label(copy(R.string.dp_diary_confirm), size = 24, bold = true); Gap(); Label(copy(R.string.dp_health_skip_note)); Gap()
                        Primary(R.string.dp_diary_continue, arrow = false) { state.set("healthIncomplete", "yes"); state.next(PreviewPage.Allergy) }
                        Secondary(R.string.dp_return_questions) { state.dialog = PreviewDialog.None }
                    }
                    PreviewDialog.AllergyMissing -> {
                        Label(copy(R.string.dp_allergy_missing), size = 24, bold = true); Gap(); Label(copy(R.string.dp_allergy_missing_copy)); Gap()
                        Primary(R.string.dp_complete_answer, arrow = false) { state.dialog = PreviewDialog.None }
                        Secondary(R.string.dp_diary_continue) { state.set("allergyUnknown", "yes"); state.next(PreviewPage.Preferences) }
                    }
                    else -> {
                        val title = when(state.dialog) { PreviewDialog.Provider -> R.string.dp_provider_title; PreviewDialog.Privacy -> R.string.dp_privacy_title; else -> state.futureTitle }
                        val text = when(state.dialog) { PreviewDialog.Provider -> R.string.dp_provider_copy; PreviewDialog.Privacy -> R.string.dp_privacy_copy; else -> R.string.dp_future_copy }
                        Label(copy(title), size = 24, bold = true); Gap(); Label(copy(text)); Gap()
                        Primary(if (state.dialog == PreviewDialog.Future) R.string.dp_return_today else R.string.dp_understood, arrow = false) { state.dialog = PreviewDialog.None }
                    }
                }
            }
        }
    }
  }
}
