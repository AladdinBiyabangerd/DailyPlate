package com.aladdin.dailyplate.ui.menu

import androidx.compose.material3.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import com.aladdin.dailyplate.ui.theme.QidaSpacing
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.planning.*
import com.aladdin.dailyplate.ui.*
import kotlinx.coroutines.*

@Composable
internal fun RecipeDetailScreen(planId: String, itemId: String, startEating: Boolean = false, back: () -> Unit) {
    val context = LocalContext.current
    val workflow = remember { RecipeWorkflow(context) }
    val scope = rememberCoroutineScope()
    var detail by remember { mutableStateOf<RecipeDetailData?>(null) }
    var pending by remember { mutableStateOf<PlanConsumption?>(null) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var saved by rememberSaveable { mutableStateOf(false) }
    fun load(resume: Boolean = false, command: PlanConsumption? = null) {
        if (busy) return
        busy = true; error = false
        scope.launch {
            try {
                if (resume || command != null) {
                    withContext(Dispatchers.IO) {
                        if (command != null) workflow.confirm(command) else workflow.resume()
                    }
                    // A failed detail refresh cannot turn a successful write into a failed intake.
                    saved = true; pending = null
                }
                val result = withContext(Dispatchers.IO) {
                    workflow.load(planId, itemId) to workflow.pending()
                }
                detail = result.first; pending = result.second
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) {
                error = true; detail = null
                pending = withContext(Dispatchers.IO) { workflow.pending() }
            } finally { busy = false }
        }
    }
    LaunchedEffect(planId, itemId) { load() }
    if (busy) QidaState(SurfaceState.Loading)
    else {
        if (error) QidaState(SurfaceState.Error, stringResource(R.string.rd_error)) { load() }
        if (saved) Text(stringResource(R.string.rd_saved))
        val retry = pending
        if (retry != null) {
            Text(stringResource(R.string.rd_pending))
            Text(retry.log.displayName ?: retry.log.recipe!!.id)
            RecipeNutrients(retry.log.nutrientSnapshot)
            QidaButton(stringResource(R.string.retry), { load(resume = true) })
        } else if (!saved) detail?.let { RecipeDetailContent(it, startEating = startEating) { command -> load(command = command) } }
        QidaButton(stringResource(R.string.back), back)
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
internal fun RecipeDetailContent(detail: RecipeDetailData, startEating: Boolean = false, confirm: (PlanConsumption) -> Unit) {
    var eating by rememberSaveable(detail.plan.ref, detail.item.id) { mutableStateOf(startEating &&
        detail.item.remainingPlannedServings > 0 && detail.item.status in
        setOf(PlanItemStatus.PLANNED, PlanItemStatus.PARTIALLY_CONSUMED)) }
    val top = remember { BringIntoViewRequester() }
    LaunchedEffect(eating) { top.bringIntoView(Rect(0f, 0f, 1f, 1f)) }
    Column(Modifier.bringIntoViewRequester(top), verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
        if (eating) RecipePortionEditor(detail, { eating = false }, confirm)
        else RecipeDetailDesign(detail) { eating = true }
    }
}

@Composable
internal fun RecipeNutrients(snapshot: NutrientSnapshot) {
    listOf("energy_kcal" to R.string.pc_energy, "protein_g" to R.string.pc_protein,
        "fat_g" to R.string.pc_fat, "available_carbohydrate_g" to R.string.pc_carbs).forEach { (key, label) ->
        val amount = snapshot.values[key]
        Text("${stringResource(label)}: ${amount?.value?.toString() ?: stringResource(R.string.fe_unknown_value)} ${if (key == "energy_kcal") "kcal" else "g"}")
    }
}
