package com.aladdin.dailyplate.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
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
        QidaArt(R.drawable.art_saved, Modifier.fillMaxWidth().height(QidaDimensions.heroArt))
        QidaHeading(stringResource(R.string.ob_saved))
        val profile = requireNotNull(confirmed)
        Text(stringResource(when (profile.goal) {
            com.aladdin.dailyplate.domain.Goal.LOSE -> R.string.ob_lose
            com.aladdin.dailyplate.domain.Goal.MAINTAIN -> R.string.ob_maintain
            com.aladdin.dailyplate.domain.Goal.GAIN -> R.string.ob_gain
            com.aladdin.dailyplate.domain.Goal.HEALTHY -> R.string.ob_healthy
        }))
        Text(stringResource(R.string.ob_meals) + ": " + profile.mealsPerDay)
        QidaCard { Text(stringResource(R.string.ob_boundary)); Text(stringResource(R.string.ob_local_note)) }
        QidaButton(stringResource(R.string.continue_action), done)
        return
    }
    BackHandler(d.step > 0 && !busy) { update(d.copy(step = d.step - 1)) }
    val windowHeight = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
    val safePadding = WindowInsets.safeDrawing.asPaddingValues()
    Column(Modifier.fillMaxWidth().heightIn(min = windowHeight - safePadding.calculateTopPadding() - safePadding.calculateBottomPadding() - QidaSpacing.large * 2),
        verticalArrangement = Arrangement.SpaceBetween) {
    Column(verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium)) {
    StepHeader(d.step) { if (d.step == 0) exit() else update(d.copy(step = d.step - 1)) }
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
        0 -> {
            val goals = listOf(Triple("LOSE", R.string.ob_lose, R.drawable.art_lose),
                Triple("MAINTAIN", R.string.ob_maintain, R.drawable.art_maintain),
                Triple("GAIN", R.string.ob_gain, R.drawable.art_gain),
                Triple("HEALTHY", R.string.ob_healthy, R.drawable.art_healthy))
            DesignGrid(goals.map { (value, label, art) -> value to { modifier ->
                DesignChoice(stringResource(label), d.answer("goal") == value, { change("goal", value) },
                    modifier, art, tile = true)
            } })
        }
        1 -> {
            listOf(Triple("age", R.string.ob_age, R.drawable.art_age),
                Triple("height", R.string.ob_height, R.drawable.art_height),
                Triple("weight", R.string.ob_weight, R.drawable.art_weight)).forEach { (key, label, art) ->
                MeasurementCard(stringResource(label), d.answer(key), art,
                    if (invalid && key in d.errors(d.step)) stringResource(R.string.ob_invalid) else null,
                    { change(key, it) }, { delta -> change(key, ((d.number(key) ?: 0.0) + delta).toString()) })
            }
            Text(stringResource(R.string.ob_age_date))
            Text(stringResource(R.string.ob_sex))
            DesignGrid(listOf("MALE" to { modifier ->
                DesignChoice(stringResource(R.string.ob_male), d.answer("sex") == "MALE", { change("sex", "MALE") }, modifier, R.drawable.art_male, true)
            }, "FEMALE" to { modifier ->
                DesignChoice(stringResource(R.string.ob_female), d.answer("sex") == "FEMALE", { change("sex", "FEMALE") }, modifier, R.drawable.art_female, true)
            }))
            choice("sex", "", R.string.ob_skip)
            Text(stringResource(R.string.ob_boundary))
        }
        2 -> {
            listOf(Triple("sedentary", R.string.ob_sedentary, R.drawable.art_sedentary),
                Triple("some", R.string.ob_some, R.drawable.art_walking),
                Triple("active", R.string.ob_active, R.drawable.art_active)).forEach { (value, label, art) ->
                DesignChoice(stringResource(label), d.answer("activity") == value, { change("activity", value) }, art = art)
            }
            QidaArt(R.drawable.art_health, Modifier.fillMaxWidth().height(QidaDimensions.heroArt))
            listOf("pregnant" to R.string.ob_pregnant, "breastfeeding" to R.string.ob_breastfeeding,
                "eating_risk" to R.string.ob_eating_risk, "medical_diet" to R.string.ob_medical_diet,
                "kidney" to R.string.ob_kidney, "treatment" to R.string.ob_treatment)
                .filter { (key, _) -> d.asksReproductiveHealth || key !in setOf("pregnant", "breastfeeding") }
                .forEach { (key, label) ->
                ChoiceGroup(stringResource(label)) {
                    SegmentedChoices(listOf("yes" to stringResource(R.string.ob_yes),
                        "no" to stringResource(R.string.ob_no), "" to stringResource(R.string.ob_skip)), d.answer(key)) { change(key, it) }
                }
            }
            Text(stringResource(R.string.ob_boundary))
        }
        3 -> {
            val allergens = listOf(Triple("milk", R.string.ob_milk, R.drawable.art_milk),
                Triple("egg", R.string.ob_egg, R.drawable.art_egg), Triple("tree_nuts", R.string.ob_nuts, R.drawable.art_nuts),
                Triple("fish", R.string.ob_fish, R.drawable.art_fish), Triple("wheat", R.string.ob_wheat, R.drawable.art_wheat))
            DesignGrid(allergens.map { (code, label, art) -> code to { modifier ->
                DesignChoice(stringResource(label), code in d.items("allergies"), { update(d.toggleAllergen(code)) },
                    modifier, art, true)
            } })
            OnboardingChoice(stringResource(R.string.ob_none), "none" in d.items("allergies")) {
                update(d.toggleAllergen("none").change("extra_allergies", ""))
            }
            OnboardingChoice(stringResource(R.string.ob_skip), d.answer("allergies") == "unknown") {
                update(d.change("allergies", "unknown").change("extra_allergies", ""))
            }
            if (d.answer("allergies") != "none") {
                QidaArt(R.drawable.art_other_choices, Modifier.fillMaxWidth().height(QidaDimensions.bannerArt))
                QidaCard {
                QidaField(stringResource(R.string.ob_extra_allergies), d.answer("extra_allergies"), {
                    update(d.change("extra_allergies", it).let { v -> if (d.answer("allergies") in listOf("none", "unknown")) v.change("allergies", "") else v })
                })
                }
            }
            OnboardingChoice(stringResource(R.string.ob_gluten), "gluten" in d.items("restrictions")) {
                val values = d.items("restrictions")
                change("restrictions", (if ("gluten" in values) values - "gluten" else values + "gluten").joinToString(";"))
            }
            QidaCard { field("restrictions", R.string.ob_restrictions) }
            var dislikesOpen by remember { mutableStateOf(false) }
            QidaButton(stringResource(R.string.ob_dislikes), { dislikesOpen = !dislikesOpen })
            if (dislikesOpen) {
                QidaArt(R.drawable.art_dislikes, Modifier.fillMaxWidth().height(QidaDimensions.bannerArt))
                QidaCard { field("disliked_foods", R.string.ob_disliked_foods); field("disliked_recipes", R.string.ob_disliked_recipes) }
            }
        }
        4 -> {
            QidaArt(R.drawable.art_cooking, Modifier.fillMaxWidth().height(QidaDimensions.bannerArt))
            ChoiceGroup(stringResource(R.string.ob_time)) {
                SegmentedChoices(listOf("15", "30", "60").map { it to it }, d.answer("time")) { change("time", it) }
            }
            ChoiceGroup(stringResource(R.string.ob_tier)) {
                SegmentedChoices(listOf("ECONOMICAL" to stringResource(R.string.ob_economical),
                    "MEDIUM" to stringResource(R.string.ob_medium), "FLEXIBLE" to stringResource(R.string.ob_flexible)),
                    d.answer("tier")) { change("tier", it) }
            }
            ChoiceGroup(stringResource(R.string.ob_meals)) {
                SegmentedChoices(listOf("3", "4", "5").map { it to it }, d.answer("meals")) { change("meals", it) }
            }
            val slots = when (d.answer("meals")) {
                "3" -> R.string.ob_slots_3; "4" -> R.string.ob_slots_4; "5" -> R.string.ob_slots_5; else -> null
            }
            slots?.let { Text(stringResource(it)) }
            QidaArt(R.drawable.art_budget, Modifier.fillMaxWidth().height(QidaDimensions.heroArt))
            ChoiceGroup(stringResource(R.string.ob_tier)) {
                field("region", R.string.ob_region); field("currency", R.string.ob_currency)
                field("amount", R.string.ob_amount, true); field("people", R.string.ob_people, true)
            }
            Text(stringResource(R.string.ob_budget_note))
        }
    }
    }
    Spacer(Modifier.height(QidaSpacing.medium))
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
    }, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(QidaDimensions.buttonRadius),
        contentPadding = PaddingValues(QidaSpacing.medium)) {
        Text(stringResource(if (busy) R.string.loading else if (d.step == 4) R.string.ob_prepare else R.string.continue_action))
    }
    }
}

@Composable
private fun OnboardingChoice(label: String, selected: Boolean, action: () -> Unit) {
    DesignChoice(label, selected, action)
}
