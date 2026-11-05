package com.aladdin.dailyplate.ui.menu

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
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

@Composable
internal fun RecipeDetailContent(detail: RecipeDetailData, startEating: Boolean = false, confirm: (PlanConsumption) -> Unit) {
    val language = LocalConfiguration.current.locales[0].language
    val name = detail.recipe.names.recipeLabel(language)
    var eating by rememberSaveable(detail.plan.ref, detail.item.id) { mutableStateOf(startEating &&
        detail.item.remainingPlannedServings > 0 && detail.item.status in
        setOf(PlanItemStatus.PLANNED, PlanItemStatus.PARTIALLY_CONSUMED)) }
    if (eating) {
        RecipePortionEditor(detail, { eating = false }, confirm)
        return
    }
    QidaHeading(name)
    Text("${detail.item.date.iso} · ${detail.item.mealSlot}")
    Text(stringResource(R.string.rd_version, detail.recipe.ref.version, detail.customization?.ref?.version ?: 0L))
    Text(stringResource(R.string.rd_plan_portion, detail.item.plannedServings, detail.item.remainingPlannedServings))
    detail.item.portionGrams?.let { Text("$it ${stringResource(R.string.fe_grams)}") }
    Text(stringResource(R.string.rd_personal_nutrients))
    RecipeNutrients(detail.item.nutrientSnapshot)
    if (detail.customization == null) detail.recipe.time.totalMinutes?.let {
        Text(stringResource(R.string.rd_time, it))
    }
    QidaCard {
        Text(stringResource(R.string.rd_batch), style = MaterialTheme.typography.titleMedium)
        detail.batchServings?.let { Text(stringResource(R.string.rd_batch_servings, it)) }
        detail.ingredients.forEach { ingredient ->
            val food = detail.foods[ingredient.food]
            Text("${food?.names?.recipeLabel(language) ?: ingredient.food.id} · ${food?.preparationState ?: stringResource(R.string.fe_unknown_value)} · ${ingredient.edibleGrams} g")
        }
    }
    QidaCard {
        Text(stringResource(R.string.rd_steps), style = MaterialTheme.typography.titleMedium)
        if (detail.instructionStatus != InstructionStatus.VERIFIED) Text(stringResource(R.string.rd_unverified))
        val steps = detail.instructions
        if (steps.isNullOrEmpty()) Text(stringResource(R.string.rd_no_steps))
        else steps.forEachIndexed { index, step -> Text("${index + 1}. $step") }
    }
    if (detail.item.remainingPlannedServings > 0 && detail.item.status in
        setOf(PlanItemStatus.PLANNED, PlanItemStatus.PARTIALLY_CONSUMED)) {
        if (detail.batchServings == null) Text(stringResource(R.string.rd_no_basis))
        else QidaButton(stringResource(R.string.rd_ate), { eating = true })
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
