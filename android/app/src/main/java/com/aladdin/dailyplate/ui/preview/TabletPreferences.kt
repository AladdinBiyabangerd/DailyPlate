package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aladdin.dailyplate.R

private data class TabletPreference(val key: String, val title: Int, val art: Int, val hint: Int, val choices: List<Pair<String, Int>>)
@Composable internal fun TabletPreferences(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    val groups = listOf(
        TabletPreference("time", R.string.dp_time, R.drawable.dp_art_cooking, R.string.dp_time_hint, listOf("15" to R.string.dp_time15, "30" to R.string.dp_time30, "60" to R.string.dp_time60)),
        TabletPreference("budget", R.string.dp_budget, R.drawable.dp_art_budget, R.string.dp_budget_hint, listOf("economical" to R.string.dp_economical, "medium" to R.string.dp_medium, "flexible" to R.string.dp_flexible)),
        TabletPreference("meals", R.string.dp_meals, R.drawable.dp_art_menu, R.string.dp_meals_hint, listOf("3" to R.string.dp_meals3, "4" to R.string.dp_meals4, "5" to R.string.dp_meals5)))
    TabletFrame(state, R.string.dp_preferences, if (wide) 1120.dp else 704.dp, top = 24.dp) {
        Label(copy(R.string.dp_preferences_intro)); Gap(24.dp)
        if (wide) Box(Modifier.fillMaxWidth()) {
            // Backgrounds match the tallest naturally measured column without
            // intrinsic/fixed heights that can truncate translated 150% text.
            Row(Modifier.matchParentSize(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                groups.forEach { Surface(Modifier.weight(1f).fillMaxHeight(), shape = PreviewStyle.radius, shadowElevation = 8.dp) {} }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                groups.forEach { group ->
                    Column(Modifier.weight(1f).padding(24.dp)) { TabletPreferenceContent(state, group, true) }
                }
            }
        }
        else Column(verticalArrangement = Arrangement.spacedBy(24.dp)) { groups.forEach { TabletPreferenceCard(state, it, false) } }
        TabletFooter(state) {
            if (PreviewPage.Preferences in state.missing()) state.error = R.string.dp_preferences_error else state.next(PreviewPage.Review)
        }
    }
}
@Composable private fun TabletPreferenceCard(state: PreviewState, group: TabletPreference, wide: Boolean) = Panel(contentPadding = 24.dp) {
    TabletPreferenceContent(state, group, wide)
}
@Composable private fun ColumnScope.TabletPreferenceContent(state: PreviewState, group: TabletPreference, wide: Boolean) {
    if (wide) {
        TabletArt(group.art, 128.dp, 96.dp, Modifier.align(Alignment.CenterHorizontally)); Gap(16.dp)
        Label(copy(group.title), size = 24, bold = true, modifier = Modifier.heightIn(min = 64.dp))
    } else Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        TabletArt(group.art, 96.dp, 80.dp); Label(copy(group.title), size = 24, bold = true, modifier = Modifier.weight(1f))
    }
    Gap(16.dp)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        group.choices.forEach { (value, title) ->
            val selected = state[group.key] == value
            Surface(onClick = { state.set(group.key, value) }, shape = PreviewStyle.button,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                modifier = Modifier.weight(1f).widthIn(min = 72.dp).heightIn(min = 56.dp)) {
                Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                    Text((if (selected) "✓ " else "") + copy(title), textAlign = TextAlign.Center,
                        fontSize = if (wide) 16.sp else 18.sp, fontWeight = FontWeight.Bold,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
    Gap(16.dp); Label(copy(group.hint), muted = true)
    if (group.key == "meals" && state["meals"].isNotBlank()) {
        Gap(16.dp)
        Surface(Modifier.fillMaxWidth(), shape = PreviewStyle.button, color = MaterialTheme.colorScheme.background) {
            Label(slotLabels(state).joinToString(" · "), Modifier.padding(8.dp))
        }
    }
}
