package com.aladdin.dailyplate.ui

import android.content.res.Configuration
import androidx.core.content.edit
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.ui.theme.QidaDimensions
import com.aladdin.dailyplate.ui.theme.QidaSpacing
import java.util.Locale
import kotlinx.coroutines.launch

private val sections = listOf(Destination.Today to R.string.today, Destination.Menu to R.string.menu,
    Destination.Add to R.string.add, Destination.Progress to R.string.progress, Destination.Profile to R.string.profile)

/** K06 shell only. Feature boundaries explicitly explain that no data was created. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BootstrapScreen() {
    val base = LocalContext.current
    val preferences = remember { base.getSharedPreferences("presentation", 0) }
    var language by rememberSaveable { mutableStateOf(preferences.getString("language", "az") ?: "az") }
    var route by rememberSaveable { mutableStateOf(NavigationState().encode()) }
    val navigation = NavigationState.restore(route)
    val contentScroll = rememberScrollState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(navigation.current) { contentScroll.scrollTo(0) }
    val config = Configuration(LocalConfiguration.current).apply { setLocale(Locale.forLanguageTag(language)) }
    val localized = base.createConfigurationContext(config)
    BackHandler(navigation.stack.size > 1) { route = navigation.back().encode() }
    CompositionLocalProvider(LocalContext provides localized, LocalConfiguration provides config) {
        fun open(destination: Destination) { route = navigation.open(destination).encode() }
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                Column(Modifier.weight(1f).verticalScroll(contentScroll)
                    .padding(QidaSpacing.large), verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                    if (navigation.stack.size > 1 && navigation.current != Destination.Setup) TextButton(onClick = { route = navigation.back().encode() },
                        modifier = Modifier.heightIn(min = QidaDimensions.touchTarget)) { Text(stringResource(R.string.back)) }
                    if (navigation.current == Destination.Welcome || navigation.current == Destination.Profile) {
                        LanguagePicker(language) { code ->
                            preferences.edit { putString("language", code) }
                            language = code
                        }
                    }
                    when (navigation.current) {
                        Destination.Welcome -> {
                            QidaHeading(stringResource(R.string.welcome_title))
                            Text(stringResource(R.string.welcome_message), style = MaterialTheme.typography.bodyLarge)
                            listOf(R.string.feature_log, R.string.feature_menu, R.string.feature_progress).forEach {
                                QidaCard { Text(stringResource(it), style = MaterialTheme.typography.bodyLarge) }
                            }
                            QidaButton(stringResource(R.string.begin), { open(Destination.Setup) })
                            TextButton(onClick = { open(Destination.Account) }, modifier = Modifier.heightIn(min = QidaDimensions.touchTarget)) {
                                Text(stringResource(R.string.account), style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        Destination.Setup -> com.aladdin.dailyplate.ui.onboarding.OnboardingScreen(language, { route = navigation.back().encode() }, { scope.launch { contentScroll.scrollTo(0) } }) { open(Destination.Today) }
                        Destination.Account -> {
                            QidaHeading(stringResource(if (navigation.current == Destination.Setup) R.string.begin else R.string.sign_in))
                            QidaState(SurfaceState.Empty, stringResource(if (navigation.current == Destination.Setup) R.string.setup_pending else R.string.unavailable))
                            QidaButton(stringResource(R.string.continue_action), { open(Destination.Today) })
                        }
                        else -> {
                            QidaHeading(stringResource(sections.first { it.first == navigation.current }.second))
                            val message = when (navigation.current) {
                                Destination.Today -> R.string.empty_today
                                Destination.Menu -> R.string.empty_menu
                                Destination.Add -> R.string.empty_add
                                Destination.Progress -> R.string.empty_progress
                                else -> R.string.empty_profile
                            }
                            if (navigation.current == Destination.Profile) {
                                Text(stringResource(R.string.ob_local_note))
                                QidaButton(stringResource(R.string.ob_profile), { open(Destination.Setup) })
                            } else QidaState(SurfaceState.Empty, stringResource(message))
                            if (navigation.current == Destination.Today) {
                                QidaButton(stringResource(R.string.add_meal), { open(Destination.Add) })
                                QidaButton(stringResource(R.string.today_menu), { open(Destination.Menu) })
                            }
                        }
                    }
                }
                if (navigation.current in sections.map { it.first }) {
                    Surface(color = MaterialTheme.colorScheme.surface) {
                        FlowRow(Modifier.fillMaxWidth().padding(QidaSpacing.small),
                            horizontalArrangement = Arrangement.SpaceEvenly) {
                            sections.forEach { (destination, label) ->
                                val selected = navigation.current == destination
                                // Natural label widths flow into additional rows, including at large font sizes.
                                TextButton(onClick = { open(destination) }, modifier = Modifier
                                    .heightIn(min = QidaDimensions.touchTarget).semantics { role = Role.Tab; this.selected = selected },
                                    contentPadding = PaddingValues(QidaSpacing.small)) {
                                    Text((if (selected) "✓\n" else "") + stringResource(label),
                                        style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguagePicker(language: String, select: (String) -> Unit) {
    Text(stringResource(R.string.language), style = MaterialTheme.typography.bodyLarge)
    Row(Modifier.fillMaxWidth()) {
        listOf("az" to "AZ", "en" to "EN", "ru" to "RU").forEach { (code, label) ->
            FilterChip(selected = language == code, onClick = { select(code) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary),
                label = { Text((if (language == code) "✓ " else "") + label) },
                modifier = Modifier.weight(1f).heightIn(min = QidaDimensions.touchTarget))
        }
    }
}
