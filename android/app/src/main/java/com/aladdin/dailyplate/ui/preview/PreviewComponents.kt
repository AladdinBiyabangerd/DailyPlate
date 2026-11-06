package com.aladdin.dailyplate.ui.preview

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aladdin.dailyplate.R

/** Dimensions follow approved HTML: 8/16/24/32 spacing; 24 card, 16 button radius. */
internal object PreviewStyle {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val radius = RoundedCornerShape(24.dp)
    val button = RoundedCornerShape(16.dp)
}
@Composable internal fun copy(@StringRes id: Int, vararg args: Any) = stringResource(id, *args)
@Composable internal fun Art(@DrawableRes id: Int, width: Dp, height: Dp, modifier: Modifier = Modifier) {
    Image(painterResource(id), contentDescription = null, contentScale = ContentScale.Fit, modifier = modifier.size(width, height))
}
@Composable internal fun Art(@DrawableRes id: Int, width: Dp, modifier: Modifier = Modifier) = Art(id, width, width, modifier)
@Composable internal fun Label(text: String, modifier: Modifier = Modifier, size: Int = 16, bold: Boolean = false, muted: Boolean = false, center: Boolean = false) {
    Text(text, modifier, color = if (muted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
        fontSize = size.sp, lineHeight = (size * 1.35).sp, fontWeight = if (bold || LocalTabletLayout.current != null) FontWeight.Bold else FontWeight.Normal,
        textAlign = if (center) TextAlign.Center else TextAlign.Start)
}
@Composable internal fun Gap(size: Dp = PreviewStyle.medium) = Spacer(Modifier.height(size))
@Composable internal fun Panel(modifier: Modifier = Modifier, selected: Boolean = false, tint: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null, contentPadding: Dp = 16.dp, content: @Composable ColumnScope.() -> Unit) {
    val border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    if (onClick == null) Surface(modifier.fillMaxWidth(), shape = PreviewStyle.radius, color = tint, border = border, shadowElevation = if (LocalTabletLayout.current != null) 8.dp else 2.dp) {
        Column(Modifier.padding(contentPadding), content = content)
    } else Surface(onClick, modifier.fillMaxWidth(), shape = PreviewStyle.radius, color = tint, border = border, shadowElevation = if (LocalTabletLayout.current != null) 8.dp else 2.dp) {
        Column(Modifier.padding(contentPadding), content = content)
    }
}
@Composable internal fun Primary(@StringRes label: Int, enabled: Boolean = true, arrow: Boolean = true, onClick: () -> Unit) {
    Button(onClick, Modifier.fillMaxWidth().heightIn(min = if (LocalTabletLayout.current != null) 56.dp else 48.dp), enabled = enabled, shape = PreviewStyle.button,
        border = if (!enabled) BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant) else null,
        contentPadding = PaddingValues(16.dp, 8.dp),
        colors = ButtonDefaults.buttonColors(disabledContainerColor = MaterialTheme.colorScheme.surface, disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant)) {
        Text(copy(label), fontSize = 20.sp, lineHeight = 25.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f, false))
        if (arrow) { Spacer(Modifier.width(16.dp)); Text("➜", fontSize = 26.sp) }
    }
}
@Composable internal fun Secondary(@StringRes label: Int, onClick: () -> Unit) {
    TextButton(onClick, Modifier.fillMaxWidth().heightIn(min = if (LocalTabletLayout.current != null) 56.dp else 48.dp), contentPadding = PaddingValues(8.dp)) {
        Text(copy(label), fontSize = 16.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
    }
}
@Composable internal fun Notice(@StringRes id: Int, yellow: Boolean = false) {
    Surface(color = if (yellow) androidx.compose.ui.res.colorResource(R.color.simple_yellow) else androidx.compose.ui.res.colorResource(R.color.simple_peach), shape = PreviewStyle.button) {
        Label(copy(id), modifier = Modifier.fillMaxWidth().padding(16.dp))
    }
}
@Composable internal fun Header(@StringRes title: Int, state: PreviewState, welcome: Boolean = false) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        val backLabel = copy(R.string.dp_back)
        if (!welcome) Surface(onClick = state::back, modifier = Modifier.semantics { contentDescription = backLabel }, shape = PreviewStyle.button, shadowElevation = if (LocalTabletLayout.current != null) 8.dp else 2.dp) {
            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { Text("‹", fontSize = 32.sp, modifier = Modifier, color = MaterialTheme.colorScheme.onSurface) }
        }
        Text(copy(title), fontSize = if (LocalTabletLayout.current != null) { when (state.page) { PreviewPage.Welcome, PreviewPage.Goal, PreviewPage.Body -> 32.sp; PreviewPage.Review -> 24.sp; else -> 26.sp } } else if (welcome) 32.sp else 26.sp, lineHeight = if (LocalTabletLayout.current != null && state.page in listOf(PreviewPage.Welcome, PreviewPage.Goal, PreviewPage.Body)) 40.sp else 30.sp, fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        if (welcome) Surface(onClick = { state.dialog = PreviewDialog.Language }, shape = PreviewStyle.radius, shadowElevation = if (LocalTabletLayout.current != null) 8.dp else 2.dp) {
            Box(Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 48.dp).padding(8.dp), contentAlignment = Alignment.Center) {
                val language = when(state.language) { "ru" -> "RU⌄"; "en" -> "EN⌄"; else -> "🇦🇿 AZ⌄" }
                if (LocalTabletLayout.current != null) Text(language, Modifier.widthIn(min = 88.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 32.sp)
                else Text(language, fontWeight = FontWeight.Bold)
            }
        }
    }
}
@Composable internal fun Frame(state: PreviewState, @StringRes title: Int, content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val scroll = rememberScrollState()
        val minimum = maxHeight
        LaunchedEffect(state.error) { if (state.error != 0) scroll.animateScrollTo(scroll.maxValue) }
        Column(Modifier.fillMaxWidth().verticalScroll(scroll).heightIn(min = minimum).padding(24.dp)) {
            Header(title, state, state.page == PreviewPage.Welcome)
            content()
        }
    }
}
@Composable internal fun ColumnScope.Footer(state: PreviewState, @StringRes label: Int = R.string.dp_next, enabled: Boolean = true,
    secondary: Int? = null, secondaryAction: (() -> Unit)? = null, arrow: Boolean = true, onClick: () -> Unit) {
    Spacer(Modifier.weight(1f))
    Gap(24.dp)
    if (state.error != 0) { Notice(state.error); Gap() }
    Primary(label, enabled, arrow = arrow, onClick = onClick)
    if (secondary != null) Secondary(secondary, secondaryAction ?: {})
}
@Composable internal fun ChoiceCard(@StringRes title: Int, @StringRes subtitle: Int, @DrawableRes art: Int, selected: Boolean, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().selectable(selected, role = Role.RadioButton, onClick = onClick), shape = PreviewStyle.radius,
        color = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null, shadowElevation = if (LocalTabletLayout.current != null) 8.dp else 2.dp) {
        Box {
            if (LocalDensity.current.fontScale > 1.15f) {
                // Accessibility reflow: keep long translated words out of a narrow side column.
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Art(art, 64.dp); Gap(8.dp)
                    Label(copy(title), size = 20, bold = true); Gap(8.dp); Label(copy(subtitle), muted = true)
                }
            } else {
                Row(Modifier.fillMaxWidth().heightIn(min = 104.dp).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Art(art, 80.dp)
                    Column(Modifier.weight(1f)) { Label(copy(title), size = 20, bold = true); Gap(8.dp); Label(copy(subtitle), muted = true) }
                }
            }
            if (selected) Text("✓", Modifier.align(Alignment.TopEnd).padding(8.dp).clip(PreviewStyle.radius).background(MaterialTheme.colorScheme.primary).padding(horizontal = 6.dp), color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
@Composable internal fun RadioChoice(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(modifier.selectable(selected, role = Role.RadioButton, onClick = onClick), shape = PreviewStyle.button,
        color = MaterialTheme.colorScheme.background, border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null) {
        Row(Modifier.heightIn(min = 48.dp).padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            RadioButton(selected, null, modifier = Modifier.size(24.dp)); Spacer(Modifier.width(8.dp))
            Text(text, fontSize = 16.sp, modifier = Modifier.weight(1f, false))
        }
    }
}
