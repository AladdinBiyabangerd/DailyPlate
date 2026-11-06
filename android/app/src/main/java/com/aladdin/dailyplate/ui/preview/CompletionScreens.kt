package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aladdin.dailyplate.R

internal val reviewGroups = listOf(PreviewPage.Goal to R.string.dp_goal_group, PreviewPage.Body to R.string.dp_body_group,
    PreviewPage.Activity to R.string.dp_activity_group, PreviewPage.Health1 to R.string.dp_health1,
    PreviewPage.Health2 to R.string.dp_health2, PreviewPage.Allergy to R.string.dp_allergy_group,
    PreviewPage.Preferences to R.string.dp_preferences_group)
@Composable internal fun Review(state: PreviewState) = Frame(state, R.string.dp_review) {
    val missing = state.missing()
    Gap(24.dp)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Art(R.drawable.dp_art_write_error, 80.dp)
        Label(copy(if (missing.isEmpty()) R.string.dp_complete_title else R.string.dp_missing_title), size = 26, bold = true, modifier = Modifier.weight(1f))
    }
    Gap(); Label(copy(if (missing.isEmpty()) R.string.dp_complete_summary else R.string.dp_missing_summary), muted = true); Gap(24.dp)
    reviewGroups.filter { state.reviewAll || it.first in missing }.forEach { (page, title) ->
        Panel(onClick = { state.edit(page) }) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(1f)) {
                    Label(copy(title), size = 20, bold = true); Gap(8.dp)
                    Label(if (page in missing && !state.reviewAll) missingSummary(state, page) else groupSummary(state, page), muted = true)
                }; Label("›", size = 26, bold = true)
            }
        }; Gap()
    }
    Notice(if (missing.isEmpty()) R.string.dp_complete_note else R.string.dp_missing_note)
    Footer(state, if (missing.isEmpty()) R.string.dp_next else R.string.dp_complete_answers,
        secondary = R.string.dp_diary_continue, secondaryAction = { state.forcedDiary = true; state.go(PreviewPage.Account) }) {
        if (missing.isEmpty()) { state.forcedDiary = false; state.go(PreviewPage.Account) } else state.edit(missing.first())
    }
    Secondary(R.string.dp_recheck) { state.reviewAll = false; state.go(PreviewPage.Review) }
}
@Composable internal fun missingSummary(state: PreviewState, page: PreviewPage): String {
    val context = androidx.compose.ui.platform.LocalContext.current
    return when (page) {
        PreviewPage.Goal -> copy(R.string.dp_missing_goal)
        PreviewPage.Body -> copy(R.string.dp_missing_body, listOf("age" to R.string.dp_age, "height" to R.string.dp_height, "weight" to R.string.dp_weight).filter { !state.validNumber(it.first) }.joinToString(", ") { context.getString(it.second) })
        PreviewPage.Activity -> copy(R.string.dp_missing_activity)
        PreviewPage.Health1, PreviewPage.Health2 -> (if (page == PreviewPage.Health1) PreviewState.healthKeys.take(5) else PreviewState.healthKeys.drop(5)).count { state[it].isBlank() }.let { count -> androidx.compose.ui.res.pluralStringResource(R.plurals.dp_missing_health, count, count) }
        PreviewPage.Allergy -> copy(R.string.dp_missing_allergy)
        else -> copy(R.string.dp_missing_prefs)
    }
}
@Composable internal fun groupSummary(state: PreviewState, page: PreviewPage): String {
    val context = androidx.compose.ui.platform.LocalContext.current
    val empty = context.getString(R.string.dp_not_selected)
    return when (page) {
        PreviewPage.Goal -> goals.find { it.key == state["goal"] }?.let { context.getString(it.title) } ?: empty
        PreviewPage.Body -> listOf("age" to R.string.dp_age, "height" to R.string.dp_height, "weight" to R.string.dp_weight).joinToString(" · ") { (key, label) -> context.getString(label) + ": " + state[key].ifBlank { empty } }
        PreviewPage.Activity -> activities.find { it.key == state["activity"] }?.let { context.getString(it.title) } ?: if (state["activity"] == "uncertain") context.getString(R.string.dp_uncertain) else empty
        PreviewPage.Health1, PreviewPage.Health2 -> {
            val range = if (page == PreviewPage.Health1) 0..4 else 5..9
            range.joinToString("\n") { i -> context.getString(healthQuestions[i]) + " " + when (state[PreviewState.healthKeys[i]]) { "yes" -> context.getString(R.string.dp_yes); "no" -> context.getString(R.string.dp_no); else -> empty } }
        }
        PreviewPage.Allergy -> when(state["allergy"]) {
            "no" -> context.getString(R.string.dp_no)
            "yes" -> allergens.filter { state.selected("allergens", it.key) }.joinToString(", ") { context.getString(it.label) }.ifBlank { empty }
            else -> empty
        }
        PreviewPage.Preferences -> listOf(state["time"].ifBlank { empty }, when (state["budget"]) {
            "economical" -> context.getString(R.string.dp_economical); "medium" -> context.getString(R.string.dp_medium); "flexible" -> context.getString(R.string.dp_flexible); else -> empty
        }, state["meals"].ifBlank { empty }).joinToString(" · ")
        else -> empty
    }
}
@Composable internal fun Account(state: PreviewState) = Frame(state, R.string.dp_account) {
    Gap(24.dp); Art(R.drawable.dp_art_account, 192.dp, 160.dp, Modifier.align(Alignment.CenterHorizontally)); Gap(24.dp)
    Label(copy(R.string.dp_account_title), size = 26, bold = true, center = true, modifier = Modifier.fillMaxWidth()); Gap()
    Label(copy(R.string.dp_account_intro), muted = true, center = true, modifier = Modifier.fillMaxWidth()); Gap(24.dp)
    Panel(onClick = { state.dialog = PreviewDialog.Provider }) {
        Row(Modifier.heightIn(min = 32.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Label("G", size = 24, bold = true); Label(copy(R.string.dp_google), size = 18, bold = true, modifier = Modifier.weight(1f))
        }
    }
    Gap(24.dp)
    Surface(shape = PreviewStyle.button, color = androidx.compose.ui.res.colorResource(R.color.simple_yellow)) {
        Column(Modifier.padding(16.dp)) { Label(copy(R.string.dp_guest_title), bold = true); Gap(8.dp); Label(copy(R.string.dp_guest_note)) }
    }
    Secondary(R.string.dp_privacy) { state.dialog = PreviewDialog.Privacy }
    Footer(state, R.string.dp_not_now) { state.go(PreviewPage.Ready) }
    Gap(); Label(copy(R.string.dp_account_later), muted = true, center = true, modifier = Modifier.fillMaxWidth())
}
@OptIn(ExperimentalLayoutApi::class)
@Composable internal fun Ready(state: PreviewState) = Frame(state, if (state.diary) R.string.dp_diary_ready else R.string.dp_ready) {
    Gap(); Art(R.drawable.dp_art_recipe, 224.dp, 208.dp, Modifier.align(Alignment.CenterHorizontally)); Gap()
    Label(copy(R.string.dp_ready_title), size = 26, bold = true, center = true, modifier = Modifier.fillMaxWidth()); Gap()
    val goal = goals.find { it.key == state["goal"] }?.let { copy(it.title) } ?: copy(R.string.dp_not_selected)
    Panel {
        listOf("🎯" to copy(R.string.dp_goal_summary, goal), "🍽" to if (state["meals"].isBlank()) copy(R.string.dp_not_selected) else copy(R.string.dp_meal_summary, state["meals"]), "ⓘ" to copy(R.string.dp_no_calorie)).forEach { (icon, title) ->
            Row(Modifier.heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Label(icon, size = 24, modifier = Modifier.width(32.dp)); Label(title, bold = true, modifier = Modifier.weight(1f))
            }
        }
    }
    Gap(); Notice(if (state.diary) R.string.dp_diary_notice else R.string.dp_demo_notice); Gap()
    if (state.diary) Notice(R.string.dp_diary_info, yellow = true) else {
        Label(copy(R.string.dp_sample_menu), size = 20, bold = true); Gap()
        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp), maxItemsInEachRow = 3) {
            slotLabels(state).forEachIndexed { i, label ->
                Column(Modifier.weight(1f).widthIn(min = 88.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Art(listOf(R.drawable.dp_art_egg, R.drawable.dp_art_healthy, R.drawable.dp_art_recipe)[i % 3], 80.dp)
                    Label(label, bold = true, center = true)
                }
            }
        }
        Gap(); Label(copy(R.string.dp_sample_caption), muted = true)
    }
    Footer(state, R.string.dp_start, secondary = R.string.dp_change_choices,
        secondaryAction = { state.reviewAll = true; state.go(PreviewPage.Review) }) { state.go(PreviewPage.Today) }
}
