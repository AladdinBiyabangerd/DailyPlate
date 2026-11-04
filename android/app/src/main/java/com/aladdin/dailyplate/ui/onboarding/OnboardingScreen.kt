package com.aladdin.dailyplate.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.data.local.OnboardingStore
import com.aladdin.dailyplate.domain.onboarding.OnboardingDraft
import com.aladdin.dailyplate.ui.*
import com.aladdin.dailyplate.ui.theme.QidaDimensions
import com.aladdin.dailyplate.ui.theme.QidaSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(language: String, exit: () -> Unit, resetScroll: () -> Unit, done: () -> Unit) {
    val context = LocalContext.current
    val store = remember { OnboardingStore(context) }
    val scope = rememberCoroutineScope()
    var draft by remember { mutableStateOf<OnboardingDraft?>(null) }
    var writeError by remember { mutableStateOf(false) }
    var invalid by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var confirmed by remember { mutableStateOf<com.aladdin.dailyplate.domain.UserProfile?>(null) }
    val saved = confirmed != null
    LaunchedEffect(Unit) {
        try { val loaded = withContext(Dispatchers.IO) { store.load() }; confirmed = withContext(Dispatchers.IO) { store.confirmed(loaded.id) }; draft = loaded } catch (_: Exception) { writeError = true }
    }
    fun update(value: OnboardingDraft) {
        draft = value; invalid = false
        val write = store.queueSave(value)
        scope.launch {
            try { withContext(Dispatchers.IO) { write.get() }; writeError = false }
            catch (_: Exception) { writeError = true }
        }
    }
    val d = draft
    if (writeError) QidaState(SurfaceState.Error, stringResource(R.string.ob_write_error)) {
        if (d != null) update(d) else scope.launch {
            try { val loaded = withContext(Dispatchers.IO) { store.load() }; confirmed = withContext(Dispatchers.IO) { store.confirmed(loaded.id) }; draft = loaded; writeError = false } catch (_: Exception) { writeError = true }
        }
    }
    if (d == null) { QidaState(SurfaceState.Loading); return }
    LaunchedEffect(d.step, saved) { resetScroll() }
    if (busy) { QidaState(SurfaceState.Loading); return }
    if (saved) {
        QidaHeading(stringResource(R.string.ob_saved))
        val profile = requireNotNull(confirmed)
        Text(stringResource(when (profile.goal) {
            com.aladdin.dailyplate.domain.Goal.LOSE -> R.string.ob_lose
            com.aladdin.dailyplate.domain.Goal.MAINTAIN -> R.string.ob_maintain
            com.aladdin.dailyplate.domain.Goal.GAIN -> R.string.ob_gain
            com.aladdin.dailyplate.domain.Goal.HEALTHY -> R.string.ob_healthy
        }))
        Text(stringResource(R.string.ob_meals) + ": " + profile.mealsPerDay)
        Text(stringResource(R.string.ob_boundary)); Text(stringResource(R.string.ob_local_note))
        QidaButton(stringResource(R.string.continue_action), done)
        return
    }
    BackHandler(d.step > 0 && !busy) { update(d.copy(step = d.step - 1)) }
    TextButton(onClick = { if (d.step == 0) exit() else update(d.copy(step = d.step - 1)) }, enabled = !busy) { Text(stringResource(R.string.back)) }
    Text("${d.step + 1}/5", style = MaterialTheme.typography.bodyLarge)
    QidaHeading(stringResource(listOf(R.string.ob_goal, R.string.ob_body, R.string.ob_activity,
        R.string.ob_allergies, R.string.ob_preferences)[d.step]))
    if (invalid) Text(stringResource(R.string.ob_invalid), style = MaterialTheme.typography.bodyLarge)
    fun change(key: String, value: String) { update(d.change(key, value)) }
    @Composable fun field(key: String, label: Int, numeric: Boolean = false) {
        QidaField(stringResource(label), d.answer(key), { change(key, it) },
            if (invalid && key in d.errors(d.step)) stringResource(R.string.ob_invalid) else null, numeric)
    }
    @Composable fun choice(key: String, value: String, label: Int) {
        OnboardingChoice(stringResource(label), d.answer(key) == value) { change(key, value) }
    }
    when (d.step) {
        0 -> listOf("LOSE" to R.string.ob_lose, "MAINTAIN" to R.string.ob_maintain,
            "GAIN" to R.string.ob_gain, "HEALTHY" to R.string.ob_healthy).forEach { (v, l) -> choice("goal", v, l) }
        1 -> {
            listOf("age" to R.string.ob_age, "height" to R.string.ob_height, "weight" to R.string.ob_weight).forEach { (key, label) ->
                field(key, label, true)
                Row(horizontalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
                    listOf(-1, 1).forEach { delta ->
                        TextButton(onClick = { change(key, ((d.number(key) ?: 0.0) + delta).toString()) },
                            modifier = Modifier.weight(1f).heightIn(min = QidaDimensions.touchTarget)) {
                            Text(stringResource(label) + if (delta > 0) " +1" else " −1")
                        }
                    }
                }
            }
            Text(stringResource(R.string.ob_age_date))
            Text(stringResource(R.string.ob_sex))
            choice("sex", "MALE", R.string.ob_male); choice("sex", "FEMALE", R.string.ob_female); choice("sex", "", R.string.ob_skip)
            Text(stringResource(R.string.ob_boundary))
        }
        2 -> {
            choice("activity", "sedentary", R.string.ob_sedentary)
            choice("activity", "some", R.string.ob_some); choice("activity", "active", R.string.ob_active)
            listOf("pregnant" to R.string.ob_pregnant, "breastfeeding" to R.string.ob_breastfeeding,
                "eating_risk" to R.string.ob_eating_risk, "medical_diet" to R.string.ob_medical_diet,
                "kidney" to R.string.ob_kidney, "treatment" to R.string.ob_treatment).forEach { (key, label) ->
                QidaCard { Text(stringResource(label)); choice(key, "yes", R.string.ob_yes)
                    choice(key, "no", R.string.ob_no); choice(key, "", R.string.ob_skip) }
            }
            Text(stringResource(R.string.ob_boundary))
        }
        3 -> {
            listOf("milk" to R.string.ob_milk, "egg" to R.string.ob_egg, "tree_nuts" to R.string.ob_nuts,
                "fish" to R.string.ob_fish, "wheat" to R.string.ob_wheat, "none" to R.string.ob_none).forEach { (code, label) ->
                OnboardingChoice(stringResource(label), code in d.items("allergies")) {
                    update(d.toggleAllergen(code).let { if (code == "none") it.change("extra_allergies", "") else it })
                }
            }
            OnboardingChoice(stringResource(R.string.ob_skip), d.answer("allergies") == "unknown") {
                update(d.change("allergies", "unknown").change("extra_allergies", ""))
            }
            QidaField(stringResource(R.string.ob_extra_allergies), d.answer("extra_allergies"), {
                update(d.change("extra_allergies", it).let { v -> if (d.answer("allergies") in listOf("none", "unknown")) v.change("allergies", "") else v })
            })
            OnboardingChoice(stringResource(R.string.ob_gluten), "gluten" in d.items("restrictions")) {
                val values = d.items("restrictions")
                change("restrictions", (if ("gluten" in values) values - "gluten" else values + "gluten").joinToString(";"))
            }
            field("restrictions", R.string.ob_restrictions)
            var dislikesOpen by remember { mutableStateOf(false) }
            QidaButton(stringResource(R.string.ob_dislikes), { dislikesOpen = !dislikesOpen })
            if (dislikesOpen) { field("disliked_foods", R.string.ob_disliked_foods); field("disliked_recipes", R.string.ob_disliked_recipes) }
        }
        4 -> {
            Text(stringResource(R.string.ob_time))
            listOf("15", "30", "60").forEach { value -> OnboardingChoice(value, d.answer("time") == value) { change("time", value) } }
            Text(stringResource(R.string.ob_meals))
            listOf("3", "4", "5").forEach { value -> OnboardingChoice(value, d.answer("meals") == value) { change("meals", value) } }
            val slots = when (d.answer("meals")) {
                "3" -> R.string.ob_slots_3; "4" -> R.string.ob_slots_4; "5" -> R.string.ob_slots_5; else -> null
            }
            slots?.let { Text(stringResource(it)) }
            Text(stringResource(R.string.ob_tier))
            choice("tier", "ECONOMICAL", R.string.ob_economical); choice("tier", "MEDIUM", R.string.ob_medium); choice("tier", "FLEXIBLE", R.string.ob_flexible)
            field("region", R.string.ob_region); field("currency", R.string.ob_currency)
            field("amount", R.string.ob_amount, true); field("people", R.string.ob_people, true)
            field("coverage", R.string.ob_coverage); field("equipment", R.string.ob_equipment)
            field("home_name", R.string.ob_home_name); field("home_quantity", R.string.ob_home_quantity, true)
            field("home_unit", R.string.ob_home_unit); field("home_state", R.string.ob_home_state)
            d.storedHomeFoods.forEachIndexed { index, food ->
                QidaCard {
                    Text(food.food.id.removePrefix("user:") + " · ${food.quantity} ${food.unit} · ${food.preparationState}")
                    QidaButton(stringResource(R.string.ob_remove), { update(d.copy(storedHomeFoods = d.storedHomeFoods.filterIndexed { i, _ -> i != index })) })
                }
            }
            QidaButton(stringResource(R.string.ob_home_add), {
                if (d.answer("home_name").isBlank() || d.number("home_quantity") == null || d.answer("home_unit").isBlank() || d.answer("home_state").isBlank()) invalid = true
                else {
                    val food = com.aladdin.dailyplate.domain.HomeFood(com.aladdin.dailyplate.domain.VersionRef("user:" + d.answer("home_name"), 1),
                        d.answer("home_state"), d.number("home_quantity")!!, d.answer("home_unit"))
                    update(d.copy(storedHomeFoods = d.storedHomeFoods + food, answers = d.answers - setOf("home_name", "home_quantity", "home_unit", "home_state")))
                }
            })
            Text(stringResource(R.string.ob_budget_note))
        }
    }
    Button(onClick = {
        if (busy) return@Button
        if (d.errors(d.step).isNotEmpty()) { invalid = true; return@Button }
        if (d.step < 4) update(d.copy(step = d.step + 1)) else {
            busy = true
            scope.launch {
                try { confirmed = withContext(Dispatchers.IO) { store.queueSave(d).get(); store.confirm(d, language) }; writeError = false }
                catch (_: Exception) { writeError = true }
                finally { busy = false }
            }
        }
    }, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget)) {
        Text(stringResource(if (busy) R.string.loading else if (d.step == 4) R.string.ob_prepare else R.string.continue_action))
    }
}

@Composable
private fun OnboardingChoice(label: String, selected: Boolean, action: () -> Unit) {
    FilterChip(selected = selected, onClick = action, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget),
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary),
        label = { Text((if (selected) "✓ " else "") + label, style = MaterialTheme.typography.bodyLarge) })
}
