package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aladdin.dailyplate.R

@Composable internal fun TabletToday(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    Column(Modifier.fillMaxSize().testTag(if (LocalTabletLayout.current!!.landscape) "tablet-landscape" else "tablet-portrait").padding(start = 48.dp, end = 48.dp, top = 48.dp, bottom = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.widthIn(max = 1120.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Label(copy(R.string.dp_today), muted = true); Gap(8.dp); Label(copy(R.string.dp_hello), size = 28, bold = true) }
            Surface(onClick = { state.future(R.string.dp_profile) }, shape = PreviewStyle.radius, color = androidx.compose.ui.res.colorResource(R.color.simple_peach)) {
                Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { Label("☺︎", size = 26) }
            }
        }
        Gap(16.dp); Label(copy(if (state.diary) R.string.dp_diary_notice else R.string.dp_demo_badge), Modifier.widthIn(max = 1120.dp).fillMaxWidth(), muted = true)
        BoxWithConstraints(Modifier.weight(1f).widthIn(max = 1120.dp).fillMaxWidth()) {
            val minimum = maxHeight
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = minimum).padding(vertical = 16.dp), verticalArrangement = Arrangement.Center) {
                if (state.diary) TabletDiary(state) else if (wide) {
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        Box(Modifier.weight(1f).fillMaxHeight()) { TabletMealHero(state) }
                        Box(Modifier.weight(1f).fillMaxHeight()) { TabletEnergy(state) }
                    }
                } else { TabletMealHero(state); Gap(24.dp); TabletEnergy(state) }
            }
        }
        Gap(8.dp); Label(copy(R.string.dp_intake_note), Modifier.widthIn(max = 1120.dp).fillMaxWidth(), muted = true); Gap(24.dp)
        TabletNavigation(state)
    }
}
@Composable private fun TabletMealHero(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    Box(Modifier.fillMaxWidth().fillMaxHeight().clip(PreviewStyle.radius).background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, lerp(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onSurface, .1f))))) {
        Box(Modifier.align(Alignment.TopEnd).offset(y = if (wide) 32.dp else 24.dp).size(if (wide) 256.dp else 320.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface.copy(alpha = .12f))) {
            TabletArt(R.drawable.dp_art_recipe, if (wide) 256.dp else 320.dp)
        }
        Column(Modifier.fillMaxWidth().heightIn(min = if (wide) 352.dp else 400.dp).padding(if (wide) 24.dp else 32.dp)) {
            Column(Modifier.fillMaxWidth(.48f).heightIn(min = if (wide) 240.dp else 256.dp)) {
                Surface(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .16f), shape = PreviewStyle.button) {
                    Text(copy(R.string.dp_sample), Modifier.padding(8.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }; Gap(24.dp)
                Text(copy(R.string.dp_lunch_meal), fontSize = if (wide) 36.sp else 40.sp, lineHeight = if (wide) 40.sp else 44.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary)
                Gap(16.dp); Text(copy(R.string.dp_hero_meal), fontSize = if (wide) 18.sp else 20.sp, lineHeight = if (wide) 25.sp else 28.sp, color = MaterialTheme.colorScheme.onPrimary)
            }
            Gap(16.dp)
            Button({ state.future(R.string.dp_add) }, Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = PreviewStyle.button,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface)) {
                Text(copy(R.string.dp_add_meal), fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
    }
}
@Composable private fun TabletEnergy(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    val primary = MaterialTheme.colorScheme.primary
    Surface(Modifier.fillMaxWidth().fillMaxHeight(), shape = PreviewStyle.radius, shadowElevation = 8.dp) {
        Column(Modifier.padding(if (wide) 24.dp else 32.dp), verticalArrangement = Arrangement.Center) {
            Label(copy(R.string.dp_energy), size = if (wide) 18 else 20, muted = true); Gap(8.dp)
            Label(copy(R.string.dp_energy_fixture), size = 40, bold = true); Gap(if (wide) 32.dp else 24.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                slotLabels(state).forEach { label ->
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(onClick = { state.future(R.string.dp_add) }, shape = CircleShape, color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(56.dp).drawWithContent {
                                drawContent(); val stroke = 2.dp.toPx()
                                drawCircle(primary, radius = size.minDimension / 2 - stroke / 2, style = Stroke(stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))))
                            }) { Box(contentAlignment = Alignment.Center) { Label("＋", size = 26) } }
                        Gap(8.dp); Label(label, muted = true, center = true, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            Gap(if (wide) 32.dp else 24.dp); Label(copy(R.string.dp_energy_note), muted = true)
        }
    }
}
@Composable private fun TabletDiary(state: PreviewState) {
    val wide = LocalTabletLayout.current!!.wide
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Panel(Modifier.widthIn(max = if (wide) 880.dp else 704.dp)) {
            TabletSplit(left = { TabletArt(R.drawable.dp_art_log, if (wide) 192.dp else 224.dp, modifier = Modifier.align(Alignment.CenterHorizontally)) }, right = {
                Column { Label(copy(R.string.dp_diary_ready), size = 24, bold = true, center = !wide, modifier = Modifier.fillMaxWidth()); Gap(24.dp)
                    Label(copy(R.string.dp_diary_empty), size = if (wide) 18 else 20, center = !wide, modifier = Modifier.fillMaxWidth()); Gap(24.dp)
                    Primary(R.string.dp_add_food, arrow = false) { state.future(R.string.dp_add) } }
            }, gap = 32.dp, leftWidth = 192.dp)
        }
    }
}
@Composable private fun TabletNavigation(state: PreviewState) {
    Surface(Modifier.widthIn(max = 1120.dp).fillMaxWidth(), shape = PreviewStyle.radius, shadowElevation = 4.dp) {
        Row(Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.Bottom) {
            listOf(R.string.dp_today to "⌂", R.string.dp_menu to "▤", R.string.dp_add to "＋", R.string.dp_progress to "↗", R.string.dp_profile to "☺︎").forEachIndexed { index, (title, icon) ->
                TextButton({ if (index == 0) state.go(PreviewPage.Today) else state.future(title) }, Modifier.weight(1f).heightIn(min = 80.dp), contentPadding = PaddingValues(0.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (index == 2) Surface(shape = PreviewStyle.button, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text(icon, color = MaterialTheme.colorScheme.onPrimary, fontSize = 28.sp) }
                        } else Text(icon, fontSize = 28.sp)
                        Gap(8.dp); Text(copy(title), fontSize = 16.sp, lineHeight = 20.sp, textAlign = TextAlign.Center, fontWeight = if (index == 0) FontWeight.ExtraBold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}
