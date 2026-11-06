package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aladdin.dailyplate.R

internal data class IllustratedChoice(val key: String, val title: Int, val detail: Int, val art: Int)
internal val goals = listOf(
    IllustratedChoice("lose", R.string.dp_lose, R.string.dp_lose_sub, R.drawable.dp_art_lose),
    IllustratedChoice("maintain", R.string.dp_maintain, R.string.dp_maintain_sub, R.drawable.dp_art_maintain),
    IllustratedChoice("gain", R.string.dp_gain, R.string.dp_gain_sub, R.drawable.dp_art_gain),
    IllustratedChoice("healthy", R.string.dp_healthy, R.string.dp_healthy_sub, R.drawable.dp_art_healthy),
)
internal val activities = listOf(
    IllustratedChoice("inactive", R.string.dp_inactive, R.string.dp_inactive_sub, R.drawable.dp_art_sedentary),
    IllustratedChoice("low_active", R.string.dp_low_active, R.string.dp_low_active_sub, R.drawable.dp_art_walking),
    IllustratedChoice("active", R.string.dp_active, R.string.dp_active_sub, R.drawable.dp_art_active),
    IllustratedChoice("very_active", R.string.dp_very_active, R.string.dp_very_active_sub, R.drawable.dp_art_active),
)
@Composable internal fun Welcome(state: PreviewState) = Frame(state, R.string.dp_welcome) {
    Gap(8.dp)
    Art(R.drawable.dp_art_welcome, 342.dp, 208.dp, Modifier.fillMaxWidth())
    Label(copy(R.string.dp_welcome_sub), size = 26, bold = true, center = true, modifier = Modifier.fillMaxWidth())
    Gap()
    listOf(R.string.dp_benefit_log to R.drawable.dp_art_log, R.string.dp_benefit_menu to R.drawable.dp_art_menu,
        R.string.dp_benefit_progress to R.drawable.dp_art_progress).forEach { (title, art) ->
        Surface(shape = PreviewStyle.radius, shadowElevation = 2.dp) {
            Row(Modifier.fillMaxWidth().heightIn(min = 80.dp).padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Art(art, 88.dp, 64.dp); Label(copy(title), size = 20, bold = true, modifier = Modifier.weight(1f))
            }
        }
        Gap(8.dp)
    }
    Footer(state, R.string.dp_start_welcome, secondary = R.string.dp_have_account,
        secondaryAction = { state.dialog = PreviewDialog.Provider }) { state.go(PreviewPage.Goal) }
}
@Composable internal fun Goal(state: PreviewState) = Frame(state, R.string.dp_goal) {
    Gap(24.dp); Label(copy(R.string.dp_goal_intro), center = true, modifier = Modifier.fillMaxWidth()); Gap(24.dp)
    goals.forEach { item ->
        ChoiceCard(item.title, item.detail, item.art, state["goal"] == item.key) { state.set("goal", item.key) }; Gap()
    }
    Gap(8.dp); Label(copy(R.string.dp_goal_hint), muted = true, center = true, modifier = Modifier.fillMaxWidth())
    Footer(state, enabled = state["goal"].isNotBlank()) { state.next(PreviewPage.Body) }
}
@Composable internal fun Body(state: PreviewState) = Frame(state, R.string.dp_body) {
    Gap(); Label(copy(R.string.dp_body_intro), center = true, modifier = Modifier.fillMaxWidth()); Gap(24.dp)
    Metric(state, "age", R.string.dp_age, R.string.dp_years, R.drawable.dp_art_age)
    Metric(state, "height", R.string.dp_height, R.string.dp_cm, R.drawable.dp_art_height)
    Metric(state, "weight", R.string.dp_weight, R.string.dp_kg, R.drawable.dp_art_weight)
    Gap(); Label(copy(R.string.dp_sex), size = 22, bold = true); Gap(8.dp); Label(copy(R.string.dp_sex_note), muted = true); Gap()
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        listOf(Triple("male", R.string.dp_male, R.drawable.dp_art_male), Triple("female", R.string.dp_female, R.drawable.dp_art_female)).forEach { (key, title, art) ->
            Surface(Modifier.weight(1f).selectable(state["sex"] == key, role = Role.RadioButton, onClick = { state.set("sex", key) }),
                shape = PreviewStyle.radius, shadowElevation = 2.dp,
                border = if (state["sex"] == key) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null) {
                Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Art(art, 80.dp); Gap(8.dp)
                    Label((if (state["sex"] == key) "✓ " else "") + copy(title), size = 20, bold = true, center = true)
                }
            }
        }
    }
    Gap(8.dp)
    RadioChoice(copy(R.string.dp_sex_skip), state["sex"] == "unspecified", Modifier.fillMaxWidth()) { state.set("sex", "unspecified") }
    Gap(8.dp); Label(copy(R.string.dp_no_target), muted = true); Gap(8.dp)
    Notice(R.string.dp_body_privacy, yellow = true)
    Footer(state) {
        if (listOf("age", "height", "weight").all(state::validNumber)) state.next(PreviewPage.Activity)
        else state.error = R.string.dp_body_error
    }
}
@Composable private fun Metric(state: PreviewState, key: String, title: Int, unit: Int, art: Int) {
    val name = copy(title)
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val reflow = maxWidth < 320.dp || LocalDensity.current.fontScale > 1.15f
        Surface(shape = PreviewStyle.radius, shadowElevation = 2.dp) {
            Column(Modifier.fillMaxWidth().padding(8.dp)) {
                if (reflow) { Row(verticalAlignment = Alignment.CenterVertically) { Art(art, 32.dp); Spacer(Modifier.width(8.dp)); Label(name, size = 18, bold = true) }; Gap(8.dp) }
                Row(Modifier.fillMaxWidth().heightIn(min = 64.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!reflow) {
                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Art(art, 32.dp); Label(name, size = 16, bold = true, modifier = Modifier.weight(1f))
                        }
                    } else Spacer(Modifier.weight(1f))
                    StepButton("−", copy(R.string.dp_decrease, name)) { adjust(state, key, -1) }
                    BasicTextField(state[key], { state.set(key, it) }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = if (key == "age") KeyboardType.Number else KeyboardType.Decimal),
                        textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center),
                        modifier = Modifier.width(64.dp).heightIn(min = 48.dp).semantics { contentDescription = name },
                        decorationBox = { inner -> Box(Modifier.defaultMinSize(minHeight = 48.dp), contentAlignment = Alignment.Center) {
                            if (state[key].isBlank()) Label("—", size = 18, muted = true); inner()
                        } })
                    StepButton("+", copy(R.string.dp_increase, name)) { adjust(state, key, 1) }
                    Label(copy(unit), modifier = Modifier.widthIn(min = 24.dp))
                    if (reflow) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
    Gap(8.dp)
}
@Composable internal fun StepButton(symbol: String, description: String, onClick: () -> Unit) {
    Surface(onClick, shape = PreviewStyle.radius, color = MaterialTheme.colorScheme.background,
        modifier = Modifier.size(48.dp).semantics { contentDescription = description }) {
        Box(contentAlignment = Alignment.Center) { Text(symbol, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary) }
    }
}
internal fun adjust(state: PreviewState, key: String, delta: Int) {
    if (!state.validNumber(key)) { state.error = R.string.dp_number_first; return }
    val next = (state[key].replace(',', '.').toDouble() * 10).toLong() + delta * 10
    if (next > 0) state.set(key, if (next % 10 == 0L) (next / 10).toString() else "${next / 10},${next % 10}")
}
@Composable internal fun Activity(state: PreviewState) = Frame(state, R.string.dp_activity) {
    Gap(); Label(copy(R.string.dp_activity_intro), center = true, modifier = Modifier.fillMaxWidth())
    Secondary(R.string.dp_activity_help) { state.dialog = PreviewDialog.ActivityHelp }; Gap()
    activities.forEach { item -> ChoiceCard(item.title, item.detail, item.art, state["activity"] == item.key) { state.set("activity", item.key) }; Gap(8.dp) }
    RadioChoice(copy(R.string.dp_uncertain), state["activity"] == "uncertain", Modifier.fillMaxWidth()) { state.set("activity", "uncertain") }
    Gap(); Label(copy(if (state["activity"] == "uncertain") R.string.dp_uncertain_hint else R.string.dp_activity_hint), muted = true, center = true, modifier = Modifier.fillMaxWidth())
    Footer(state, enabled = state["activity"].isNotBlank()) { state.next(PreviewPage.Health1) }
}
