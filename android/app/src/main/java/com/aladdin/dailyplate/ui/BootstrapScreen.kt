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
import androidx.compose.ui.semantics.contentDescription
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
                    .padding(if (navigation.current == Destination.Welcome) QidaSpacing.medium else QidaSpacing.large),
                    verticalArrangement = Arrangement.spacedBy(if (navigation.current == Destination.Welcome) QidaSpacing.small else QidaSpacing.medium)) {
                    if (navigation.stack.size > 1 && navigation.current != Destination.Setup) TextButton(onClick = { route = navigation.back().encode() },
                        modifier = Modifier.heightIn(min = QidaDimensions.touchTarget),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Text("‹"); Text(stringResource(R.string.back))
                    }
                    if (navigation.current == Destination.Welcome || navigation.current == Destination.Profile) {
                        LanguagePicker(language) { code ->
                            preferences.edit { putString("language", code) }
                            language = code
                        }
                    }
                    when (navigation.current) {
                        Destination.Welcome -> {
                            QidaHeading(stringResource(R.string.welcome_title))
                            QidaArt(R.drawable.art_welcome, Modifier.fillMaxWidth().height(com.aladdin.dailyplate.ui.theme.QidaDimensions.heroArt))
                            Text(stringResource(R.string.welcome_message), Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge)
                            listOf(R.string.feature_log to R.drawable.art_log, R.string.feature_menu to R.drawable.art_menu,
                                R.string.feature_progress to R.drawable.art_progress).forEach { (label, art) ->
                                Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface,
                                    shadowElevation = QidaDimensions.cardElevation) { Row(Modifier.fillMaxWidth().padding(QidaSpacing.small), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                                    QidaArt(art, Modifier.size(QidaDimensions.touchTarget))
                                    Text(stringResource(label), style = MaterialTheme.typography.titleMedium)
                                } }
                            }
                            QidaButton(stringResource(R.string.begin), { open(Destination.Setup) })
                            TextButton(onClick = { open(Destination.Account) }, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget)) {
                                Text(stringResource(R.string.account), style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        Destination.Setup -> com.aladdin.dailyplate.ui.onboarding.OnboardingScreen(language, { route = navigation.back().encode() }, { scope.launch { contentScroll.scrollTo(0) } }) { open(Destination.Today) }
                        Destination.Account -> {
                            QidaArt(R.drawable.art_account, Modifier.fillMaxWidth().height(QidaDimensions.heroArt))
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
                                com.aladdin.dailyplate.ui.profile.ProfileScreen({ scope.launch { contentScroll.scrollTo(0) } }) { open(Destination.Setup) }
                            } else if (navigation.current == Destination.Add) {
                                com.aladdin.dailyplate.ui.entry.FoodEntryScreen { scope.launch { contentScroll.scrollTo(0) } }
                            } else {
                                val art = when (navigation.current) {
                                    Destination.Today -> R.drawable.art_empty
                                    Destination.Menu -> R.drawable.art_empty_menu
                                    Destination.Add -> R.drawable.art_empty_add
                                    else -> R.drawable.art_empty_progress
                                }
                                QidaArt(art, Modifier.fillMaxWidth().height(QidaDimensions.heroArt))
                                QidaState(SurfaceState.Empty, stringResource(message))
                            }
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
                                    shape = MaterialTheme.shapes.large,
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = if (destination == Destination.Add) MaterialTheme.colorScheme.primary else if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface,
                                        contentColor = if (destination == Destination.Add) MaterialTheme.colorScheme.onPrimary else if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant),
                                    contentPadding = PaddingValues(QidaSpacing.small)) {
                                    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                                        val icon = when (destination) {
                                            Destination.Today -> R.drawable.nav_today
                                            Destination.Menu -> R.drawable.nav_menu
                                            Destination.Add -> R.drawable.nav_add
                                            Destination.Progress -> R.drawable.nav_progress
                                            else -> R.drawable.nav_profile
                                        }
                                        Icon(androidx.compose.ui.res.painterResource(icon), contentDescription = null,
                                            modifier = Modifier.size(QidaSpacing.large))
                                        Text(stringResource(label), style = MaterialTheme.typography.labelLarge,
                                            fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.ExtraBold else androidx.compose.ui.text.font.FontWeight.Normal)
                                    }
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
    var expanded by remember { mutableStateOf(false) }
    val languageLabel = stringResource(R.string.language)
    Box(Modifier.fillMaxWidth().wrapContentWidth(androidx.compose.ui.Alignment.End)) {
        TextButton(onClick = { expanded = true },
            modifier = Modifier.heightIn(min = QidaDimensions.touchTarget).semantics { contentDescription = languageLabel },
            colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large) {
            Text(language.uppercase(), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Text(" ⌄")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("az", "en", "ru").forEach { code ->
                DropdownMenuItem(text = { Text(code.uppercase()) },
                    onClick = { select(code); expanded = false })
            }
        }
    }
}
