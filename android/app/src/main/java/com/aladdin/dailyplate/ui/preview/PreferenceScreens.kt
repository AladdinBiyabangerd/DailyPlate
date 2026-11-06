package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aladdin.dailyplate.R

internal val healthQuestions = listOf(R.string.dp_pregnancy, R.string.dp_breastfeeding, R.string.dp_kidney,
    R.string.dp_diabetes, R.string.dp_diet, R.string.dp_eating, R.string.dp_medication, R.string.dp_loss,
    R.string.dp_sport, R.string.dp_composition)
@Composable internal fun Health(state: PreviewState, second: Boolean) = Frame(state, R.string.dp_health) {
    Gap(24.dp)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Art(R.drawable.dp_art_health, 64.dp)
        Column(Modifier.weight(1f)) { Label(copy(R.string.dp_health_step, if (second) 2 else 1), bold = true); Gap(8.dp); Label(copy(R.string.dp_health_intro), muted = true) }
    }
    Gap(24.dp)
    val indices = if (second) 5..9 else 0..4
    indices.forEach { index ->
        val key = PreviewState.healthKeys[index]
        Panel {
            Label(copy(healthQuestions[index]), size = 18, bold = true); Gap()
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                RadioChoice(copy(R.string.dp_yes), state[key] == "yes", Modifier.weight(1f)) { state.set(key, "yes") }
                RadioChoice(copy(R.string.dp_no), state[key] == "no", Modifier.weight(1f)) { state.set(key, "no") }
            }
        }
        Gap()
    }
    Label(copy(R.string.dp_health_note), muted = true)
    Footer(state, secondary = R.string.dp_skip_answers, secondaryAction = { state.dialog = PreviewDialog.HealthSkip }) {
        if (indices.any { state[PreviewState.healthKeys[it]].isBlank() }) state.error = R.string.dp_health_note
        else {
            if (PreviewState.healthKeys.all { state[it] in listOf("yes", "no") }) state.set("healthIncomplete", "")
            if ((second || state.editing) && PreviewState.healthKeys.any { state[it] == "yes" }) state.go(PreviewPage.Warning)
            else state.next(if (second) PreviewPage.Allergy else PreviewPage.Health2)
        }
    }
}
@Composable internal fun Warning(state: PreviewState) = Frame(state, R.string.dp_support) {
    Gap(24.dp); Art(R.drawable.dp_art_health, 160.dp, modifier = Modifier.align(Alignment.CenterHorizontally)); Gap(24.dp)
    Label(copy(R.string.dp_warning_title), size = 26, bold = true, center = true, modifier = Modifier.fillMaxWidth()); Gap()
    Surface(shape = PreviewStyle.radius, color = androidx.compose.ui.res.colorResource(R.color.simple_peach)) {
        Label(copy(R.string.dp_warning_copy), size = 18, modifier = Modifier.padding(24.dp))
    }
    Gap(24.dp); Label(copy(R.string.dp_warning_hint), muted = true, center = true, modifier = Modifier.fillMaxWidth())
    Footer(state, R.string.dp_diary_continue, arrow = false, secondary = R.string.dp_review_answers,
        secondaryAction = { state.go(PreviewPage.Health1) }) { state.next(PreviewPage.Allergy) }
}
internal data class Allergen(val key: String, val label: Int, val art: Int?)
internal val allergens = listOf(Allergen("milk", R.string.dp_milk, R.drawable.dp_art_milk), Allergen("egg", R.string.dp_egg, R.drawable.dp_art_egg),
    Allergen("nuts", R.string.dp_nuts, R.drawable.dp_art_nuts), Allergen("fish", R.string.dp_fish, R.drawable.dp_art_fish),
    Allergen("wheat", R.string.dp_wheat, R.drawable.dp_art_wheat), Allergen("other", R.string.dp_other, null))
