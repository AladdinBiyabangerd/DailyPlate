package com.aladdin.dailyplate.ui.entry

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.ui.*
import com.aladdin.dailyplate.ui.theme.*

/** Generic decorative art must never imply a measured portion or a real food photo. */
@Composable
internal fun PortionHero(@StringRes title: Int, @DrawableRes art: Int) {
    QidaHeading(stringResource(title))
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(QidaSpacing.small)) {
        QidaArt(art, Modifier.fillMaxWidth().height(QidaDimensions.bannerArt))
        Text(stringResource(R.string.pd_illustration), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun PortionNotice(text: String) {
    Surface(shape = MaterialTheme.shapes.large, color = colorResource(R.color.simple_peach),
        contentColor = MaterialTheme.colorScheme.onSurface) {
        Text(text, Modifier.fillMaxWidth().padding(QidaSpacing.medium))
    }
}
