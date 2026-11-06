package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aladdin.dailyplate.R

@Composable internal fun TabletReview(state: PreviewState) {
    val missing = state.missing()
    val wide = LocalTabletLayout.current!!.wide && missing.isNotEmpty()
    TabletFrame(state, R.string.dp_review, if (wide) 1120.dp else 704.dp) {
        val overview: @Composable () -> Unit = {
            Column {
                if (wide) {
                    TabletArt(R.drawable.dp_art_write_error, 96.dp); Gap(16.dp)
                    Label(copy(R.string.dp_missing_title), size = 26, bold = true)
                } else Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TabletArt(R.drawable.dp_art_write_error, 96.dp)
                    Label(copy(if (missing.isEmpty()) R.string.dp_complete_title else R.string.dp_missing_title), size = 26, bold = true, modifier = Modifier.weight(1f))
                }
                Gap(16.dp); Label(copy(if (missing.isEmpty()) R.string.dp_complete_summary else R.string.dp_missing_summary), muted = true)
                if (wide) TabletReviewActions(state)
            }
        }
        val details: @Composable () -> Unit = {
            val groups = reviewGroups.filter { state.reviewAll || it.first in missing }
            TabletGrid(groups, 2, 16.dp) { (page, title) ->
                Panel(Modifier.fillMaxHeight(), onClick = { state.edit(page) }) {
                    Row(Modifier.heightIn(min = if (wide) 48.dp else 80.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(Modifier.weight(1f)) {
                            Label(copy(title), size = 20, bold = true); Gap(8.dp)
                            Label(if (page in missing && !state.reviewAll) missingSummary(state, page) else groupSummary(state, page), muted = true)
                        }; Label("›", size = 26, bold = true)
                    }
                }
            }
        }
        if (wide) TabletSplit(overview, details, gap = 32.dp, leftWidth = 320.dp, topAligned = true)
        else { overview(); Gap(24.dp); details(); TabletReviewActions(state) }
    }
}
@Composable private fun TabletReviewActions(state: PreviewState) {
    val complete = state.missing().isEmpty()
    Gap(24.dp); Notice(if (complete) R.string.dp_complete_note else R.string.dp_missing_note)
    TabletFooter(state, if (complete) R.string.dp_next else R.string.dp_complete_answers,
        secondary = R.string.dp_diary_continue, secondaryAction = { state.forcedDiary = true; state.go(PreviewPage.Account) }) {
        if (complete) { state.forcedDiary = false; state.go(PreviewPage.Account) } else state.edit(state.missing().first())
    }
    Secondary(R.string.dp_recheck) { state.reviewAll = false; state.go(PreviewPage.Review) }
}
