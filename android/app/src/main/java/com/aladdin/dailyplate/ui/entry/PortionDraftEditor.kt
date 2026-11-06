package com.aladdin.dailyplate.ui.entry

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.entry.*
import com.aladdin.dailyplate.ui.*
import androidx.compose.ui.platform.LocalContext
import com.aladdin.dailyplate.data.local.UserRepositories
import com.aladdin.dailyplate.domain.FoodLog
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.util.UUID
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PortionDraftEditor(draft: PortionDraft, back: () -> Unit, update: (PortionDraft) -> Unit,
    record: (suspend (FoodLog) -> FoodLog)? = null,
    openSaved: (String) -> Unit = {},
    openToday: () -> Unit = {},
) {
    var amount by rememberSaveable(draft.name) { mutableStateOf(draft.quantity.toString()) }
    var invalid by rememberSaveable { mutableStateOf(false) }
    var ready by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pending by rememberSaveable { mutableStateOf<String?>(null) }
    var saved by rememberSaveable { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    if (saved != null) {
        val log = Json.decodeFromString(FoodLog.serializer(), requireNotNull(saved))
        QidaHeading(stringResource(R.string.pc_saved))
        QidaCard {
            Text(requireNotNull(log.displayName))
            Text("${log.enteredQuantity} ${portionUnitLabel(log.enteredUnit)}")
            Text(stringResource(R.string.pc_estimate))
            PortionNutrients(log.nutrientSnapshot)
            com.aladdin.dailyplate.ui.today.RecordedIngredients(log)
            Text(stringResource(R.string.pc_local))
        }
        com.aladdin.dailyplate.ui.today.SavedDaySummary(log)
        QidaButton(stringResource(R.string.today), openToday)
        QidaButton(stringResource(R.string.td_edit), { openSaved(log.ref.id) })
        QidaButton(stringResource(R.string.pc_another), back)
        return
    }
    if (busy) { QidaState(SurfaceState.Loading); return }
    if (pending != null) {
        Text(stringResource(R.string.pc_error))
        QidaButton(stringResource(R.string.pc_retry), {
            if (!busy) {
                busy = true
                scope.launch {
                    try {
                        val log = Json.decodeFromString(FoodLog.serializer(), requireNotNull(pending))
                        val result = withContext(Dispatchers.IO) { record?.invoke(log) ?: UserRepositories.open(context).use { it.foodLogs.record(log) } }
                        saved = Json.encodeToString(FoodLog.serializer(), result)
                    } catch (cancelled: CancellationException) { throw cancelled }
                    catch (_: Exception) { /* Retain the exact event for retry; no success claim. */ }
                    finally { busy = false }
                }
            }
        })
        return
    }
    EntryBack(back)
    PortionHero(if (draft.unit == EntryUnit.PREVIOUS_PORTION) R.string.fe_portion else R.string.pd_quantity,
        if (draft.unit == EntryUnit.PREVIOUS_PORTION) R.drawable.art_entry_previous else R.drawable.art_portion_scale)
    QidaCard {
        Text(draft.name, style = MaterialTheme.typography.titleMedium)
        draft.preparationState?.let { Text(it) }
        draft.source?.takeUnless { draft.unit == EntryUnit.PREVIOUS_PORTION }
            ?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        Text(Instant.parse(draft.consumedAt.instant).atZone(ZoneId.of(draft.consumedAt.timezone))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z")))
    }
    if (!ready) {
        QidaCard {
            QidaField(stringResource(R.string.fe_amount), amount, { amount = it; ready = false; invalid = false },
                if (invalid) stringResource(R.string.fe_invalid) else null, numeric = true)
            Text(portionUnitLabel(draft.unit.name))
        }
        PortionNotice(stringResource(if (draft.unit == EntryUnit.PREVIOUS_PORTION)
            R.string.pd_previous else R.string.pd_no_photo))
        if (draft.nutrients.values["energy"]?.value == null && draft.nutrients.values["energy_kcal"]?.value == null)
            PortionNotice(stringResource(R.string.pd_energy_unknown))
        QidaButton(stringResource(R.string.pd_preview), {
            val value = parseEntryNumber(amount)
            val next = value?.takeIf { it > 0 }?.let { runCatching { draft.copy(quantity = it).also { p -> p.nutrients } }.getOrNull() }
            invalid = next == null
            if (next != null) { update(next); ready = true }
        })
    } else {
        QidaCard {
            Text(stringResource(R.string.fe_ready))
            Text(stringResource(R.string.fe_unknown))
            Text("${draft.quantity} ${portionUnitLabel(draft.unit.name)}")
            if (draft.unit != EntryUnit.GRAM) draft.grams?.let { Text("$it ${stringResource(R.string.fe_grams)}") }
            Text(stringResource(R.string.pc_estimate))
            PortionNutrients(draft.nutrients)
        }
        PortionSecondaryButton(stringResource(R.string.pd_edit), { ready = false })
    }
    if (ready) QidaButton(stringResource(R.string.pc_confirm), {
        if (!busy && pending == null) {
            val log = runCatching { draft.confirm("local-guest", UUID.randomUUID().toString()) }.getOrNull()
            if (log == null) { invalid = true; ready = false }
            else {
                pending = Json.encodeToString(FoodLog.serializer(), log)
                busy = true
                scope.launch {
                    try {
                        val result = withContext(Dispatchers.IO) { record?.invoke(log) ?: UserRepositories.open(context).use { it.foodLogs.record(log) } }
                        saved = Json.encodeToString(FoodLog.serializer(), result)
                    } catch (cancelled: CancellationException) { throw cancelled }
                    catch (_: Exception) { /* Retain the exact event for retry; no success claim. */ }
                    finally { busy = false }
                }
            }
        }
    })
    PortionSecondaryButton(stringResource(R.string.back), back)
}

@Composable
private fun portionUnitLabel(unit: String?): String = stringResource(when (unit) {
    EntryUnit.KILOGRAM.name -> R.string.fe_kilograms
    EntryUnit.PREVIOUS_PORTION.name -> R.string.fe_previous_portion
    else -> R.string.fe_grams
})

@Composable
private fun PortionNutrients(snapshot: com.aladdin.dailyplate.domain.NutrientSnapshot) {
    if (snapshot.values.isEmpty()) Text(stringResource(R.string.fe_unknown_value))
    snapshot.values.forEach { (key, nutrient) ->
        val label = when (key) {
            "energy" -> stringResource(R.string.pc_energy)
            "protein" -> stringResource(R.string.pc_protein)
            "fat" -> stringResource(R.string.pc_fat)
            "carbohydrate", "carbs" -> stringResource(R.string.pc_carbs)
            else -> key
        }
        val unit = when (nutrient.unit) {
            com.aladdin.dailyplate.domain.NutrientUnit.KCAL -> "kcal"
            com.aladdin.dailyplate.domain.NutrientUnit.GRAM -> "g"
            com.aladdin.dailyplate.domain.NutrientUnit.MILLIGRAM -> "mg"
            com.aladdin.dailyplate.domain.NutrientUnit.MICROGRAM -> "µg"
        }
        Text("$label: ${nutrient.value?.toString() ?: stringResource(R.string.fe_unknown_value)} $unit")
    }
}
