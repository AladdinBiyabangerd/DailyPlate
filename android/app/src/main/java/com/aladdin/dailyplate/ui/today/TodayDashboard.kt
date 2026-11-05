package com.aladdin.dailyplate.ui.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.diary.diaryTotal
import com.aladdin.dailyplate.ui.*
import com.aladdin.dailyplate.ui.theme.*
import java.time.Instant
import java.time.ZoneId

/** Artwork is illustrative, never a meal photo or a measured portion reference. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TodayDashboard(entries: List<FoodLog>, meals: List<TodayMeal>, date: String, zone: ZoneId,
                            add: () -> Unit, menu: () -> Unit, openLog: (String) -> Unit,
                            openMeal: (TodayMeal) -> Unit) {
    Text(date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    QidaHeading(stringResource(R.string.td_greeting))
    val next = meals.firstOrNull { it.item.status in setOf(PlanItemStatus.PLANNED, PlanItemStatus.PARTIALLY_CONSUMED) &&
        it.item.remainingPlannedServings > 0 }
    val latest = entries.maxByOrNull { Instant.parse(it.consumedAt.instant) }
    Card(onClick = { if (next != null) openMeal(next) else if (latest != null) openLog(latest.ref.id) else add() },
        modifier = Modifier.fillMaxWidth().testTag("today-hero"), shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary)) {
        Column(Modifier.padding(QidaSpacing.medium), verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
            if (LocalConfiguration.current.fontScale > 1.3f) {
                MealIllustration(Modifier.size(QidaDimensions.bannerArt).clip(CircleShape).align(Alignment.CenterHorizontally))
                HeroLabel(next, latest)
            } else Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) { HeroLabel(next, latest) }
                MealIllustration(Modifier.size(QidaDimensions.bannerArt).clip(CircleShape))
            }
            Text(stringResource(R.string.td_illustration), style = MaterialTheme.typography.bodyMedium)
            FilledTonalButton(onClick = add, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget),
                shape = MaterialTheme.shapes.large, contentPadding = PaddingValues(QidaSpacing.medium),
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary)) {
                Text("+ " + stringResource(R.string.add_meal), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
    QidaCard {
        Text(stringResource(R.string.td_total), style = MaterialTheme.typography.titleMedium)
        val energy = diaryTotal(entries, "energy", NutrientUnit.KCAL)
        Text(energy?.let { stringResource(R.string.td_kcal, it) } ?: stringResource(R.string.fe_unknown_value),
            style = MaterialTheme.typography.headlineMedium)
        // No diary-completeness declaration exists yet. Do not turn partial intake into an allowance.
        Text(stringResource(R.string.td_remaining_unknown), color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (meals.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small),
                verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                meals.forEach { meal ->
                    TextButton(onClick = { openMeal(meal) },
                        contentPadding = PaddingValues(QidaSpacing.small),
                        modifier = (if (LocalConfiguration.current.fontScale > 1.3f) Modifier.fillMaxWidth()
                            else Modifier.widthIn(min = QidaDimensions.touchTarget,
                                max = QidaDimensions.choiceArt + QidaSpacing.medium))
                            .heightIn(min = QidaDimensions.touchTarget)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                MealIllustration(Modifier.size(QidaDimensions.touchTarget).clip(CircleShape))
                                if (meal.item.status == PlanItemStatus.CONSUMED) Text("✓")
                            }
                            Text(slotLabel(meal.item.mealSlot), style = MaterialTheme.typography.bodyMedium)
                            Text(statusLabel(meal.item.status), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
    if (meals.isNotEmpty()) {
        Text(stringResource(R.string.today_menu), style = MaterialTheme.typography.titleLarge)
        meals.forEach { meal ->
            MealPictureCard(meal.name, slotLabel(meal.item.mealSlot) + " · " + statusLabel(meal.item.status),
                "today-plan-" + meal.item.id, { openMeal(meal) }, planned = true)
        }
    }
    if (entries.isEmpty()) {
        if (meals.isEmpty()) QidaArt(R.drawable.art_empty, Modifier.fillMaxWidth().height(QidaDimensions.bannerArt))
        Text(stringResource(R.string.empty_today), style = MaterialTheme.typography.bodyLarge)
    } else {
        Text(stringResource(R.string.td_recorded_meals), style = MaterialTheme.typography.titleLarge)
        entries.forEach { entry ->
            val time = Instant.parse(entry.consumedAt.instant).atZone(zone).toLocalTime().withSecond(0).withNano(0)
            MealPictureCard(entry.displayName ?: entry.ref.id, "$time · " + stringResource(R.string.td_recorded),
                "today-log-" + entry.ref.id, { openLog(entry.ref.id) })
        }
    }
    TextButton(onClick = menu, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget)) {
        Text(stringResource(R.string.today_menu), style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun HeroLabel(next: TodayMeal?, latest: FoodLog?) {
    Text(stringResource(if (next != null) R.string.td_next_meal else if (latest != null) R.string.td_latest_meal else R.string.td_start),
        style = MaterialTheme.typography.titleMedium)
    Text(next?.name ?: latest?.displayName ?: latest?.ref?.id ?: stringResource(R.string.empty_today), style = MaterialTheme.typography.titleLarge)
    if (next != null) Text(stringResource(R.string.mn_planned))
    else if (latest != null) Text(stringResource(R.string.td_recorded))
    if (next != null || latest != null) Text(stringResource(if (next != null) R.string.rd_details else R.string.td_details), style = MaterialTheme.typography.labelLarge)
}

@Composable
private fun MealPictureCard(name: String, subtitle: String, tag: String, open: () -> Unit, planned: Boolean = false) {
    Card(onClick = open, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget).testTag(tag),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(QidaSpacing.medium), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
            MealIllustration(Modifier.size(QidaDimensions.choiceArt).clip(CircleShape))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(stringResource(if (planned) R.string.rd_details else R.string.td_details), color = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.td_illustration), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
internal fun slotLabel(slot: String): String = when (slot.lowercase()) {
    "breakfast" -> stringResource(R.string.td_breakfast)
    "lunch" -> stringResource(R.string.td_lunch)
    "dinner" -> stringResource(R.string.td_dinner)
    "snack", "snack1", "snack2", "snack_1", "snack_2" -> stringResource(R.string.td_snack)
    else -> slot.toIntOrNull()?.let { stringResource(R.string.td_numbered_meal, it + 1) } ?: slot
}

@Composable
private fun statusLabel(status: PlanItemStatus) = stringResource(when (status) {
    PlanItemStatus.PLANNED -> R.string.mn_planned
    PlanItemStatus.PARTIALLY_CONSUMED -> R.string.mn_partial
    PlanItemStatus.CONSUMED -> R.string.mn_consumed
    PlanItemStatus.SKIPPED -> R.string.mn_skipped
})

@Composable
private fun MealIllustration(modifier: Modifier = Modifier) {
    Image(painterResource(R.drawable.art_empty), contentDescription = null,
        modifier = modifier.clip(CircleShape), contentScale = ContentScale.Crop)
}