@Composable internal fun Allergy(state: PreviewState) = Frame(state, R.string.dp_allergy) {
    Gap(); Label(copy(R.string.dp_allergy_intro), center = true, modifier = Modifier.fillMaxWidth()); Gap(24.dp)
    Label(copy(R.string.dp_choose_one), size = 18, bold = true); Gap()
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        RadioChoice(copy(R.string.dp_yes), state["allergy"] == "yes", Modifier.weight(1f)) { state.allergy("yes") }
        RadioChoice(copy(R.string.dp_no), state["allergy"] == "no", Modifier.weight(1f)) { state.allergy("no") }
    }
    Gap(24.dp)
    if (state["allergy"] == "yes") {
        Label(copy(R.string.dp_allergens), size = 24, bold = true); Gap(8.dp); Label(copy(R.string.dp_allergen_hint), muted = true); Gap()
        allergens.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                pair.forEach { item ->
                    val selected = state.selected("allergens", item.key)
                    Surface(Modifier.weight(1f).toggleable(selected, role = Role.Checkbox, onValueChange = {
                        state.toggle("allergens", item.key)
                        if (!state.selected("allergens", item.key) && item.key in listOf("nuts", "other")) state.set(item.key, "")
                    }), shape = PreviewStyle.radius, shadowElevation = 2.dp,
                        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null) {
                        Box(Modifier.fillMaxWidth().padding(16.dp)) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                if (item.art != null) Art(item.art, 80.dp) else Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) { Text("＋", color = MaterialTheme.colorScheme.primary, fontSize = 48.sp) }
                                Gap(8.dp); Label(copy(item.label), size = 18, bold = true, center = true)
                            }
                            Checkbox(selected, null, Modifier.size(24.dp).align(Alignment.TopEnd))
                        }
                    }
                }
            }; Gap()
        }
        if (state.selected("allergens", "nuts")) { DetailInput(state, "nuts", R.string.dp_nuts_detail, R.string.dp_nuts_placeholder); Gap() }
        if (state.selected("allergens", "other")) { DetailInput(state, "other", R.string.dp_other_detail, R.string.dp_other_placeholder); Gap() }
    }
    Panel {
        Row(Modifier.fillMaxWidth().toggleable(state["gluten"] == "yes", role = Role.Checkbox, onValueChange = { state.set("gluten", if (it) "yes" else "no") }).heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(state["gluten"] == "yes", null); Spacer(Modifier.width(8.dp)); Label(copy(R.string.dp_gluten), bold = true, modifier = Modifier.weight(1f))
        }; Gap(8.dp); Label(copy(R.string.dp_gluten_hint), muted = true)
    }
    Gap(24.dp); Notice(R.string.dp_allergy_note, yellow = true)
    Footer(state, secondary = R.string.dp_not_answering, secondaryAction = { state.dialog = PreviewDialog.AllergyMissing }) {
        when {
            state["allergy"].isBlank() -> state.dialog = PreviewDialog.AllergyMissing
            state["allergy"] == "yes" && state["allergens"].isBlank() -> state.error = R.string.dp_allergy_error
            state.selected("allergens", "nuts") && state["nuts"].isBlank() -> state.error = R.string.dp_nuts_detail
            state.selected("allergens", "other") && state["other"].isBlank() -> state.error = R.string.dp_other_detail
            else -> { state.set("allergyUnknown", ""); state.next(PreviewPage.Preferences) }
        }
    }
}
@Composable internal fun DetailInput(state: PreviewState, key: String, label: Int, placeholder: Int) {
    Label(copy(label), bold = true); Gap(8.dp)
    OutlinedTextField(state[key], { state.set(key, it) }, label = { Text(copy(label)) }, placeholder = { Text(copy(placeholder)) },
        modifier = Modifier.fillMaxWidth(), shape = PreviewStyle.button, singleLine = true)
}
@Composable internal fun Preferences(state: PreviewState) = Frame(state, R.string.dp_preferences) {
    Gap(24.dp); Label(copy(R.string.dp_preferences_intro), center = true, modifier = Modifier.fillMaxWidth()); Gap(24.dp)
    PreferenceGroup(state, "time", R.string.dp_time, R.drawable.dp_art_cooking, R.string.dp_time_hint,
        listOf("15" to R.string.dp_time15, "30" to R.string.dp_time30, "60" to R.string.dp_time60))
    Gap()
    PreferenceGroup(state, "budget", R.string.dp_budget, R.drawable.dp_art_budget, R.string.dp_budget_hint,
        listOf("economical" to R.string.dp_economical, "medium" to R.string.dp_medium, "flexible" to R.string.dp_flexible))
    Gap()
    PreferenceGroup(state, "meals", R.string.dp_meals, R.drawable.dp_art_menu, R.string.dp_meals_hint,
        listOf("3" to R.string.dp_meals3, "4" to R.string.dp_meals4, "5" to R.string.dp_meals5))
    Footer(state) {
        if (PreviewPage.Preferences in state.missing()) state.error = R.string.dp_preferences_error
        else state.next(PreviewPage.Review)
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable private fun PreferenceGroup(state: PreviewState, key: String, title: Int, art: Int, hint: Int, choices: List<Pair<String, Int>>) {
    Panel {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Art(art, 64.dp); Label(copy(title), size = 22, bold = true, modifier = Modifier.weight(1f))
        }; Gap()
        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            choices.forEach { (value, label) ->
                val selected = state[key] == value
                Surface(onClick = { state.set(key, value) }, shape = PreviewStyle.button,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                    modifier = Modifier.weight(1f).widthIn(min = 72.dp).heightIn(min = 48.dp)) {
                    Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                        Text((if (selected) "✓ " else "") + copy(label), fontWeight = FontWeight.Bold, fontSize = 16.sp,
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                    }
                }
            }
        }
        Gap(); Label(copy(hint), muted = true)
        if (key == "meals" && state[key].isNotBlank()) { Gap(); Label(slotLabels(state).joinToString(" · ")) }
    }
}
@Composable internal fun slotLabels(state: PreviewState): List<String> = when (state["meals"]) {
    "4" -> listOf(R.string.dp_breakfast, R.string.dp_lunch, R.string.dp_snack, R.string.dp_dinner)
    "5" -> listOf(R.string.dp_breakfast, R.string.dp_snack1, R.string.dp_lunch, R.string.dp_snack2, R.string.dp_dinner)
    else -> listOf(R.string.dp_breakfast, R.string.dp_lunch, R.string.dp_dinner)
}.map { copy(it) }
