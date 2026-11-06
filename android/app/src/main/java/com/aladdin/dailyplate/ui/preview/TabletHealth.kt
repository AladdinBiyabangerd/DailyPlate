package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.aladdin.dailyplate.R

@Composable internal fun TabletHealth(state: PreviewState, second: Boolean) {
    val wide = LocalTabletLayout.current!!.wide
    TabletFrame(state, R.string.dp_health, if (wide) 1040.dp else 704.dp, top = if (wide) 16.dp else 32.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            TabletArt(R.drawable.dp_art_health, if (wide) 48.dp else 96.dp)
            Column(Modifier.weight(1f)) { Label(copy(R.string.dp_health_step, if (second) 2 else 1), size = if (wide) 18 else 20, bold = true); Gap(8.dp); Label(copy(R.string.dp_health_intro), muted = true) }
        }; Gap(if (wide) 8.dp else 32.dp)
        val indices = (if (second) 5..9 else 0..4).toList()
        TabletGrid(indices, 2, if (wide) 8.dp else 24.dp, centeredLast = !wide) { index ->
            val key = PreviewState.healthKeys[index]
            Panel(Modifier.fillMaxHeight(), contentPadding = if (wide) 16.dp else 24.dp) {
                Label(copy(healthQuestions[index]), size = if (wide) 16 else 18, bold = true,
                    modifier = if (wide) Modifier else Modifier.heightIn(min = 76.dp))
                Gap(if (wide) 8.dp else 16.dp)
                Row(Modifier.widthIn(max = if (wide) 224.dp else 1000.dp), horizontalArrangement = Arrangement.spacedBy(if (wide) 8.dp else 16.dp)) {
                    RadioChoice(copy(R.string.dp_yes), state[key] == "yes", Modifier.weight(1f)) { state.set(key, "yes") }
                    RadioChoice(copy(R.string.dp_no), state[key] == "no", Modifier.weight(1f)) { state.set(key, "no") }
                }
            }
        }
        Gap(16.dp); Label(copy(R.string.dp_health_note), muted = true, center = !wide, modifier = Modifier.fillMaxWidth())
        TabletFooter(state, secondary = R.string.dp_skip_answers, secondaryAction = { state.dialog = PreviewDialog.HealthSkip }) {
            if (indices.any { state[PreviewState.healthKeys[it]].isBlank() }) state.error = R.string.dp_health_note
            else {
                if (PreviewState.healthKeys.all { state[it] in listOf("yes", "no") }) state.set("healthIncomplete", "")
                if ((second || state.editing) && PreviewState.healthKeys.any { state[it] == "yes" }) state.go(PreviewPage.Warning)
                else state.next(if (second) PreviewPage.Allergy else PreviewPage.Health2)
            }
        }
    }
}
@Composable internal fun TabletWarning(state: PreviewState) = TabletFrame(state, R.string.dp_support, 704.dp) {
    TabletArt(R.drawable.dp_art_health, if (LocalTabletLayout.current!!.wide) 192.dp else 224.dp, modifier = Modifier.align(Alignment.CenterHorizontally)); Gap(32.dp)
    Label(copy(R.string.dp_warning_title), size = 26, bold = true, center = true, modifier = Modifier.fillMaxWidth()); Gap(24.dp)
    Surface(shape = PreviewStyle.radius, color = colorResource(R.color.simple_peach)) {
        Label(copy(R.string.dp_warning_copy), size = 18, modifier = Modifier.fillMaxWidth().padding(32.dp))
    }
    Gap(24.dp); Label(copy(R.string.dp_warning_hint), muted = true, center = true, modifier = Modifier.fillMaxWidth())
    TabletFooter(state, R.string.dp_diary_continue, arrow = false, secondary = R.string.dp_review_answers,
        secondaryAction = { state.go(PreviewPage.Health1) }) { state.next(PreviewPage.Allergy) }
}
