package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.aladdin.dailyplate.R

@Composable internal fun TabletAccount(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    TabletFrame(state, R.string.dp_account, if (wide) 1040.dp else 640.dp, centered = true) {
        TabletSplit(left = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TabletArt(R.drawable.dp_art_account, 256.dp, 224.dp); Gap(32.dp)
                Label(copy(R.string.dp_account_title), size = 32, bold = true, center = true, modifier = Modifier.fillMaxWidth()); Gap(24.dp)
                Label(copy(R.string.dp_account_intro), muted = true, center = true, modifier = Modifier.fillMaxWidth())
            }
        }, right = {
            Column {
                Panel(onClick = { state.dialog = PreviewDialog.Provider }) {
                    Row(Modifier.heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Label("G", size = 24, bold = true); Label(copy(R.string.dp_google), size = 20, bold = true, modifier = Modifier.weight(1f))
                    }
                }
                Gap(24.dp)
                Surface(shape = PreviewStyle.button, color = colorResource(R.color.simple_yellow)) {
                    Column(Modifier.padding(24.dp)) { Label(copy(R.string.dp_guest_title), size = 18, bold = true); Gap(8.dp); Label(copy(R.string.dp_guest_note), size = 18) }
                }
                Secondary(R.string.dp_privacy) { state.dialog = PreviewDialog.Privacy }
                Gap(16.dp); Primary(R.string.dp_not_now) { state.go(PreviewPage.Ready) }
                Gap(16.dp); Label(copy(R.string.dp_account_later), muted = true, center = true, modifier = Modifier.fillMaxWidth())
            }
        })
    }
}
@Composable internal fun TabletReady(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    TabletFrame(state, if (state.diary) R.string.dp_diary_ready else R.string.dp_ready, if (wide) 1040.dp else 704.dp, centered = true) {
        TabletSplit(left = { TabletReadyOverview(state) }, right = { TabletReadyMenu(state) })
    }
}
@Composable private fun TabletReadyOverview(state: PreviewState) {
    Column {
        TabletArt(R.drawable.dp_art_recipe, 224.dp, 192.dp, Modifier.align(Alignment.CenterHorizontally)); Gap(16.dp)
        Label(copy(R.string.dp_ready_title), size = if (LocalTabletLayout.current!!.wide) 26 else 32, bold = true, center = true, modifier = Modifier.fillMaxWidth()); Gap(24.dp)
        val goal = goals.find { it.key == state["goal"] }?.let { copy(it.title) } ?: copy(R.string.dp_not_selected)
        Panel {
            listOf("🎯" to copy(R.string.dp_goal_summary, goal), "🍽" to if (state["meals"].isBlank()) copy(R.string.dp_not_selected) else copy(R.string.dp_meal_summary, state["meals"]), "ⓘ" to copy(R.string.dp_no_calorie)).forEach { (icon, text) ->
                Row(Modifier.heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Label(icon, size = 24, modifier = Modifier.widthIn(min = 32.dp)); Label(text, size = if (LocalTabletLayout.current!!.wide) 16 else 18, bold = true, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
@Composable private fun TabletReadyMenu(state: PreviewState) {
    Column {
        Notice(if (state.diary) R.string.dp_diary_notice else R.string.dp_demo_notice); Gap(24.dp)
        if (state.diary) Notice(R.string.dp_diary_info, yellow = true) else {
            Label(copy(R.string.dp_sample_menu), size = 20, bold = true); Gap(16.dp)
            TabletGrid(slotLabels(state).mapIndexed { i, label -> label to listOf(R.drawable.dp_art_egg, R.drawable.dp_art_healthy, R.drawable.dp_art_recipe)[i % 3] }, 3, 16.dp) { (label, art) ->
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { TabletArt(art, 80.dp); Gap(8.dp); Label(label, bold = true, center = true) }
            }
            Gap(16.dp); Label(copy(R.string.dp_sample_caption), muted = true)
        }
        TabletFooter(state, R.string.dp_start, secondary = R.string.dp_change_choices,
            secondaryAction = { state.reviewAll = true; state.go(PreviewPage.Review) }) { state.go(PreviewPage.Today) }
    }
}
