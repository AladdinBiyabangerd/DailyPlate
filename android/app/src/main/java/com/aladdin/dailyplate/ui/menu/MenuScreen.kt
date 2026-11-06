package com.aladdin.dailyplate.ui.menu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.Modifier
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

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MenuScreen(profile: () -> Unit) {
    val context = LocalContext.current
    var showDemo by rememberSaveable { mutableStateOf(debugMenuAvailable(context)) }
    if (debugMenuAvailable(context)) {
        DebugMenuToggle(showDemo) { showDemo = !showDemo }
        if (showDemo) {
            DebugWeeklyMenu()
            return
        }
    }
    val workflow = remember { MenuWorkflow(context) }
    val scope = rememberCoroutineScope()
    var data by remember { mutableStateOf<MenuData?>(null) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var recipePlan by rememberSaveable { mutableStateOf<String?>(null) }
    var recipeItem by rememberSaveable { mutableStateOf<String?>(null) }
    var eating by rememberSaveable { mutableStateOf(false) }
    val top = remember { BringIntoViewRequester() }
    Spacer(Modifier.height(com.aladdin.dailyplate.ui.theme.QidaSpacing.small).bringIntoViewRequester(top))
    LaunchedEffect(recipePlan, recipeItem, eating) { top.bringIntoView() }
    var week by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedDate by rememberSaveable { mutableStateOf<String?>(null) }
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
    if (recipePlan != null && recipeItem != null) {
        BackHandler { recipePlan = null; recipeItem = null; action {} }
        RecipeDetailScreen(requireNotNull(recipePlan), requireNotNull(recipeItem), startEating = eating) {
            recipePlan = null; recipeItem = null; action {}
        }
        return
    }
    if (busy) { MenuLoadState(SurfaceState.Loading); return }
    if (error) {
        MenuLoadState(SurfaceState.Error) { action {} }
        return
    }
    val d = data ?: return
    val p = d.profile
    if (p == null) {
        MenuMissingProfile(profile); return
    }
    val plan = d.plans.firstOrNull { it.ref.id == week } ?: d.plans.firstOrNull()
    if (d.plans.size > 1) d.plans.forEach {
        QidaButton(it.week.iso, { week = it.ref.id; selectedDate = null })
    }
    if (plan != null) DatedMenu(plan, d.recipes, selectedDay = selectedDate, selectDay = { selectedDate = it }, openRecipe = { item ->
        eating = false; recipePlan = plan.ref.id; recipeItem = item.id
    }, eat = { item ->
        eating = true; recipePlan = plan.ref.id; recipeItem = item.id
    })
    if (plan == null && d.pending == null && !d.inputsAvailable) MenuUnavailable()
    MenuProfileSummary(p)
    QidaButton(stringResource(R.string.mn_choices), profile)
    val pending = d.pending
    if (pending != null) {
        val generated = d.generated
        if (generated == null) QidaButton(stringResource(R.string.retry), { action { workflow.resume() } })
        else if (pending.future == null) {
            GenerationMessage(generated.result.status)
            generated.result.plan?.let { preview ->
                Text(stringResource(R.string.mn_target, preview.targetKcal))
                Text(stringResource(R.string.mn_cost, preview.shoppingCost.totalCost?.let { "${it.amount} ${it.currency}" } ?: "—"))
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
                    (preview.before.items.map { it.id } + preview.after.items.map { it.id }).distinct().forEach { id ->
                        val before = preview.before.items.firstOrNull { it.id == id }
                        val after = preview.after.items.firstOrNull { it.id == id }
                        if (before != after) {
                            val item = requireNotNull(after ?: before)
                            Text("${item.date.iso} · ${item.mealSlot}: ${before?.recipe?.id ?: "—"} (${before?.portionGrams ?: "—"} g) → ${after?.recipe?.id ?: "—"} (${after?.portionGrams ?: "—"} g)")
                        }
                    }
                    Text(stringResource(R.string.mn_cost, result.calculation.shoppingCost?.totalCost?.let { "${it.amount} ${it.currency}" } ?: "—"))
                    result.calculation.shoppingDelta.forEach { Text("${it.foodId}: ${it.quantityDelta} ${it.unit}") }
                    if (result.calculation.status == GenerationStatus.SUCCESS)
                        QidaButton(stringResource(R.string.mn_confirm_future), { action { workflow.confirm() } })
                }
            }
        }
        QidaButton(stringResource(R.string.mn_cancel), { action { workflow.discard() } })
    } else if (!d.inputsAvailable) {
        if (plan != null) MenuUnavailable(hasPlan = true)
    }
    else {
        QidaButton(stringResource(R.string.mn_generate), { action { workflow.start(null) } })
        if (plan != null && plan.userProfile != p.ref &&
            LocalDate.now(ZoneId.of(plan.timezone)).toString() <= LocalDate.parse(plan.week.iso).plusDays(6).toString())
            QidaButton(stringResource(R.string.mn_recalculate), { action { workflow.start(plan) } })
    }
}

@Composable
internal fun MenuUnavailable(hasPlan: Boolean = false) {
    if (!hasPlan) {
        QidaArt(R.drawable.art_empty_menu, Modifier.fillMaxWidth().height(com.aladdin.dailyplate.ui.theme.QidaDimensions.bannerArt))
        QidaHeading(stringResource(R.string.wm_empty))
    }
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
