package com.aladdin.dailyplate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.aladdin.dailyplate.R

// Spacing maps directly to design-concepts/tokens.css.
object QidaSpacing {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
}

object QidaDimensions {
    // Android platform minimum, separate from the design spacing scale.
    val touchTarget = 48.dp
    val cardRadius = 24.dp
}

@Composable
fun QidaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = colorResource(R.color.simple_primary),
            onPrimary = colorResource(R.color.simple_surface),
            background = colorResource(R.color.simple_bg),
            onBackground = colorResource(R.color.simple_text),
            surface = colorResource(R.color.simple_surface),
            onSurface = colorResource(R.color.simple_text),
            onSurfaceVariant = colorResource(R.color.simple_muted),
            secondary = colorResource(R.color.simple_primary),
            onSecondary = colorResource(R.color.simple_surface),
            secondaryContainer = colorResource(R.color.simple_bg),
            onSecondaryContainer = colorResource(R.color.simple_text),
            primaryContainer = colorResource(R.color.simple_bg),
            onPrimaryContainer = colorResource(R.color.simple_text),
            tertiary = colorResource(R.color.simple_primary),
            onTertiary = colorResource(R.color.simple_surface),
            tertiaryContainer = colorResource(R.color.simple_yellow),
            onTertiaryContainer = colorResource(R.color.simple_text),
            surfaceVariant = colorResource(R.color.simple_bg),
            surfaceTint = colorResource(R.color.simple_primary),
            outline = colorResource(R.color.simple_muted),
            error = colorResource(R.color.simple_text),
            onError = colorResource(R.color.simple_surface),
            errorContainer = colorResource(R.color.simple_peach),
            onErrorContainer = colorResource(R.color.simple_text),
        ),
        shapes = Shapes(large = RoundedCornerShape(QidaDimensions.cardRadius)),
        typography = Typography().let { it.copy(bodyMedium = it.bodyMedium.copy(fontSize = 16.sp),
            labelLarge = it.labelLarge.copy(fontSize = 16.sp)) },
        content = content,
    )
}
