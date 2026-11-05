package com.aladdin.dailyplate.ui.menu

import androidx.activity.compose.BackHandler
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.aladdin.dailyplate.ui.theme.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.entry.parseEntryNumber
import com.aladdin.dailyplate.domain.planning.PlanConsumption
import com.aladdin.dailyplate.ui.*
import com.aladdin.dailyplate.ui.entry.*
import java.time.Instant
import java.util.UUID

/** Selection and preview create no intake; only the last explicit action submits a command. */
@Composable
internal fun RecipePortionEditor(detail: RecipeDetailData, back: () -> Unit, confirm: (PlanConsumption) -> Unit) {
    val name = detail.recipe.names.recipeLabel(LocalConfiguration.current.locales[0].language)
    var prepared by rememberSaveable { mutableStateOf(detail.batchServings?.toString() ?: "") }
    var amount by rememberSaveable { mutableStateOf("") }
    var grams by rememberSaveable { mutableStateOf("") }
    var exact by rememberSaveable { mutableStateOf(false) }
    var invalid by rememberSaveable { mutableStateOf(false) }
    var preview by remember { mutableStateOf<PlanConsumption?>(null) }
    BackHandler { if (preview != null) preview = null else back() }
    EntryBack { if (preview != null) preview = null else back() }
    PortionHero(R.string.pd_recipe_title, R.drawable.art_cooking)
    QidaCard { Text(name, style = MaterialTheme.typography.titleMedium) }
    val basis = detail.batchServings
    if (basis == null) {
        PortionNotice(stringResource(R.string.rd_no_basis))
        QidaButton(stringResource(R.string.mn_cancel), back)
        return
    }
    val batch = parseEntryNumber(prepared)?.takeIf { it > 0 }
    val gramsPerServing = detail.item.portionGrams?.div(detail.item.plannedServings)
        ?.takeIf { it.isFinite() && it > 0 && detail.customization == null && detail.recipe.yieldCookedGrams != null }
    val serving = if (exact) parseEntryNumber(grams)?.let { value -> gramsPerServing?.let { value / it } }
        else parseEntryNumber(amount)
    val command = preview
    if (command == null) {
        PreparedServings(prepared, if (invalid && batch == null) stringResource(R.string.pd_batch_invalid) else null,
            { prepared = it; invalid = false }, { delta ->
                val next = (parseEntryNumber(prepared) ?: basis) + delta
                if (next.isFinite() && next > 0) { prepared = next.toString(); invalid = false }
            })
        Text(stringResource(R.string.pd_batch_scale), color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (!exact) QidaCard {
            Text(stringResource(R.string.pd_eaten), style = MaterialTheme.typography.titleMedium)
            SegmentedChoices(listOf("0.5" to stringResource(R.string.pd_half), "1.0" to stringResource(R.string.pd_one),
                "2.0" to stringResource(R.string.pd_two)), parseEntryNumber(amount)?.toString() ?: "", { amount = it; invalid = false })
            QidaField(stringResource(R.string.rd_personal), amount, { amount = it; invalid = false },
                if (invalid) stringResource(R.string.rd_invalid) else null, numeric = true)
        } else QidaCard {
            QidaField(stringResource(R.string.pd_grams), grams, { grams = it; invalid = false },
                if (invalid) stringResource(R.string.rd_invalid) else null, numeric = true)
        }
        if (batch != null && serving != null && serving > 0 && serving <= batch)
            QidaCard { Text(stringResource(R.string.pd_share, number(serving), number(batch))) }
        QidaButton(stringResource(if (exact) R.string.pd_servings_mode else R.string.pd_exact), {
            exact = !exact; invalid = false
        })
        if (exact && gramsPerServing == null) PortionNotice(stringResource(R.string.pd_no_yield))
        PortionNotice(stringResource(R.string.pc_estimate))
        QidaButton(stringResource(R.string.pd_preview), {
            preview = if (serving == null || batch == null) null else runCatching {
                detail.command(serving, name, UUID.randomUUID().toString(),
                    RecordedAt(Instant.now().toString(), detail.plan.timezone), batch)
            }.getOrNull()
            invalid = preview == null
        })
    } else {
        QidaCard {
            Text(stringResource(R.string.fe_ready))
            Text(stringResource(R.string.rd_confirm_portion, command.consumedServings))
            Text(stringResource(R.string.pd_share, number(command.consumedServings), number(command.log.actualRecipeSnapshot!!.servingsPrepared)))
            command.log.confirmedPortionGrams?.let { Text(stringResource(R.string.pd_grams) + ": ${number(it)}") }
            Text(stringResource(R.string.pc_estimate))
            RecipeNutrients(command.log.nutrientSnapshot)
        }
        QidaCard {
            Text(stringResource(R.string.rd_batch), style = MaterialTheme.typography.titleMedium)
            command.log.actualRecipeSnapshot!!.ingredients.forEach { ingredient ->
                Text((detail.foods[ingredient.food]?.names?.recipeLabel(LocalConfiguration.current.locales[0].language)
                    ?: ingredient.food.id) + ": " + stringResource(R.string.pd_batch_grams, number(ingredient.edibleGrams)))
            }
        }
        QidaButton(stringResource(R.string.pd_edit), { preview = null })
        QidaButton(stringResource(R.string.pc_confirm), { confirm(command) })
    }
    QidaButton(stringResource(R.string.mn_cancel), back)
}

private fun number(value: Double): String = java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()

@Composable
private fun PreparedServings(value: String, error: String?, change: (String) -> Unit, step: (Int) -> Unit) {
    val label = stringResource(R.string.pd_prepared)
    QidaCard {
        Text(label, style = MaterialTheme.typography.titleMedium)
        if (LocalConfiguration.current.fontScale > 1.3f) {
            QidaField(stringResource(R.string.pd_count), value, change, error, numeric = true)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                listOf(-1, 1).forEach { delta ->
                    FilledTonalButton(onClick = { step(delta) }, modifier = Modifier.weight(1f)
                        .heightIn(min = QidaDimensions.touchTarget)
                        .semantics { contentDescription = label + if (delta > 0) " +1" else " −1" },
                        contentPadding = PaddingValues(QidaSpacing.small)) { Text(if (delta > 0) "+" else "−") }
                }
            }
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                FilledTonalButton(onClick = { step(-1) }, modifier = Modifier.size(QidaDimensions.touchTarget)
                    .semantics { contentDescription = "$label −1" }, contentPadding = PaddingValues(QidaSpacing.small)) { Text("−") }
                Box(Modifier.weight(1f)) { QidaField(stringResource(R.string.pd_count), value, change, error, numeric = true) }
                FilledTonalButton(onClick = { step(1) }, modifier = Modifier.size(QidaDimensions.touchTarget)
                    .semantics { contentDescription = "$label +1" }, contentPadding = PaddingValues(QidaSpacing.small)) { Text("+") }
            }
        }
    }
}
