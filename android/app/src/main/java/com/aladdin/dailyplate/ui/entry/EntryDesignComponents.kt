package com.aladdin.dailyplate.ui.entry

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.Food
import com.aladdin.dailyplate.ui.*
import com.aladdin.dailyplate.ui.theme.*

/** Approved reference-board artwork, with native text and one accessible click target. */
@Composable
internal fun EntryLanding(open: (String) -> Unit) {
    QidaHeading(stringResource(R.string.fe_entry_title))
    Text(stringResource(R.string.fe_entry_hint), color = MaterialTheme.colorScheme.onSurfaceVariant)
    EntryAction(stringResource(R.string.fe_camera), stringResource(R.string.fe_camera_hint),
        R.drawable.art_entry_camera) { open("camera") }
    EntryAction(stringResource(R.string.fe_search), stringResource(R.string.fe_search_hint),
        R.drawable.art_entry_search) { open("search") }
    EntryAction(stringResource(R.string.fe_previous), stringResource(R.string.fe_previous_hint),
        R.drawable.art_entry_previous) { open("previous") }
}

@Composable
internal fun EntryBack(back: () -> Unit) {
    TextButton(onClick = back, modifier = Modifier.heightIn(min = QidaDimensions.touchTarget)) {
        Text("‹  " + stringResource(R.string.back))
    }
}

@Composable
internal fun EntryAction(title: String, hint: String, @DrawableRes art: Int, action: () -> Unit) {
    Card(onClick = action, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = QidaDimensions.cardElevation)) {
        Row(Modifier.fillMaxWidth().padding(QidaSpacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
            QidaArt(art, Modifier.size(QidaDimensions.choiceArt))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(hint, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            EntryArrow()
        }
    }
}

@Composable
private fun EntryArrow() {
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
        Box(Modifier.size(QidaDimensions.fieldArt), contentAlignment = Alignment.Center) {
            Text("›", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

/** Food records have no verified photo field: use an explicitly labelled illustration. */
@Composable
internal fun FoodResultCard(food: Food, name: String, select: () -> Unit) {
    Card(onClick = select, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget)
        .semantics { contentDescription = name },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = QidaDimensions.cardElevation)) {
        Row(Modifier.fillMaxWidth().padding(QidaSpacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                QidaArt(R.drawable.art_entry_previous, Modifier.size(QidaDimensions.choiceArt))
                Text(stringResource(R.string.fe_illustration), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                val energy = food.nutrientsPer100g.values["energy"]?.value
                Text(if (energy == null) stringResource(R.string.fe_unknown_value)
                    else stringResource(R.string.fe_result_energy, energy),
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(food.preparationState, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${food.sourceType} / ${food.sourceId} / ${food.sourceVersion}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(stringResource(R.string.fe_choose), color = MaterialTheme.colorScheme.primary)
            }
            EntryArrow()
        }
    }
}
