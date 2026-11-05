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
    var preview by remember { mutableStateOf<com.aladdin.dailyplate.domain.onboarding.OnboardingPreview?>(null) }
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
    LaunchedEffect(d.step, saved, preview != null) { resetScroll() }
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
    val review = preview
    if (review != null) {
        BackHandler { preview = null }
        QidaHeading(stringResource(R.string.ob_review_target))
        com.aladdin.dailyplate.ui.profile.TargetExplanation(review.profile, review.assessment)
        QidaButton(stringResource(R.string.pf_confirm), {
            busy = true
            scope.launch {
                try { confirmed = withContext(Dispatchers.IO) { store.confirm(review) }; preview = null; writeError = false }
                catch (_: Exception) { writeError = true }
                finally { busy = false }
            }
        })
        QidaButton(stringResource(R.string.pf_continue_edit), { preview = null })
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
    ProfileAnswerFields(d, invalid, ::update)
    }
    Spacer(Modifier.height(QidaSpacing.medium))
    Button(onClick = {
        if (busy) return@Button
        if (d.errors(d.step).isNotEmpty()) { invalid = true; return@Button }
        if (d.step < 4) update(d.copy(step = d.step + 1)) else {
            val zone = java.time.ZoneId.systemDefault()
            val now = java.time.Instant.now()
            preview = com.aladdin.dailyplate.domain.onboarding.OnboardingPreview.create(d, language,
                com.aladdin.dailyplate.domain.RecordedAt(now.toString(), zone.id),
                com.aladdin.dailyplate.domain.CalendarDate(now.atZone(zone).toLocalDate().toString()))
        }
    }, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(QidaDimensions.buttonRadius),
        contentPadding = PaddingValues(QidaSpacing.medium)) {
        Text(stringResource(if (busy) R.string.loading else if (d.step == 4) R.string.ob_prepare else R.string.continue_action))
    }
    }
}
