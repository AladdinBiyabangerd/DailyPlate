package com.aladdin.dailyplate.ui.today

import com.aladdin.dailyplate.domain.policy.diaryTarget
import com.aladdin.dailyplate.domain.policy.profileForDiaryDate

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.data.local.*
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.diary.*
import com.aladdin.dailyplate.domain.entry.parseEntryNumber
import com.aladdin.dailyplate.ui.*
import kotlinx.coroutines.*
import java.time.*

@Composable
fun TodayScreen(initialId: String? = null, add: () -> Unit, menu: () -> Unit, resetScroll: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var logs by remember { mutableStateOf<List<FoodLog>>(emptyList()) }
    var meals by remember { mutableStateOf<List<TodayMeal>>(emptyList()) }
    val language = LocalConfiguration.current.locales[0].language
    var selected by rememberSaveable(initialId) { mutableStateOf(initialId) }
    var recipePlan by rememberSaveable { mutableStateOf<String?>(null) }
    var recipeItem by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(selected, recipePlan, recipeItem) { resetScroll() }
    var refresh by remember { mutableIntStateOf(0) }
    var state by remember { mutableStateOf(SurfaceState.Loading) }
    var busy by remember { mutableStateOf(false) }
    var writeError by remember { mutableStateOf(false) }
    var day by remember { mutableStateOf(LocalDate.now()) }
    var zone by remember { mutableStateOf(ZoneId.systemDefault()) }
    // Also refresh on resume/timezone changes; no stale previous-day totals after midnight.
    val owner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(owner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) refresh++
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(Unit) {
        while (true) {
            zone = ZoneId.systemDefault(); day = LocalDate.now(zone)
            delay(1000)
        }
    }
    LaunchedEffect(refresh, day, zone, language) {
        state = SurfaceState.Loading
        try {
            val data = withContext(Dispatchers.IO) {
                val entries = UserRepositories.open(context).use { it.foodLogs.recent("local-guest") }
                entries to loadTodayMeals(context, language, day)
            }
            logs = data.first; meals = data.second; state = SurfaceState.Empty
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { state = SurfaceState.Error }
    }
    fun mutate(action: (FoodLogRepository) -> Unit) {
        if (busy) return
        busy = true; writeError = false
        scope.launch {
            try {
                withContext(Dispatchers.IO) { UserRepositories.open(context).use { action(it.foodLogs) } }
                selected = null; refresh++
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { writeError = true }
            finally { busy = false }
        }
    }
    BackHandler(selected != null || recipePlan != null) { selected = null; recipePlan = null; recipeItem = null }
    if (recipePlan != null && recipeItem != null) {
        com.aladdin.dailyplate.ui.menu.RecipeDetailScreen(requireNotNull(recipePlan), requireNotNull(recipeItem)) {
            recipePlan = null; recipeItem = null; refresh++
        }
        return
    }
    if (state == SurfaceState.Loading || state == SurfaceState.Error) {
        QidaState(state) { refresh++ }; return
    }
    val entries = diaryDay(logs, day, zone)
    if (writeError) {
        Text(stringResource(R.string.td_write_error))
        QidaButton(stringResource(R.string.retry), { refresh++ })
    }
    val log = logs.firstOrNull { it.ref.id == selected && !it.deleted }
    if (log != null) {
        // Full nutrients belong in the existing detail/editor, not the dashboard.
        TodaySummary(listOf(log), null, "", log.consumedAt.timezone)
        LogEditor(log, busy, { selected = null },
            { changed -> mutate { it.revise(changed, log.ref.version) } },
            { mutate { it.delete(log.userId, log.ref) } })
    } else {
        if (selected != null) Text(stringResource(R.string.td_missing))
        TodayDashboard(entries, meals, day.format(java.time.format.DateTimeFormatter.ofLocalizedDate(
            java.time.format.FormatStyle.FULL).withLocale(LocalConfiguration.current.locales[0])), zone, add, menu,
            { selected = it }, { meal -> recipePlan = meal.planId; recipeItem = meal.item.id })
        com.aladdin.dailyplate.ui.menu.TodayPlanProjection(logs, refresh, day)
    }
}

@Composable
fun TodaySummary(entries: List<FoodLog>, target: Double?, date: String, timezone: String) {
    Text("$date · $timezone")
    QidaCard {
        val energy = diaryTotal(entries, "energy", NutrientUnit.KCAL)
        Text(stringResource(R.string.td_total))
        Text("${stringResource(R.string.pc_energy)}: ${energy?.toString() ?: stringResource(R.string.fe_unknown_value)} kcal")
        listOf("protein" to R.string.pc_protein, "fat" to R.string.pc_fat, "carbohydrate" to R.string.pc_carbs).forEach { (key, label) ->
            Text("${stringResource(label)}: ${diaryTotal(entries, key, NutrientUnit.GRAM)?.toString() ?: stringResource(R.string.fe_unknown_value)} g")
        }
        if (target != null && energy != null) {
            val remaining = target - energy
            Text(stringResource(if (remaining >= 0) R.string.td_remaining else R.string.td_over, kotlin.math.abs(remaining)))
        } else Text(stringResource(R.string.td_remaining_unknown))
    }
}

@Composable
internal fun LogEditor(log: FoodLog, busy: Boolean, back: () -> Unit, save: (FoodLog) -> Unit, delete: () -> Unit) {
    var quantity by rememberSaveable(log.ref.id, log.ref.version) { mutableStateOf("1") }
    var preview by remember { mutableStateOf<FoodLog?>(null) }
    var invalid by remember { mutableStateOf(false) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    QidaHeading(log.displayName ?: log.ref.id)
    RecordedPortion(log)
    RecordedIngredients(log)
    Text(stringResource(R.string.pc_local))
    QidaField(stringResource(R.string.td_multiple), quantity, { quantity = it; preview = null },
        if (invalid) stringResource(R.string.fe_invalid) else null, numeric = true)
    if (busy) { QidaState(SurfaceState.Loading); return }
    QidaButton(stringResource(R.string.fe_prepare), {
        preview = parseEntryNumber(quantity)?.let { runCatching { log.resizePortion(it) }.getOrNull() }
        invalid = preview == null
    })
    preview?.let { changed ->
        TodaySummary(listOf(changed), null, "", log.consumedAt.timezone)
        QidaButton(stringResource(R.string.pc_confirm), { save(changed) })
    }
    QidaButton(stringResource(R.string.td_delete), { confirmDelete = true })
    QidaButton(stringResource(R.string.back), back)
    if (confirmDelete) QidaCard {
        Text(stringResource(R.string.td_delete_question), style = MaterialTheme.typography.titleMedium)
        Text(log.displayName ?: log.ref.id)
        QidaButton(stringResource(R.string.td_delete), { confirmDelete = false; delete() })
        QidaButton(stringResource(R.string.back), { confirmDelete = false })
    }
}

/** Success summary reads committed data; never derives totals from the pending draft. */
@Composable
fun SavedDaySummary(log: FoodLog) {
    val context = LocalContext.current
    var entries by remember { mutableStateOf<List<FoodLog>?>(null) }
    var target by remember { mutableStateOf<Double?>(null) }
    val zone = ZoneId.systemDefault()
    val date = Instant.parse(log.consumedAt.instant).atZone(zone).toLocalDate()
    LaunchedEffect(log.ref) {
        try {
            withContext(Dispatchers.IO) {
                val id = OnboardingStore(context).load().id
                UserRepositories.open(context).use { repositories ->
                    val profile = profileForDiaryDate(repositories.profiles.history(log.userId, id), date, zone)
                    val assessment = profile?.let { repositories.nutritionAssessments.version(it.userId, it.ref)?.assessment }
                    target = assessment?.diaryTarget()
                    entries = diaryDay(repositories.foodLogs.recent(log.userId), date, zone)
                }
            }
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { /* The log is saved; a failed summary read is not a failed write. */ }
    }
    entries?.let { TodaySummary(it, target, date.toString(), zone.id) }
        ?: Text(stringResource(R.string.td_remaining_unknown))
}

@Composable
private fun RecordedPortion(log: FoodLog) {
    val grams = log.confirmedPortionGrams
    if (grams != null) Text("$grams ${stringResource(R.string.fe_grams)}")
    else Text(stringResource(R.string.td_batch_share, requireNotNull(log.actualRecipeSnapshot).consumedShare))
}
