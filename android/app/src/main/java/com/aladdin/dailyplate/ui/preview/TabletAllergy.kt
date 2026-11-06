package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.aladdin.dailyplate.R

@Composable internal fun TabletAllergy(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    val yes = state["allergy"] == "yes"
    TabletFrame(state, R.string.dp_allergy, if (wide && yes) 1040.dp else if (wide) 640.dp else 704.dp, top = if (wide) 16.dp else 24.dp) {
        if (wide && yes) Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            Column(Modifier.width(320.dp)) { TabletAllergyAnswers(state); Gap(16.dp); TabletGluten(state); TabletAllergyActions(state) }
            Column(Modifier.weight(1f)) { TabletAllergens(state) }
        } else {
            TabletAllergyAnswers(state); Gap(16.dp)
            if (yes) { TabletAllergens(state); Gap(16.dp) }
            TabletGluten(state); TabletAllergyActions(state)
        }
    }
}
@Composable private fun TabletAllergyAnswers(state: PreviewState) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.widthIn(max = 416.dp).fillMaxWidth()) {
            Label(copy(R.string.dp_choose_one), size = 18, bold = true); Gap(16.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TabletRadio(copy(R.string.dp_yes), state["allergy"] == "yes", Modifier.weight(1f), answerCard = true) { state.allergy("yes") }
                TabletRadio(copy(R.string.dp_no), state["allergy"] == "no", Modifier.weight(1f), answerCard = true) { state.allergy("no") }
            }
        }
    }
}
@Composable private fun TabletAllergens(state: PreviewState) {
    Label(copy(R.string.dp_allergens), size = 24, bold = true); Gap(8.dp); Label(copy(R.string.dp_allergen_hint), muted = true); Gap(16.dp)
    TabletGrid(allergens, 3, 16.dp) { item ->
        val selected = state.selected("allergens", item.key)
        Surface(Modifier.fillMaxWidth().fillMaxHeight().toggleable(selected, role = Role.Checkbox, onValueChange = {
            state.toggle("allergens", item.key)
            if (!state.selected("allergens", item.key) && item.key in listOf("nuts", "other")) state.set(item.key, "")
        }), shape = PreviewStyle.radius, shadowElevation = 8.dp, border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null) {
            Box(Modifier.padding(16.dp)) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (item.art != null) TabletArt(item.art, 80.dp, 72.dp) else Label("＋", size = 48, center = true, modifier = Modifier.heightIn(min = 72.dp))
                    Gap(8.dp); Label(copy(item.label), size = 18, bold = true, center = true)
                }
                Checkbox(selected, null, Modifier.size(24.dp).align(Alignment.TopEnd))
            }
        }
    }
    if (state.selected("allergens", "nuts")) { Gap(16.dp); DetailInput(state, "nuts", R.string.dp_nuts_detail, R.string.dp_nuts_placeholder) }
    if (state.selected("allergens", "other")) { Gap(16.dp); DetailInput(state, "other", R.string.dp_other_detail, R.string.dp_other_placeholder) }
}
@Composable private fun TabletGluten(state: PreviewState) = Panel {
    Row(Modifier.fillMaxWidth().toggleable(state["gluten"] == "yes", role = Role.Checkbox, onValueChange = { state.set("gluten", if (it) "yes" else "no") }).heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(state["gluten"] == "yes", null); Spacer(Modifier.width(8.dp)); Label(copy(R.string.dp_gluten), bold = true, modifier = Modifier.weight(1f))
    }; Label(copy(R.string.dp_gluten_hint), muted = true)
}
@Composable private fun TabletAllergyActions(state: PreviewState) {
    Gap(16.dp); Notice(R.string.dp_allergy_note, yellow = true)
    TabletFooter(state, secondary = R.string.dp_not_answering, secondaryAction = { state.dialog = PreviewDialog.AllergyMissing }) {
        when {
            state["allergy"].isBlank() -> state.dialog = PreviewDialog.AllergyMissing
            state["allergy"] == "yes" && state["allergens"].isBlank() -> state.error = R.string.dp_allergy_error
            state.selected("allergens", "nuts") && state["nuts"].isBlank() -> state.error = R.string.dp_nuts_detail
            state.selected("allergens", "other") && state["other"].isBlank() -> state.error = R.string.dp_other_detail
            else -> { state.set("allergyUnknown", ""); state.next(PreviewPage.Preferences) }
        }
    }
}
