package com.aladdin.dailyplate.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.ui.theme.*

/** Raster artwork is decorative; labels and selection remain native and accessible. */
@Composable
fun QidaArt(@DrawableRes art: Int, modifier: Modifier = Modifier) {
    Image(painterResource(art), contentDescription = null, modifier = modifier,
        contentScale = ContentScale.Fit)
}

@Composable
fun DesignChoice(label: String, selected: Boolean, action: () -> Unit,
                 modifier: Modifier = Modifier, art: Int? = null, tile: Boolean = false) {
    val outline = if (selected) BorderStroke(QidaDimensions.selectionStroke, MaterialTheme.colorScheme.primary) else null
    Card(onClick = action, modifier = modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget)
        .semantics { this.selected = selected; role = Role.Checkbox },
        shape = MaterialTheme.shapes.large, border = outline,
        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = QidaDimensions.cardElevation)) {
        if (tile) {
            Column(Modifier.fillMaxWidth().padding(QidaSpacing.medium), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                if (art != null) QidaArt(art, Modifier.size(QidaDimensions.choiceArt))
                Text((if (selected) "✓ " else "") + label, style = MaterialTheme.typography.titleMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else Row(Modifier.fillMaxWidth().padding(QidaSpacing.medium), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
            if (art != null) QidaArt(art, Modifier.size(QidaDimensions.choiceArt))
            Text((if (selected) "✓ " else "") + label, Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun DesignGrid(items: List<Pair<String, @Composable (Modifier) -> Unit>>, columnsAtNormal: Int = 2) {
    // Large system text uses a single column, keeping complete labels and 48dp targets.
    val columns = if (LocalConfiguration.current.fontScale > 1.3f) 1 else columnsAtNormal
    items.chunked(columns).forEach { row ->
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
            row.forEach { (_, content) -> Box(Modifier.weight(1f).fillMaxHeight()) { content(Modifier.fillMaxHeight()) } }
            if (row.size < columns) Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun StepHeader(step: Int, back: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
        TextButton(onClick = back, modifier = Modifier.heightIn(min = QidaDimensions.touchTarget),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Text("‹"); Text(stringResource(R.string.back))
        }
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
            repeat(5) { index ->
                Surface(Modifier.weight(1f).height(QidaSpacing.small),
                    shape = MaterialTheme.shapes.large,
                    color = if (index <= step) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface) {}
            }
        }
        Text("${step + 1}/5", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ChoiceGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    QidaCard {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@Composable
fun SegmentedChoices(values: List<Pair<String, String>>, current: String, choose: (String) -> Unit) {
    if (LocalConfiguration.current.fontScale > 1.3f) {
        values.forEach { (value, label) -> DesignChoice(label, current == value, { choose(value) }) }
    } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
        values.forEach { (value, label) ->
            val selected = current == value
            FilledTonalButton(onClick = { choose(value) }, modifier = Modifier.weight(1f).heightIn(min = QidaDimensions.touchTarget)
                .semantics { this.selected = selected }, shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(QidaSpacing.small),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                    contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground)) {
                Text((if (selected) "✓ " else "") + label, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MeasurementCard(label: String, value: String, art: Int, error: String?,
                    change: (String) -> Unit, step: (Int) -> Unit) {
    QidaCard {
        if (LocalConfiguration.current.fontScale > 1.3f) {
            QidaField(label, value, change, error, numeric = true)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                QidaArt(art, Modifier.size(QidaDimensions.fieldArt))
                listOf(-1, 1).forEach { delta ->
                    FilledTonalButton(onClick = { step(delta) }, modifier = Modifier.weight(1f)
                        .heightIn(min = QidaDimensions.touchTarget).semantics { contentDescription = label + if (delta > 0) " +1" else " −1" }) {
                        Text(if (delta > 0) "+" else "−")
                    }
                }
            }
        } else Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
            QidaArt(art, Modifier.size(QidaDimensions.fieldArt))
            TextButton(onClick = { step(-1) }, modifier = Modifier.size(QidaDimensions.touchTarget)
                .semantics { contentDescription = "$label −1" }) { Text("−") }
            Box(Modifier.weight(1f)) { QidaField(label, value, change, error, numeric = true) }
            TextButton(onClick = { step(1) }, modifier = Modifier.size(QidaDimensions.touchTarget)
                .semantics { contentDescription = "$label +1" }) { Text("+") }
        }
    }
}
