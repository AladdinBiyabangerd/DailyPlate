package com.aladdin.dailyplate.ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle

/** Accepted-plan presentation only; date selection never changes a plan or a food log. */
@Composable
internal fun DatedMenu(plan: MealPlan, recipes: Map<VersionRef, Recipe> = emptyMap(),
                       today: LocalDate = LocalDate.now(ZoneId.of(plan.timezone)),
                       selectedDay: String? = null, selectDay: (String) -> Unit = {},
                       openRecipe: (MealPlanItem) -> Unit = {}, eat: (MealPlanItem) -> Unit = {}) {
    val locale = LocalConfiguration.current.locales[0]
    val first = LocalDate.parse(plan.week.iso)
    val last = first.plusDays(6)
    var selected by rememberSaveable(plan.ref.id, plan.week.iso) {
        mutableStateOf(selectedDay?.takeIf { LocalDate.parse(it) in first..last }
            ?: today.takeIf { it in first..last }?.toString() ?: first.toString())
    }
    QidaHeading(stringResource(R.string.wm_title))
    val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    Text(stringResource(R.string.wm_range, first.format(format), last.format(format)),
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    WeekSelector(first, selected, today) { selected = it; selectDay(it) }
    Text(stringResource(R.string.wm_date_zone, LocalDate.parse(selected).format(format), plan.timezone),
        style = MaterialTheme.typography.titleMedium)
    plan.energyEstimate?.dailyKcal?.let { Text(stringResource(R.string.mn_target, it)) }
    val items = plan.items.filter { it.date.iso == selected }
    if (items.isEmpty()) QidaState(SurfaceState.Empty, stringResource(R.string.wm_empty_day))
    items.forEach { item ->
        MenuMealCard(item, menuRecipeSummary(item, recipes[item.recipe]), { openRecipe(item) }, { eat(item) })
    }
    Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.errorContainer) {
        Column(Modifier.fillMaxWidth().padding(QidaSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
            Text(stringResource(R.string.wm_tools), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.wm_tools_note))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WeekSelector(first: LocalDate, selected: String, today: LocalDate, select: (String) -> Unit) {
    val config = LocalConfiguration.current
    val locale = config.locales[0]
    val expanded = config.fontScale > 1.3f || locale.language == "ru"
    @Composable fun day(index: Long, modifier: Modifier) {
        val date = first.plusDays(index)
        val checked = date.toString() == selected
        val current = date == today
        val fullDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale))
        val todayLabel = stringResource(R.string.today)
        Surface(onClick = { select(date.toString()) }, modifier = modifier
            .heightIn(min = QidaDimensions.touchTarget).testTag("menu-day-$date")
            .semantics { role = Role.Tab; this.selected = checked
                contentDescription = fullDate + if (current) ", $todayLabel" else "" },
            shape = MaterialTheme.shapes.large,
            color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (checked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            border = if (checked) BorderStroke(QidaDimensions.selectionStroke, MaterialTheme.colorScheme.primary) else null) {
            Column(Modifier.padding(QidaSpacing.small), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
                Text(if (expanded) fullDate else date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
                    style = MaterialTheme.typography.bodyMedium)
                if (!expanded) Text(date.dayOfMonth.toString(), style = MaterialTheme.typography.titleMedium)
                if (checked) Text("✓", style = MaterialTheme.typography.bodyMedium)
                if (current) Text(todayLabel, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
    if (expanded) FlowRow(Modifier.fillMaxWidth(), maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small),
        verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
        for (index in 0L..6L) day(index, Modifier.weight(1f))
    } else Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
        for (index in 0L..6L) day(index, Modifier.widthIn(min = QidaDimensions.touchTarget))
    }
}

@Composable
private fun MenuMealCard(item: MealPlanItem, summary: MenuRecipeSummary?, recipe: () -> Unit, eat: () -> Unit) {
    val config = LocalConfiguration.current
    val name = summary?.names?.recipeLabel(config.locales[0].language) ?: item.recipe.id
    val large = config.fontScale > 1.3f
    QidaCard {
        @Composable fun labels() {
            Text(slotLabel(item.mealSlot) + " · " + stringResource(when (item.status) {
                PlanItemStatus.PLANNED -> R.string.mn_planned
                PlanItemStatus.PARTIALLY_CONSUMED -> R.string.mn_partial
                PlanItemStatus.CONSUMED -> R.string.mn_consumed
                PlanItemStatus.SKIPPED -> R.string.mn_skipped
            }), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(name, style = MaterialTheme.typography.titleMedium)
            val energy = WeeklyNutrition.value(item.nutrientSnapshot, "energy_kcal", NutrientUnit.KCAL)
            Text(energy?.let { stringResource(R.string.wm_energy, it) } ?: stringResource(R.string.wm_energy_unknown))
            Text(summary?.totalMinutes?.let { stringResource(R.string.wm_time, it) }
                ?: stringResource(R.string.wm_time_unknown), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (large) {
            QidaArt(R.drawable.art_menu, Modifier.size(QidaDimensions.choiceArt).align(Alignment.CenterHorizontally))
            labels()
        } else Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
            QidaArt(R.drawable.art_menu, Modifier.size(QidaDimensions.choiceArt))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) { labels() }
        }
        Text(stringResource(R.string.wm_illustration), style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.wm_portion, item.plannedServings, item.remainingPlannedServings),
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        val canEat = item.remainingPlannedServings > 0 && item.status in
            setOf(PlanItemStatus.PLANNED, PlanItemStatus.PARTIALLY_CONSUMED)
        @Composable fun recipeButton(modifier: Modifier) {
            FilledTonalButton(onClick = recipe, modifier = modifier.heightIn(min = QidaDimensions.touchTarget)
                .testTag("menu-recipe-${item.id}"), shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(QidaSpacing.medium)) {
                Text(stringResource(R.string.rd_details), style = MaterialTheme.typography.labelLarge)
            }
        }
        if (large || config.locales[0].language == "ru") {
            recipeButton(Modifier.fillMaxWidth())
            if (canEat) QidaButton(stringResource(R.string.wm_ate), eat, Modifier.testTag("menu-eat-${item.id}"))
        } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
            recipeButton(Modifier.weight(1f))
            if (canEat) QidaButton(stringResource(R.string.wm_ate), eat,
                Modifier.weight(1f).testTag("menu-eat-${item.id}"))
        }
    }
}

@Composable
internal fun MenuLoadState(state: SurfaceState, retry: () -> Unit = {}) {
    QidaState(state, if (state == SurfaceState.Error) stringResource(R.string.mn_error) else null,
        if (state == SurfaceState.Error) retry else null)
}

@Composable
internal fun MenuMissingProfile(profile: () -> Unit) {
    QidaState(SurfaceState.Empty, stringResource(R.string.empty_profile))
    QidaButton(stringResource(R.string.profile), profile)
}
