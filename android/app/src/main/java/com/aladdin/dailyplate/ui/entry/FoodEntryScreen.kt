package com.aladdin.dailyplate.ui.entry

import androidx.activity.compose.BackHandler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.data.local.*
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.entry.*
import com.aladdin.dailyplate.ui.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId

/** Search and draft navigation; confirmation owns durable recording. */
@Composable
fun FoodEntryScreen(openSaved: (String) -> Unit = {}, openToday: () -> Unit = {}, resetScroll: () -> Unit) {
    val context = LocalContext.current
    val savedForms = rememberSaveableStateHolder()
    var foods by remember { mutableStateOf<List<Food>>(emptyList()) }
    var previous by remember { mutableStateOf<List<Pair<FoodLog, String>>>(emptyList()) }
    var state by remember { mutableStateOf(SurfaceState.Loading) }
    var refresh by remember { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var mode by rememberSaveable { mutableStateOf("home") }
    var encoded by rememberSaveable { mutableStateOf<String?>(null) }
    val language = LocalConfiguration.current.locales[0].language
    LaunchedEffect(refresh, language) {
        state = SurfaceState.Loading
        try {
            val data = withContext(Dispatchers.IO) {
                CatalogRepository.open(context).use { catalog ->
                    val logs = UserRepositories.open(context).use { it.foodLogs.recent("local-guest") }
                    catalog.productionFoods() to logs.map { log ->
                        val names = log.food?.let(catalog::food)?.names ?: log.recipe?.let(catalog::recipe)?.names
                        log to (log.displayName ?: names?.label(language) ?: context.getString(R.string.fe_previous_item, log.ref.id))
                    }
                }
            }
            foods = data.first; previous = data.second; state = SurfaceState.Empty
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { state = SurfaceState.Error }
    }
    fun select(draft: PortionDraft) { encoded = Json.encodeToString(PortionDraft.serializer(), draft) }
    fun now() = RecordedAt(Instant.now().toString(), ZoneId.systemDefault().id)
    val draft = encoded?.let { Json.decodeFromString(PortionDraft.serializer(), it) }
    BackHandler(mode != "home" || draft != null) { if (draft != null) encoded = null else mode = "home" }
    LaunchedEffect(mode, draft != null) { resetScroll() }
    when {
        draft != null -> PortionDraftEditor(draft, { encoded = null; refresh++ }, ::select, openSaved = openSaved, openToday = openToday)
        mode == "manual" -> savedForms.SaveableStateProvider("manual") {
            ManualFoodForm(query, { mode = "search" }) { select(it) }
        }
        mode == "home" -> {
            EntryLanding { mode = it }
            QidaButton(stringResource(R.string.fe_manual), { mode = "manual" })
        }
        mode == "camera" -> {
            EntryBack { mode = "home" }
            QidaHeading(stringResource(R.string.fe_camera))
            QidaState(SurfaceState.Empty, stringResource(R.string.fe_camera_unavailable))
            EntryAction(stringResource(R.string.fe_search), stringResource(R.string.fe_search_hint),
                R.drawable.art_entry_search) { mode = "search" }
            QidaButton(stringResource(R.string.fe_manual), { mode = "manual" })
        }
        else -> {
            EntryBack { mode = "home" }
            QidaHeading(stringResource(R.string.fe_title))
            QidaButton(stringResource(if (mode == "previous") R.string.fe_search else R.string.fe_previous), {
                mode = if (mode == "previous") "search" else "previous"
            })
            if (mode == "search") {
                QidaField(stringResource(R.string.fe_query), query, { query = it })
                if (query.isNotEmpty()) QidaButton(stringResource(R.string.fe_clear), { query = "" })
            }
            if (state == SurfaceState.Loading || state == SurfaceState.Error) {
                QidaState(state) { refresh++ }
            } else if (mode == "previous") {
                if (previous.isEmpty()) QidaState(SurfaceState.Empty, stringResource(R.string.fe_no_previous))
                previous.forEach { (log, name) ->
                    QidaCard {
                        Text(name)
                        Text(java.time.Instant.parse(log.consumedAt.instant).atZone(ZoneId.of(log.consumedAt.timezone))
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z")))
                        QidaButton(stringResource(R.string.fe_repeat), { select(log.repeatDraft(name, now())) })
                    }
                }
            } else {
                val matches = FoodSearch.find(foods, query)
                if (matches.isEmpty()) QidaState(SurfaceState.Empty, stringResource(R.string.fe_no_results))
                matches.forEach { food ->
                    FoodResultCard(food, food.names.label(language)) {
                        select(food.portionDraft(100.0, EntryUnit.GRAM, now()).copy(name = food.names.label(language)))
                    }
                }
            }
            QidaButton(stringResource(R.string.fe_manual), { mode = "manual" })
        }
    }
}

private fun LocalizedText.label(language: String) = when (language) { "en" -> en; "ru" -> ru; else -> az } ?: az
