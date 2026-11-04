package az.qida.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import az.qida.app.R

// Spacing maps directly to design-concepts/tokens.css.
object QidaSpacing {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
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
        ),
        typography = Typography(),
        content = content,
    )
}
