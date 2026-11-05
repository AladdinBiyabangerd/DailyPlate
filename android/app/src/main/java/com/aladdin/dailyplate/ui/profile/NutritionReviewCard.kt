package com.aladdin.dailyplate.ui.profile

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.domain.NutritionSuspension
import com.aladdin.dailyplate.domain.profile.NutritionReviewState
import com.aladdin.dailyplate.ui.QidaButton
import com.aladdin.dailyplate.ui.QidaCard

@Composable
internal fun NutritionReviewCard(state: NutritionReviewState, suspended: Boolean, busy: Boolean,
                                 review: () -> Unit, report: (NutritionSuspension) -> Unit) {
    QidaCard {
        if (suspended) {
            Text(stringResource(R.string.nr_suspended))
            Text(stringResource(R.string.pf_specialist_needed))
            Text(stringResource(R.string.nr_support))
        } else {
            if (state.due) {
                Text(stringResource(R.string.pf_review_due))
                Text(stringResource(R.string.nr_days, state.measurementCount))
                val change = state.observedChangeKg
                Text(if (change == null) stringResource(R.string.nr_insufficient)
                    else stringResource(R.string.nr_observed, java.text.DecimalFormat("0.##").format(change)))
                Text(stringResource(R.string.nr_review_note))
                if (!busy) QidaButton(stringResource(R.string.nr_review), review)
            }
            Text(stringResource(R.string.nr_report_note))
            if (!busy) {
                QidaButton(stringResource(R.string.nr_symptom), { report(NutritionSuspension.NEW_SYMPTOM) })
                QidaButton(stringResource(R.string.nr_unexplained), { report(NutritionSuspension.UNEXPLAINED_WEIGHT_CHANGE) })
            }
        }
    }
}
