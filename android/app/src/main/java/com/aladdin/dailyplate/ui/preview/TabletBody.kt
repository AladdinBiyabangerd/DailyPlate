package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable internal fun TabletBody(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    TabletFrame(state, R.string.dp_body, if (wide) 1040.dp else 704.dp, top = if (wide) 32.dp else 48.dp) {
        Label(copy(R.string.dp_body_intro)); Gap(32.dp)
        TabletSplit(left = {
            val metrics = listOf(Triple("age", R.string.dp_age, R.drawable.dp_art_age), Triple("height", R.string.dp_height, R.drawable.dp_art_height), Triple("weight", R.string.dp_weight, R.drawable.dp_art_weight))
            if (wide) Column(verticalArrangement = Arrangement.spacedBy(16.dp)) { metrics.forEach { TabletMetric(state, it.first, it.second, it.third, true) } }
            else BoxWithConstraints(Modifier.fillMaxWidth()) {
                if (maxWidth >= 656.dp) TabletGrid(metrics, 3, 16.dp) { TabletMetric(state, it.first, it.second, it.third, false) }
                else Column(verticalArrangement = Arrangement.spacedBy(16.dp)) { metrics.forEach { TabletMetric(state, it.first, it.second, it.third, true) } }
            }
        }, right = { TabletSex(state) }, topAligned = true)
        Gap(if (wide) 16.dp else 24.dp)
        if (state.error != 0) { Notice(state.error); Gap(16.dp) }
        Surface(shape = PreviewStyle.button, color = androidx.compose.ui.res.colorResource(R.color.simple_yellow)) {
            Label(copy(R.string.dp_body_privacy), Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = if (wide) 8.dp else 16.dp), center = true)
        }
        TabletFooter(state, showError = false, topGap = if (wide) 16.dp else 24.dp) {
            if (listOf("age", "height", "weight").all(state::validNumber)) state.next(PreviewPage.Activity) else state.error = R.string.dp_body_error
        }
    }
}
@Composable private fun TabletMetric(state: PreviewState, key: String, title: Int, art: Int, horizontal: Boolean) {
    val name = copy(title)
    val unit = when (key) { "age" -> R.string.dp_years; "height" -> R.string.dp_cm; else -> R.string.dp_kg }
    Panel {
        if (horizontal) Row(Modifier.heightIn(min = 72.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TabletArt(art, 64.dp)
            Column(Modifier.weight(1f)) { Label(name, size = 18, bold = true); Gap(8.dp); Label(copy(unit), muted = true) }
            TabletStepper(state, key, name)
        } else Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            TabletArt(art, 80.dp); Gap(16.dp); Label(name, size = 20, bold = true); Gap(16.dp)
            TabletStepper(state, key, name); Gap(16.dp); Label(copy(unit), muted = true)
        }
    }
}
@Composable private fun TabletStepper(state: PreviewState, key: String, name: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StepButton("−", copy(R.string.dp_decrease, name)) { adjust(state, key, -1) }
        BasicTextField(state[key], { state.set(key, it) }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = if (key == "age") KeyboardType.Number else KeyboardType.Decimal),
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface),
            modifier = Modifier.width(64.dp).heightIn(min = 48.dp).semantics { contentDescription = name },
            decorationBox = { inner -> Box(Modifier.defaultMinSize(minHeight = 48.dp), contentAlignment = Alignment.Center) {
                if (state[key].isBlank()) Label("—", muted = true); inner()
            } })
        StepButton("+", copy(R.string.dp_increase, name)) { adjust(state, key, 1) }
    }
}
@Composable private fun TabletSex(state: PreviewState) {
    Column {
        Label(copy(R.string.dp_sex), size = 22, bold = true); Gap(8.dp); Label(copy(R.string.dp_sex_note), muted = true); Gap(if (LocalTabletLayout.current!!.wide) 16.dp else 24.dp)
        TabletGrid(listOf(Triple("male", R.string.dp_male, R.drawable.dp_art_male), Triple("female", R.string.dp_female, R.drawable.dp_art_female)), 2) { (key, title, art) ->
            Surface(Modifier.fillMaxWidth().selectable(state["sex"] == key, role = Role.RadioButton, onClick = { state.set("sex", key) }),
                shape = PreviewStyle.radius, shadowElevation = 8.dp, border = if (state["sex"] == key) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null) {
                Column(Modifier.padding(if (LocalTabletLayout.current!!.wide) 16.dp else 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    TabletArt(art, if (LocalTabletLayout.current!!.wide) 80.dp else 96.dp); Gap(16.dp)
                    Label((if (state["sex"] == key) "✓ " else "") + copy(title), size = 20, bold = true, center = true)
                }
            }
        }
        Gap(16.dp); TabletRadio(copy(R.string.dp_sex_skip), state["sex"] == "unspecified", Modifier.fillMaxWidth(), bare = true) { state.set("sex", "unspecified") }
        Gap(8.dp); Label(copy(R.string.dp_no_target), muted = true)
    }
}
