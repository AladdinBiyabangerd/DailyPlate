package com.aladdin.dailyplate.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.*
import com.aladdin.dailyplate.domain.profile.*
import com.aladdin.dailyplate.ui.*
import com.aladdin.dailyplate.ui.onboarding.ProfileAnswerFields
import java.time.*

@Composable
fun ProfileEditor(base: UserProfile, startStep: Int, busy: Boolean, error: Boolean,
                  cancel: () -> Unit, confirm: (ProfileEditPreview) -> Unit) {
    var draft by remember(base.ref) { mutableStateOf(ProfileEdit.draft(base).copy(step = startStep)) }
    var specialist by remember(base.ref) { mutableStateOf(base.specialistTargetKcal?.toString().orEmpty()) }
    var ageConfirmed by remember { mutableStateOf(false) }
    var reason by remember { mutableStateOf("") }
    var invalid by remember { mutableStateOf(false) }
    var preview by remember { mutableStateOf<ProfileEditPreview?>(null) }
    BackHandler(!busy) { if (preview != null) preview = null else cancel() }
    if (busy) { QidaState(SurfaceState.Loading); return }
    if (error) QidaState(SurfaceState.Error, stringResource(R.string.pf_conflict))
    val p = preview
    if (p != null) {
        QidaHeading(stringResource(R.string.pf_preview))
        QidaCard {
            Text(stringResource(R.string.pf_versions, p.before.ref.version, p.after.ref.version))
            Text(stringResource(R.string.pf_reason) + ": " + p.request.reason)
            p.request.changedFields.sorted().forEach { key ->
                Text(profileFieldLabel(key) + ": " + profileAnswer(key, ProfileEdit.draft(p.before).answer(key), p.before) +
                    " → " + profileAnswer(key, ProfileEdit.draft(p.after).answer(key), p.after))
            }
        }
        TargetExplanation(p.after, p.assessment)
        QidaCard { Text(stringResource(R.string.pf_future)) }
        QidaButton(stringResource(R.string.pf_confirm), { confirm(p) })
        QidaButton(stringResource(R.string.pf_continue_edit), { preview = null })
        QidaButton(stringResource(R.string.pf_cancel), cancel)
        return
    }
    QidaHeading(stringResource(R.string.pf_edit))
    Text(stringResource(R.string.pf_unsaved))
    if (invalid) Text(stringResource(R.string.pf_invalid))
    val headings = listOf(R.string.ob_goal, R.string.ob_body, R.string.ob_activity, R.string.ob_allergies, R.string.ob_preferences)
    headings.forEachIndexed { step, label ->
        TextButton(onClick = { draft = draft.copy(step = step); invalid = false }) { Text(stringResource(label)) }
    }
    QidaCard {
        Text(stringResource(headings[draft.step]), style = MaterialTheme.typography.titleLarge)
        ProfileAnswerFields(draft, invalid) { draft = it; invalid = false }
    }
    if (draft.step == 1) {
        Text(stringResource(R.string.pf_age_date, base.ageAsOfDate?.iso ?: "—"))
        QidaButton(stringResource(if (ageConfirmed) R.string.pf_age_confirmed else R.string.pf_age_confirm), { ageConfirmed = !ageConfirmed })
    }
    QidaCard {
        Text(stringResource(R.string.pf_specialist_note))
        QidaField(stringResource(R.string.pf_specialist), specialist, { specialist = it; invalid = false }, numeric = true)
        QidaField(stringResource(R.string.pf_reason), reason, { reason = it; invalid = false })
    }
    QidaButton(stringResource(R.string.pf_preview), {
        try {
            val zone = ZoneId.systemDefault()
            val now = Instant.now()
            preview = ProfileEdit.preview(base, draft, specialist, ageConfirmed, reason,
                RecordedAt(now.toString(), zone.id), CalendarDate(now.atZone(zone).toLocalDate().toString()))
        } catch (_: IllegalArgumentException) { invalid = true }
    })
    QidaButton(stringResource(R.string.pf_cancel), cancel)
}
