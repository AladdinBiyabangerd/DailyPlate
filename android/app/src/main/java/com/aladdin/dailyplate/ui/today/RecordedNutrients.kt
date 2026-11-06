package com.aladdin.dailyplate.ui.today

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.FoodLog
import com.aladdin.dailyplate.domain.NutrientUnit
import com.aladdin.dailyplate.ui.QidaCard

/** Display the saved snapshot; do not recalculate a historical meal from the catalog. */
@Composable
internal fun RecordedNutrients(log: FoodLog) {
    QidaCard {
        Text(stringResource(R.string.pc_estimate))
        if (log.nutrientSnapshot.values.isEmpty()) Text(stringResource(R.string.fe_unknown_value))
        log.nutrientSnapshot.values.forEach { (key, amount) ->
            val label = when (key) {
                "energy", "energy_kcal" -> stringResource(R.string.pc_energy)
                "protein", "protein_g" -> stringResource(R.string.pc_protein)
                "fat", "fat_g" -> stringResource(R.string.pc_fat)
                "carbohydrate", "carbohydrate_g", "available_carbohydrate_g" -> stringResource(R.string.pc_carbs)
                else -> key
            }
            val unit = when (amount.unit) {
                NutrientUnit.KCAL -> "kcal"
                NutrientUnit.GRAM -> "g"
                NutrientUnit.MILLIGRAM -> "mg"
                NutrientUnit.MICROGRAM -> "µg"
            }
            Text("$label: ${amount.value?.toString() ?: stringResource(R.string.fe_unknown_value)} $unit")
        }
    }
}
