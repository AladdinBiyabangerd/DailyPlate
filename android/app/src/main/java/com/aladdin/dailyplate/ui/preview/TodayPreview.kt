package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aladdin.dailyplate.R

@Composable internal fun Today(state: PreviewState) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Label(copy(R.string.dp_today), muted = true); Gap(8.dp); Label(copy(R.string.dp_hello), size = 28, bold = true) }
                Surface(onClick = { state.future(R.string.dp_profile) }, shape = PreviewStyle.radius, color = androidx.compose.ui.res.colorResource(R.color.simple_peach)) {
                    Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { Text("☺︎", fontSize = 26.sp) }
                }
            }
            Gap(); Label(copy(if (state.diary) R.string.dp_diary_notice else R.string.dp_demo_badge), muted = true); Gap()
            if (state.diary) {
                Panel {
                    Art(R.drawable.dp_art_log, 160.dp, modifier = Modifier.align(Alignment.CenterHorizontally)); Gap()
                    Label(copy(R.string.dp_diary_ready), size = 24, bold = true, center = true, modifier = Modifier.fillMaxWidth()); Gap()
                    Label(copy(R.string.dp_diary_empty), center = true, modifier = Modifier.fillMaxWidth()); Gap()
                    Primary(R.string.dp_add_food, arrow = false) { state.future(R.string.dp_add) }
                }
            } else {
                MealHero(state); Gap(24.dp)
                val circleColor = MaterialTheme.colorScheme.primary
                Panel {
                    Label(copy(R.string.dp_energy), muted = true); Gap(8.dp)
                    Label(copy(R.string.dp_energy_fixture), size = 32, bold = true); Gap(24.dp)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        slotLabels(state).forEach { label ->
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(onClick = { state.future(R.string.dp_add) }, shape = CircleShape, color = MaterialTheme.colorScheme.background,
                                    modifier = Modifier.size(48.dp).drawWithContent {
                                        drawContent()
                                        val stroke = 2.dp.toPx()
                                        drawCircle(circleColor, radius = size.minDimension / 2 - stroke / 2,
                                            style = Stroke(stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))))
                                    }) {
                                    Box(contentAlignment = Alignment.Center) { Text("＋", fontSize = 26.sp, color = MaterialTheme.colorScheme.primary) }
                                }
                                Gap(8.dp); Label(label, size = 14, muted = true, center = true, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                    Gap(); Label(copy(R.string.dp_energy_note), muted = true)
                }
            }
            Gap(); Label(copy(R.string.dp_intake_note), muted = true); Gap()
        }
        BottomNavigation(state)
    }
}
@Composable private fun MealHero(state: PreviewState) {
    Box(Modifier.fillMaxWidth().clip(PreviewStyle.radius).background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, lerp(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onSurface, .1f))))) {
        Art(R.drawable.dp_art_recipe, 208.dp, modifier = Modifier.align(Alignment.TopEnd).offset(x = 32.dp, y = 24.dp))
        Column(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)) {
            Column(Modifier.fillMaxWidth(.48f).heightIn(min = 176.dp)) {
                Surface(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .16f), shape = PreviewStyle.button) {
                    Text(copy(R.string.dp_sample), Modifier.padding(8.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }; Gap()
                Text(copy(R.string.dp_lunch_meal), fontSize = 30.sp, lineHeight = 33.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary)
                Gap(8.dp)
                Text(copy(R.string.dp_hero_meal), fontSize = 16.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onPrimary)
            }
            Gap()
            Button({ state.future(R.string.dp_add) }, Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = PreviewStyle.button,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.primary), contentPadding = PaddingValues(8.dp)) {
                Text(copy(R.string.dp_add_meal), fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
    }
}
@Composable private fun BottomNavigation(state: PreviewState) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), shape = PreviewStyle.radius, shadowElevation = 4.dp) {
        Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Bottom) {
            listOf(R.string.dp_today to "⌂", R.string.dp_menu to "▤", R.string.dp_add to "＋", R.string.dp_progress to "↗", R.string.dp_profile to "☺︎").forEachIndexed { i, (title, icon) ->
                TextButton({ if (i == 0) state.go(PreviewPage.Today) else state.future(title) },
                    Modifier.weight(1f).heightIn(min = 72.dp), contentPadding = PaddingValues(0.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (i == 2) Surface(shape = PreviewStyle.button, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text(icon, color = MaterialTheme.colorScheme.onPrimary, fontSize = 26.sp) }
                        } else Text(icon, fontSize = 26.sp, color = if (i == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Gap(8.dp)
                        Text(copy(title), fontSize = 12.sp, lineHeight = 15.sp, textAlign = TextAlign.Center,
                            color = if (i == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
