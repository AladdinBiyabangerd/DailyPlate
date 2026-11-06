package com.aladdin.dailyplate.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.WeeklyNutrition
import com.aladdin.dailyplate.ui.*
import com.aladdin.dailyplate.ui.theme.*
import com.aladdin.dailyplate.ui.today.slotLabel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** Read-only presentation of the exact accepted recipe and personal nutrient snapshot. */
@Composable
internal fun RecipeDetailDesign(detail: RecipeDetailData, eat: () -> Unit) {
    val locale = LocalConfiguration.current.locales[0]
    QidaHeading(detail.recipe.names.recipeLabel(locale.language))
    Text(LocalDate.parse(detail.item.date.iso).format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)) + " · " + slotLabel(detail.item.mealSlot),
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        QidaArt(R.drawable.art_recipe, Modifier.size(QidaDimensions.heroArt))
    }
    Text(stringResource(R.string.rdd_art), style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    Column(Modifier.testTag("d04-summary"), verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
        QidaCard {
            Text(stringResource(R.string.rdd_personal, detail.item.plannedServings),
                style = MaterialTheme.typography.titleMedium)
            val energy = WeeklyNutrition.value(detail.item.nutrientSnapshot, "energy_kcal", NutrientUnit.KCAL)
            Text(energy?.let { stringResource(R.string.rdd_energy, it) } ?: stringResource(R.string.wm_energy_unknown))
            detail.item.portionGrams?.let { Text(stringResource(R.string.rdd_grams, it)) }
            val minutes = detail.recipe.time.totalMinutes.takeIf { detail.customization == null }
            Text(minutes?.let { stringResource(R.string.wm_time, it) } ?: stringResource(R.string.wm_time_unknown),
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(stringResource(when (detail.item.status) {
                PlanItemStatus.PLANNED -> R.string.mn_planned
                PlanItemStatus.PARTIALLY_CONSUMED -> R.string.mn_partial
                PlanItemStatus.CONSUMED -> R.string.mn_consumed
                PlanItemStatus.SKIPPED -> R.string.mn_skipped
            }))
        }
    }
    Text(stringResource(R.string.rdd_ingredients), Modifier.testTag("d04-ingredients").semantics { heading() },
        style = MaterialTheme.typography.titleLarge)
    Text(stringResource(R.string.rdd_batch_hint), color = MaterialTheme.colorScheme.onSurfaceVariant)
    detail.batchServings?.let { Text(stringResource(R.string.rd_batch_servings, it)) }
    if (detail.ingredients.isEmpty()) Text(stringResource(R.string.rdd_empty))
    // Full-width rows at large font sizes; two native tiles otherwise. No fake photo metadata.
    DesignGrid(detail.ingredients.mapIndexed { index, ingredient ->
        ingredient.food.id to { modifier: Modifier ->
            Surface(modifier.testTag("d04-ingredient-$index"), shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.fillMaxWidth().padding(QidaSpacing.medium),
                    verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                    val food = detail.foods[ingredient.food]
                    QidaArt(recipeIngredientArt(food), Modifier.size(QidaDimensions.choiceArt))
                    Text(food?.names?.recipeLabel(locale.language) ?: ingredient.food.id,
                        style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.rdd_grams, ingredient.edibleGrams))
                    Text(food?.preparationState ?: stringResource(R.string.fe_unknown_value),
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    })
    RecipeDisclosure(stringResource(R.string.rdd_steps), "d04-steps", detail.item.id) {
        if (detail.instructionStatus != InstructionStatus.VERIFIED) Text(stringResource(R.string.rd_unverified))
        if (detail.instructions.isNullOrEmpty()) Text(stringResource(R.string.rd_no_steps))
        else detail.instructions!!.forEachIndexed { index, step ->
            Row(Modifier.testTag("d04-step-$index"), horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.primary) {
                    Text("${index + 1}", Modifier.padding(QidaSpacing.small), color = MaterialTheme.colorScheme.onPrimary)
                }
                Text(step, Modifier.weight(1f))
            }
        }
    }
    if (detail.item.remainingPlannedServings > 0 && detail.item.status in
        setOf(PlanItemStatus.PLANNED, PlanItemStatus.PARTIALLY_CONSUMED)) {
        if (detail.batchServings == null) Text(stringResource(R.string.rd_no_basis))
        else QidaButton(stringResource(R.string.rd_ate), eat, Modifier.testTag("d04-eat"))
    }
    RecipeDisclosure(stringResource(R.string.rdd_more), "d04-info", detail.item.id) {
        Text(stringResource(R.string.rd_personal_nutrients), style = MaterialTheme.typography.titleMedium)
        RecipeNutrients(detail.item.nutrientSnapshot)
        Text(stringResource(R.string.rd_plan_portion, detail.item.plannedServings, detail.item.remainingPlannedServings))
        Text(stringResource(R.string.rd_version, detail.recipe.ref.version, detail.customization?.ref?.version ?: 0L),
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun RecipeDisclosure(title: String, tag: String, itemId: String, content: @Composable ColumnScope.() -> Unit) {
    var expanded by rememberSaveable(itemId, tag) { mutableStateOf(false) }
    val description = stringResource(if (expanded) R.string.rdd_expanded else R.string.rdd_collapsed)
    QidaCard {
        TextButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()
            .heightIn(min = QidaDimensions.touchTarget).testTag(tag)
            .semantics { stateDescription = description },
            shape = MaterialTheme.shapes.large) {
            Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Text(if (expanded) "−" else "+", Modifier.padding(start = QidaSpacing.small))
        }
        if (expanded) content()
    }
}
