package com.aladdin.dailyplate.ui.menu

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.*
import com.aladdin.dailyplate.ui.*
import kotlinx.coroutines.*
import java.time.*

@Composable
fun MenuScreen(profile: () -> Unit) {
    val context = LocalContext.current
    val workflow = remember { MenuWorkflow(context) }
    val scope = rememberCoroutineScope()
    var data by remember { mutableStateOf<MenuData?>(null) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var week by rememberSaveable { mutableStateOf<String?>(null) }
    fun action(block: () -> Unit) {
        if (busy) return
        busy = true; error = false
        scope.launch {
            try { data = withContext(Dispatchers.IO) { block(); workflow.load() } }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { error = true }
            finally { busy = false }
        }
    }
    LaunchedEffect(Unit) { action {} }
    if (busy) { QidaState(SurfaceState.Loading); return }
    if (error) {
        QidaState(SurfaceState.Error, stringResource(R.string.mn_error)) { action { workflow.resume() } }
        QidaButton(stringResource(R.string.mn_reload), { action {} })
    }
    val d = data ?: return
    val p = d.profile
    if (p == null) {
        QidaState(SurfaceState.Empty, stringResource(R.string.empty_profile))
        QidaButton(stringResource(R.string.profile), profile); return
    }
    MenuProfileSummary(p)
    QidaButton(stringResource(R.string.mn_choices), profile)
    val plan = d.plans.firstOrNull { it.ref.id == week } ?: d.plans.firstOrNull()
    if (d.plans.size > 1) d.plans.forEach {
        QidaButton(it.week.iso, { week = it.ref.id })
    }
    plan?.let { DatedMenu(it) }
    val pending = d.pending
    if (pending != null) {
        val generated = d.generated
        if (generated == null) QidaButton(stringResource(R.string.retry), { action { workflow.resume() } })
        else if (pending.future == null) {
            GenerationMessage(generated.result.status)
            generated.result.plan?.let { preview ->
                Text(stringResource(R.string.mn_target, preview.targetKcal))
                preview.meals.forEach { Text("${it.day.iso} · ${it.recipe.id} · ${it.portionGrams} g") }
                if (generated.result.status == GenerationStatus.SUCCESS)
                    QidaButton(stringResource(R.string.mn_accept), { action { workflow.confirm() } })
            }
        } else {
            val result = d.future
            if (result == null) {
                GenerationMessage(generated.result.status)
                if (generated.result.status == GenerationStatus.SUCCESS)
                    QidaButton(stringResource(R.string.retry), { action { workflow.resume() } })
            } else {
                GenerationMessage(result.calculation.status)
                result.calculation.preview?.let { preview ->
                    Text(stringResource(R.string.mn_future_note))
                    Text(stringResource(R.string.mn_from, preview.effectiveFrom.iso))
                    Text(stringResource(R.string.mn_before_after))
                    Text("${preview.before.energyEstimate?.dailyKcal} → ${preview.after.energyEstimate?.dailyKcal} kcal")
                    preview.after.items.filter { item -> preview.before.items.firstOrNull { it.id == item.id } != item }
                        .forEach { item ->
                            val before = preview.before.items.firstOrNull { it.id == item.id }
                            Text("${item.date.iso} · ${item.mealSlot}: ${before?.recipe?.id ?: "—"} (${before?.portionGrams ?: "—"} g) → ${item.recipe.id} (${item.portionGrams ?: "—"} g)")
                        }
                    result.calculation.shoppingDelta.forEach { Text("${it.foodId}: ${it.quantityDelta} ${it.unit}") }
                    if (result.calculation.status == GenerationStatus.SUCCESS)
                        QidaButton(stringResource(R.string.mn_confirm_future), { action { workflow.confirm() } })
                }
            }
        }
        QidaButton(stringResource(R.string.mn_cancel), { action { workflow.discard() } })
    } else if (!d.inputsAvailable) MenuUnavailable()
    else {
        QidaButton(stringResource(R.string.mn_generate), { action { workflow.start(null) } })
        if (plan != null && plan.userProfile != p.ref &&
            LocalDate.now(ZoneId.of(plan.timezone)).toString() <= LocalDate.parse(plan.week.iso).plusDays(6).toString())
            QidaButton(stringResource(R.string.mn_recalculate), { action { workflow.start(plan) } })
    }
}

@Composable
internal fun MenuUnavailable() {
    QidaState(SurfaceState.Empty, stringResource(R.string.mn_unavailable))
}

@Composable
internal fun MenuProfileSummary(profile: UserProfile) {
    QidaCard {
        Text(stringResource(when (profile.goal) {
            Goal.LOSE -> R.string.ob_lose
            Goal.MAINTAIN -> R.string.ob_maintain
            Goal.GAIN -> R.string.ob_gain
            Goal.HEALTHY -> R.string.ob_healthy
        }), style = MaterialTheme.typography.titleMedium)
        Text(stringResource(R.string.mn_slots, profile.mealsPerDay))
    }
}

@Composable
private fun GenerationMessage(status: GenerationStatus) {
    Text(stringResource(when (status) {
        GenerationStatus.SUCCESS -> R.string.mn_ready
        GenerationStatus.NO_FEASIBLE_PLAN -> R.string.mn_no_plan
        GenerationStatus.INCOMPLETE_DATA -> R.string.mn_incomplete
        GenerationStatus.POLICY_UNAVAILABLE -> R.string.mn_unavailable
        GenerationStatus.STALE_INPUT -> R.string.mn_stale
        GenerationStatus.TEMPORARY_FAILURE -> R.string.mn_temporary
        GenerationStatus.REQUEST_CONFLICT -> R.string.mn_error
    }))
}

@Composable
internal fun DatedMenu(plan: MealPlan) {
    var selected by rememberSaveable(plan.ref.id) { mutableStateOf(
        LocalDate.now(ZoneId.of(plan.timezone)).toString().takeIf { date -> plan.items.any { it.date.iso == date } } ?: plan.week.iso) }
    Text("${plan.week.iso} · ${plan.timezone}")
    plan.energyEstimate?.dailyKcal?.let { Text(stringResource(R.string.mn_target, it)) }
    for (index in 0L..6L) {
        val date = LocalDate.parse(plan.week.iso).plusDays(index).toString()
        val today = date == LocalDate.now(ZoneId.of(plan.timezone)).toString()
        QidaButton("${if (date == selected) "✓ " else ""}$date${if (today) " · ${stringResource(R.string.today)}" else ""}", { selected = date })
    }
    plan.items.filter { it.date.iso == selected }.forEach { item ->
        QidaCard {
            Text(item.recipe.id, style = MaterialTheme.typography.titleMedium)
            Text("${item.date.iso} · ${item.mealSlot}")
            Text(stringResource(when (item.status) {
                PlanItemStatus.PLANNED -> R.string.mn_planned
                PlanItemStatus.PARTIALLY_CONSUMED -> R.string.mn_partial
                PlanItemStatus.CONSUMED -> R.string.mn_consumed
                PlanItemStatus.SKIPPED -> R.string.mn_skipped
            }))
            Text("${item.portionGrams ?: "—"} g · ${item.remainingPlannedServings}/${item.plannedServings}")
        }
    }
}
