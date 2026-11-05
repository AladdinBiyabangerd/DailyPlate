package com.aladdin.dailyplate.ui.menu

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.ui.QidaCard
import com.aladdin.dailyplate.ui.QidaHeading
import com.aladdin.dailyplate.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.ui.platform.LocalConfiguration

/** Debug presentation only. Never inserts fabricated plans, recipes or diary entries. */
internal fun debugMenuAvailable(context: Context): Boolean =
    context.applicationContext is com.aladdin.dailyplate.DailyPlateApp &&
        context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

@Composable
internal fun DebugMenuToggle(showDemo: Boolean, change: () -> Unit) {
    TextButton(onClick = change, modifier = Modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget)) {
        Text(stringResource(if (showDemo) R.string.demo_show_real else R.string.demo_show_mock))
    }
}

@Composable
internal fun DebugWeeklyMenu(today: LocalDate = LocalDate.now()) {
    val locale = LocalConfiguration.current.locales[0]
    val first = today.with(java.time.DayOfWeek.MONDAY)
    val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
    val names = stringArrayResource(R.array.demo_meals)
    val photos = listOf(R.drawable.demo_meal_0, R.drawable.demo_meal_1, R.drawable.demo_meal_2,
        R.drawable.demo_meal_3, R.drawable.demo_meal_4, R.drawable.demo_meal_5, R.drawable.demo_meal_6)
    QidaHeading(stringResource(R.string.demo_title))
    Text(stringResource(R.string.demo_notice), color = MaterialTheme.colorScheme.onSurfaceVariant)
    names.forEachIndexed { index, name ->
        QidaCard {
            Text(first.plusDays(index.toLong()).format(format), style = MaterialTheme.typography.titleMedium)
            Image(painterResource(photos[index]), contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(QidaDimensions.heroArt).testTag("demo-photo-$index"))
            Text(name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("demo-meal-$index"))
        }
    }
}
