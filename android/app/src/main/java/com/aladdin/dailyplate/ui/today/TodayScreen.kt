package com.aladdin.dailyplate.ui.today

import androidx.activity.compose.BackHandler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
fun TodayScreen(initialId: String? = null, add: () -> Unit, menu: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var logs by remember { mutableStateOf<List<FoodLog>>(emptyList()) }
    var target by remember { mutableStateOf<Double?>(null) }
    var selected by rememberSaveable(initialId) { mutableStateOf(initialId) }
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
    LaunchedEffect(refresh) {
        state = SurfaceState.Loading
        try {
            val data = withContext(Dispatchers.IO) {
                val id = OnboardingStore(context).load().id
                UserRepositories.open(context).use { repositories ->
                    val profile = repositories.profiles.latest("local-guest", id)
                    val assessment = profile?.let { repositories.nutritionAssessments.version(it.userId, it.ref)?.assessment }
                    repositories.foodLogs.recent("local-guest") to assessment?.takeIf {
                        it.path == com.aladdin.dailyplate.domain.policy.TargetPath.SPECIALIST
                    }?.dailyKcal
                }
            }
            logs = data.first; target = data.second; state = SurfaceState.Empty
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
    BackHandler(selected != null) { selected = null }
    if (state == SurfaceState.Loading || state == SurfaceState.Error) {
        QidaState(state) { refresh++ }; return
    }
    val entries = diaryDay(logs, day, zone)
    TodaySummary(entries, target, day.toString(), zone.id)
    if (writeError) {
        Text(stringResource(R.string.td_write_error))
        QidaButton(stringResource(R.string.retry), { refresh++ })
    }
    val log = logs.firstOrNull { it.ref.id == selected }
    if (log != null) {
        LogEditor(log, busy, { selected = null },
            { changed -> mutate { it.revise(changed, log.ref.version) } },
            { mutate { it.delete(log.userId, log.ref) } })
    } else {
        if (selected != null) Text(stringResource(R.string.td_missing))
        if (entries.isEmpty()) QidaState(SurfaceState.Empty, stringResource(R.string.empty_today))
        entries.forEach { entry ->
            QidaCard {
                Text(entry.displayName ?: entry.ref.id, style = MaterialTheme.typography.titleMedium)
                Text(Instant.parse(entry.consumedAt.instant).atZone(zone).toLocalTime().withSecond(0).withNano(0).toString())
                Text("${entry.confirmedPortionGrams ?: "?"} ${stringResource(R.string.fe_grams)}")
                QidaButton(stringResource(R.string.td_edit), { selected = entry.ref.id })
            }
        }
        QidaButton(stringResource(R.string.add_meal), add)
        QidaButton(stringResource(R.string.today_menu), menu)
    }
}

@Composable
fun TodaySummary(entries: List<FoodLog>, target: Double?, date: String, timezone: String) {
    Text("$date · $timezone")
    QidaCard {
        val energy = diaryTotal(entries, "energy", NutrientUnit.KCAL)
        Text(stringResource(R.string.pc_estimate))
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
private fun LogEditor(log: FoodLog, busy: Boolean, back: () -> Unit, save: (FoodLog) -> Unit, delete: () -> Unit) {
    var quantity by rememberSaveable(log.ref.id, log.ref.version) { mutableStateOf("1") }
    var preview by remember { mutableStateOf<FoodLog?>(null) }
    var invalid by remember { mutableStateOf(false) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    QidaHeading(log.displayName ?: log.ref.id)
    Text("${log.confirmedPortionGrams ?: "?"} ${stringResource(R.string.fe_grams)}")
    log.actualRecipeSnapshot?.ingredients?.forEach { Text("${it.food.id}: ${it.edibleGrams} ${stringResource(R.string.fe_grams)}") }
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
    if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false },
        title = { Text(stringResource(R.string.td_delete_question)) },
        text = { Text(log.displayName ?: log.ref.id) },
        confirmButton = { TextButton(onClick = { confirmDelete = false; delete() }) { Text(stringResource(R.string.td_delete)) } },
        dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.back)) } })
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
                    val profile = repositories.profiles.latest(log.userId, id)
                    val assessment = profile?.let { repositories.nutritionAssessments.version(it.userId, it.ref)?.assessment }
                    target = assessment?.takeIf { it.path == com.aladdin.dailyplate.domain.policy.TargetPath.SPECIALIST }?.dailyKcal
                    entries = diaryDay(repositories.foodLogs.recent(log.userId), date, zone)
                }
            }
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { /* The log is saved; a failed summary read is not a failed write. */ }
    }
    entries?.let { TodaySummary(it, target, date.toString(), zone.id) }
        ?: Text(stringResource(R.string.td_remaining_unknown))
}
