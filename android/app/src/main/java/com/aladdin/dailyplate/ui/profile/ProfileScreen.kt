package com.aladdin.dailyplate.ui.profile

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import com.aladdin.dailyplate.ui.theme.QidaSpacing
import com.aladdin.dailyplate.ui.theme.QidaDimensions
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.data.local.*
import com.aladdin.dailyplate.domain.UserProfile
import com.aladdin.dailyplate.domain.policy.NutritionAssessmentRecord
import com.aladdin.dailyplate.domain.WeightEntry
import com.aladdin.dailyplate.domain.RecordedAt
import com.aladdin.dailyplate.domain.profile.NutritionReview
import com.aladdin.dailyplate.domain.profile.ProfileChangeRecord
import com.aladdin.dailyplate.ui.*
import kotlinx.coroutines.*

@Composable
fun ProfileScreen(resetScroll: () -> Unit, setup: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var history by remember { mutableStateOf<List<Pair<UserProfile, NutritionAssessmentRecord?>>>(emptyList()) }
    var changes by remember { mutableStateOf<List<ProfileChangeRecord>>(emptyList()) }
    var weights by remember { mutableStateOf<List<WeightEntry>>(emptyList()) }
    var reviewWeight by remember { mutableStateOf<Double?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Int?>(null) }
    var explain by remember { mutableStateOf(false) }
    var refresh by remember { mutableIntStateOf(0) }
    LaunchedEffect(editing, explain) { resetScroll() }
    LaunchedEffect(refresh) {
        try {
            val id = withContext(Dispatchers.IO) { OnboardingStore(context).load().id }
            val data = withContext(Dispatchers.IO) {
                UserRepositories.open(context).use { r ->
                    r.profiles.history("local-guest", id).map { it to r.nutritionAssessments.version(it.userId, it.ref) } to
                        r.profileChanges.history("local-guest", id)
                }
            }
            weights = withContext(Dispatchers.IO) {
                UserRepositories.open(context).use { it.weights.current("local-guest") }
            }
            history = data.first; changes = data.second; profile = history.firstOrNull()?.first
            loaded = true; error = false
        } catch (_: Exception) { error = true }
    }
    if (!loaded) {
        QidaState(if (error) SurfaceState.Error else SurfaceState.Loading) { refresh++ }; return
    }
    val p = profile
    if (p == null) {
        QidaState(SurfaceState.Empty, stringResource(R.string.empty_profile))
        QidaButton(stringResource(R.string.ob_profile), setup); return
    }
    val step = editing
    if (step != null) {
        ProfileEditor(p, step, busy, error, resetScroll, { editing = null; reviewWeight = null; error = false; refresh++ }, initialWeight = reviewWeight) { preview ->
            busy = true
            scope.launch {
                try {
                    withContext(Dispatchers.IO) { UserRepositories.open(context).use { it.profileEdits.confirm(preview) } }
                    editing = null; explain = true; refresh++
                } catch (_: Exception) { error = true }
                finally { busy = false }
            }
        }
        return
    }
    if (error) QidaState(SurfaceState.Error, stringResource(R.string.ob_write_error)) { refresh++ }
    Text(stringResource(R.string.pf_version, p.ref.version))
    ProfileAction(stringResource(R.string.ob_profile)) { reviewWeight = null; editing = 1 }
    ProfileAction(stringResource(R.string.ob_goal)) { reviewWeight = null; editing = 0 }
    ProfileAction(stringResource(R.string.pf_food_choices)) { reviewWeight = null; editing = 3 }
    QidaButton(stringResource(R.string.pf_basis), { explain = !explain })
    val zone = java.time.ZoneId.systemDefault()
    val now = RecordedAt(java.time.Instant.now().toString(), zone.id)
    val review = NutritionReview.evaluate(p, history.firstOrNull()?.second?.assessment, weights, now)
    NutritionReviewCard(review, p.nutritionSuspension != null, busy, {
        reviewWeight = review.latestWeightKg
        editing = 1
    }) { reason ->
        busy = true
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    UserRepositories.open(context).use {
                        it.profileEdits.confirm(NutritionReview.suspension(p, reason,
                            RecordedAt(java.time.Instant.now().toString(), zone.id)))
                    }
                }
                refresh++
            } catch (_: Exception) { error = true }
            finally { busy = false }
        }
    }
    QidaCard {
        val current = history.firstOrNull()?.second?.assessment
        Text(stringResource(if (current?.path == com.aladdin.dailyplate.domain.policy.TargetPath.AUTOMATIC)
            R.string.pf_current_target else R.string.pf_general))
        Text(stringResource(R.string.pf_future))
    }
    if (explain) history.forEach { (version, assessment) ->
        changes.firstOrNull { it.ref == version.ref }?.let { QidaCard { Text(stringResource(R.string.pf_reason) + ": " + it.request.reason) } }
        TargetExplanation(version, assessment?.assessment)
    }
}

@Composable
private fun ProfileAction(label: String, action: () -> Unit) {
    TextButton(onClick = action, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget),
        shape = MaterialTheme.shapes.large, contentPadding = PaddingValues(QidaSpacing.medium),
        colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface)) {
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}
