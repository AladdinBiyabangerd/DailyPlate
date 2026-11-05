package com.aladdin.dailyplate.ui.entry

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.entry.*
import com.aladdin.dailyplate.ui.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PortionDraftEditor(draft: PortionDraft, back: () -> Unit, update: (PortionDraft) -> Unit) {
    var amount by rememberSaveable(draft.name) { mutableStateOf(draft.quantity.toString()) }
    var invalid by rememberSaveable { mutableStateOf(false) }
    var ready by rememberSaveable { mutableStateOf(false) }
    QidaHeading(stringResource(R.string.fe_portion))
    QidaCard {
        Text(draft.name, style = MaterialTheme.typography.titleMedium)
        draft.preparationState?.let { Text(it) }
        draft.source?.let { Text(it) }
        Text(Instant.parse(draft.consumedAt.instant).atZone(ZoneId.of(draft.consumedAt.timezone))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z")))
    }
    QidaField(stringResource(R.string.fe_amount), amount, { amount = it; ready = false },
        if (invalid) stringResource(R.string.fe_invalid) else null, numeric = true)
    Text(stringResource(when (draft.unit) {
        EntryUnit.GRAM -> R.string.fe_grams
        EntryUnit.KILOGRAM -> R.string.fe_kilograms
        EntryUnit.PREVIOUS_PORTION -> R.string.fe_previous_portion
    }))
    QidaButton(stringResource(R.string.fe_prepare), {
        val value = parseEntryNumber(amount)
        val next = value?.takeIf { it > 0 }?.let { runCatching { draft.copy(quantity = it).also { p -> p.nutrients } }.getOrNull() }
        invalid = next == null
        if (next != null) { update(next); ready = true }
    })
    if (ready) QidaCard {
        Text(stringResource(R.string.fe_ready))
        Text(stringResource(R.string.fe_unknown))
        draft.nutrients.values.forEach { (key, nutrient) ->
            Text("$key: ${nutrient.value?.toString() ?: stringResource(R.string.fe_unknown_value)} ${nutrient.unit}")
        }
    }
    QidaButton(stringResource(R.string.back), back)
}
