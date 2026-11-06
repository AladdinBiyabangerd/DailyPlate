package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.aladdin.dailyplate.R

@Composable internal fun TabletWelcome(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    TabletFrame(state, R.string.dp_welcome, if (wide) 1040.dp else 560.dp, centered = true) {
        TabletSplit(left = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TabletArt(R.drawable.dp_art_welcome, 480.dp, 320.dp, Modifier.fillMaxWidth()); Gap()
                Label(copy(R.string.dp_welcome_sub), size = 32, bold = true, center = true, modifier = Modifier.fillMaxWidth())
            }
        }, right = {
            Column {
                listOf(R.string.dp_benefit_log to R.drawable.dp_art_log, R.string.dp_benefit_menu to R.drawable.dp_art_menu,
                    R.string.dp_benefit_progress to R.drawable.dp_art_progress).forEach { (title, art) ->
                    Panel { Row(Modifier.heightIn(min = 72.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        TabletArt(art, 96.dp, 72.dp); Label(copy(title), size = 20, bold = true, modifier = Modifier.weight(1f))
                    } }; Gap(16.dp)
                }
                Gap(16.dp); Primary(R.string.dp_start_welcome) { state.go(PreviewPage.Goal) }
                Secondary(R.string.dp_have_account) { state.dialog = PreviewDialog.Provider }
            }
        })
    }
}
@Composable internal fun TabletChoice(item: IllustratedChoice, selected: Boolean, tall: Boolean,
    activity: Boolean = false, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().fillMaxHeight().selectable(selected, role = Role.RadioButton, onClick = onClick), shape = PreviewStyle.radius,
        color = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null, shadowElevation = 8.dp) {
        Box {
            if (tall) Column(Modifier.fillMaxWidth().heightIn(min = if (activity) 272.dp else 320.dp).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                TabletArt(item.art, 128.dp); Gap(24.dp); Label(copy(item.title), size = 20, bold = true, center = true)
                Gap(8.dp); Label(copy(item.detail), muted = true, center = true)
            } else Row(Modifier.fillMaxWidth().heightIn(min = if (activity) 144.dp else 160.dp).padding(24.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                TabletArt(item.art, 96.dp); Column(Modifier.weight(1f)) { Label(copy(item.title), size = 20, bold = true); Gap(8.dp); Label(copy(item.detail), muted = true) }
            }
            if (selected) Label("✓", Modifier.align(Alignment.TopEnd).padding(8.dp), bold = true)
        }
    }
}
@Composable internal fun TabletGoal(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    TabletFrame(state, R.string.dp_goal, if (wide) 1040.dp else 704.dp, centered = wide, top = if (wide) 32.dp else 48.dp) {
        Label(copy(R.string.dp_goal_intro), center = wide, modifier = Modifier.fillMaxWidth()); Gap(32.dp)
        TabletGrid(goals, 2) { item -> TabletChoice(item, state["goal"] == item.key, !wide) { state.set("goal", item.key) } }
        Gap(32.dp); Label(copy(R.string.dp_goal_hint), muted = true, center = true, modifier = Modifier.fillMaxWidth())
        TabletFooter(state, enabled = state["goal"].isNotBlank()) { state.next(PreviewPage.Body) }
    }
}
@Composable internal fun TabletActivity(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    TabletFrame(state, R.string.dp_activity, if (wide) 1040.dp else 704.dp, top = if (wide) 24.dp else 32.dp) {
        Label(copy(R.string.dp_activity_intro)); Secondary(R.string.dp_activity_help) { state.dialog = PreviewDialog.ActivityHelp }; Gap(24.dp)
        TabletGrid(activities, 2, if (wide) 16.dp else 24.dp) { item ->
            TabletChoice(item, state["activity"] == item.key, !wide, activity = true) { state.set("activity", item.key) }
        }
        Gap(24.dp); TabletRadio(copy(R.string.dp_uncertain), state["activity"] == "uncertain", Modifier.fillMaxWidth()) { state.set("activity", "uncertain") }
        Gap(24.dp); Label(copy(if (state["activity"] == "uncertain") R.string.dp_uncertain_hint else R.string.dp_activity_hint), muted = true, center = true, modifier = Modifier.fillMaxWidth())
        TabletFooter(state, enabled = state["activity"].isNotBlank()) { state.next(PreviewPage.Health1) }
    }
}
