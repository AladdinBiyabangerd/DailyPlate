package com.aladdin.dailyplate.ui.entry

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.entry.*
import com.aladdin.dailyplate.ui.*
import java.time.Instant
import java.time.ZoneId

@Composable
fun ManualFoodForm(initialName: String, back: () -> Unit, prepared: (PortionDraft) -> Unit) {
    var name by rememberSaveable { mutableStateOf(initialName) }
    var amount by rememberSaveable { mutableStateOf("") }
    var kilograms by rememberSaveable { mutableStateOf(false) }
    var source by rememberSaveable { mutableStateOf("") }
    var energy by rememberSaveable { mutableStateOf("") }
    var protein by rememberSaveable { mutableStateOf("") }
    var fat by rememberSaveable { mutableStateOf("") }
    var carbs by rememberSaveable { mutableStateOf("") }
    var invalid by rememberSaveable { mutableStateOf(false) }
    QidaHeading(stringResource(R.string.fe_manual))
    QidaField(stringResource(R.string.fe_name), name, { name = it })
    QidaField(stringResource(R.string.fe_amount), amount, { amount = it }, numeric = true)
    QidaButton(stringResource(if (kilograms) R.string.fe_kilograms else R.string.fe_grams) + " ✓", { kilograms = !kilograms })
    Text(stringResource(R.string.fe_basis))
    QidaField(stringResource(R.string.fe_energy), energy, { energy = it }, numeric = true)
    QidaField(stringResource(R.string.fe_protein), protein, { protein = it }, numeric = true)
    QidaField(stringResource(R.string.fe_fat), fat, { fat = it }, numeric = true)
    QidaField(stringResource(R.string.fe_carbs), carbs, { carbs = it }, numeric = true)
    QidaField(stringResource(R.string.fe_source), source, { source = it })
    Text(stringResource(R.string.fe_unknown))
    if (invalid) Text(stringResource(R.string.fe_invalid), color = MaterialTheme.colorScheme.error)
    QidaButton(stringResource(R.string.fe_prepare), {
        val fields = listOf(energy, protein, fat, carbs)
        val values = fields.map { if (it.isBlank()) null else parseEntryNumber(it) }
        val quantity = parseEntryNumber(amount)
        invalid = name.isBlank() || quantity == null || quantity <= 0 ||
            fields.indices.any { fields[it].isNotBlank() && values[it] == null } ||
            (values.any { it != null } && source.isBlank()) || (kilograms && quantity * 1000 == Double.POSITIVE_INFINITY)
        if (!invalid) prepared(PortionDraft(name.trim(), RecordedAt(Instant.now().toString(), ZoneId.systemDefault().id),
            EntryMethod.MANUAL, source = source.trim().ifBlank { null }, quantity = requireNotNull(quantity),
            unit = if (kilograms) EntryUnit.KILOGRAM else EntryUnit.GRAM,
            nutrientsPerBasis = NutrientSnapshot(listOf("energy_kcal", "protein_g", "fat_g", "carbohydrate_g").mapIndexed { i, key ->
                key to NutrientAmount(values[i], if (i == 0) NutrientUnit.KCAL else NutrientUnit.GRAM)
            }.toMap())))
    })
    QidaButton(stringResource(R.string.back), back)
}
