package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aladdin.dailyplate.R

/** Evaluated from the usable window before IME padding, not the physical device type. */
internal data class TabletLayout(val landscape: Boolean, val expanded: Boolean) { val wide get() = landscape && expanded }
internal val LocalTabletLayout = staticCompositionLocalOf<TabletLayout?> { null }
@Composable internal fun TabletOnboarding(state: PreviewState) {
    when (state.page) {
        PreviewPage.Welcome -> TabletWelcome(state)
        PreviewPage.Goal -> TabletGoal(state)
        PreviewPage.Body -> TabletBody(state)
        PreviewPage.Activity -> TabletActivity(state)
        PreviewPage.Health1 -> TabletHealth(state, false)
        PreviewPage.Health2 -> TabletHealth(state, true)
        PreviewPage.Warning -> TabletWarning(state)
        PreviewPage.Allergy -> TabletAllergy(state)
        PreviewPage.Preferences -> TabletPreferences(state)
        PreviewPage.Review -> TabletReview(state)
        PreviewPage.Account -> TabletAccount(state)
        PreviewPage.Ready -> TabletReady(state)
        PreviewPage.Today -> TabletToday(state)
    }
}
/** Reference constraints are dp at mdpi, never a scaled screenshot canvas. Height can grow. */
@Composable internal fun TabletFrame(state: PreviewState, title: Int, width: Dp = 704.dp,
    centered: Boolean = false, top: Dp = 32.dp, content: @Composable ColumnScope.() -> Unit) {
    val landscape = LocalTabletLayout.current!!.landscape
    BoxWithConstraints(Modifier.fillMaxSize().testTag(if (landscape) "tablet-landscape" else "tablet-portrait")) {
        val scroll = rememberScrollState()
        val minimum = maxHeight
        val contentMinimum = (minimum - 152.dp).coerceAtLeast(0.dp)
        LaunchedEffect(state.error) { if (state.error != 0) scroll.animateScrollTo(scroll.maxValue) }
        Column(Modifier.fillMaxWidth().verticalScroll(scroll).heightIn(min = minimum)
            .background(Brush.radialGradient(listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.background), radius = 1100f))
            .padding(start = 48.dp, end = 48.dp, top = 48.dp, bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.widthIn(max = 1120.dp).fillMaxWidth()) { Header(title, state, state.page == PreviewPage.Welcome) }
            Column(Modifier.widthIn(max = width).fillMaxWidth().padding(top = top)
                .heightIn(min = if (centered) contentMinimum else 0.dp),
                verticalArrangement = if (centered) Arrangement.Center else Arrangement.Top, content = content)
        }
    }
}
@Composable internal fun TabletFooter(state: PreviewState, label: Int = R.string.dp_next, enabled: Boolean = true, showError: Boolean = true,
    secondary: Int? = null, secondaryAction: (() -> Unit)? = null, arrow: Boolean = true, topGap: Dp = 24.dp, onClick: () -> Unit) {
    Gap(topGap)
    if (showError && state.error != 0) { Notice(state.error); Gap(16.dp) }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.widthIn(max = 488.dp).fillMaxWidth()) {
            Primary(label, enabled, arrow, onClick)
            if (secondary != null) Secondary(secondary, secondaryAction ?: {})
        }
    }
}
@Composable internal fun TabletSplit(left: @Composable () -> Unit, right: @Composable () -> Unit,
    gap: Dp = 64.dp, leftWidth: Dp? = null, topAligned: Boolean = false) {
    if (LocalTabletLayout.current!!.wide) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap), verticalAlignment = if (topAligned) Alignment.Top else Alignment.CenterVertically) {
        Box(if (leftWidth == null) Modifier.weight(1f) else Modifier.width(leftWidth)) { left() }
        Box(Modifier.weight(1f)) { right() }
    } else { left(); Gap(32.dp); right() }
}
/** Finite-width rows grow vertically for translations and 150% text; no lazy nested scrolling. */
@Composable internal fun <T> TabletGrid(items: List<T>, columns: Int, gap: Dp = 24.dp,
    centeredLast: Boolean = false, content: @Composable (T) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
        items.chunked(columns).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                if (centeredLast && row.size == 1 && columns == 2) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Box(Modifier.fillMaxWidth(.5f).padding(horizontal = gap / 4)) { content(row.single()) }
                    }
                } else {
                    row.forEach { item -> Box(Modifier.weight(1f).fillMaxHeight()) { content(item) } }
                    repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
