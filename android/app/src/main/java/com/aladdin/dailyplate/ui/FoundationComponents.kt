package com.aladdin.dailyplate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import com.aladdin.dailyplate.R
import com.aladdin.dailyplate.ui.theme.QidaSpacing
import com.aladdin.dailyplate.ui.theme.QidaDimensions

@Composable
fun QidaButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick, modifier.fillMaxWidth().heightIn(min = QidaDimensions.touchTarget),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(QidaDimensions.buttonRadius),
        contentPadding = PaddingValues(QidaSpacing.medium)) {
        Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

/** Persistent label, numeric keyboard and an announced error; caller owns raw text. */
@Composable
fun QidaField(label: String, value: String, onValueChange: (String) -> Unit,
              error: String? = null, numeric: Boolean = false) {
    OutlinedTextField(value, onValueChange, modifier = Modifier.fillMaxWidth(),
        label = { Text(label) }, isError = error != null,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(QidaDimensions.buttonRadius),
        singleLine = numeric,
        supportingText = error?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Decimal else KeyboardType.Text))
}

enum class SurfaceState { Loading, Empty, Error }

@Composable
fun QidaState(state: SurfaceState, message: String? = null, retry: (() -> Unit)? = null) {
    QidaCard {
        if (state == SurfaceState.Error) QidaArt(R.drawable.art_write_error,
            Modifier.fillMaxWidth().height(QidaDimensions.bannerArt))
        if (state == SurfaceState.Loading) CircularProgressIndicator(trackColor = colorResource(R.color.simple_accent))
        Text(message ?: stringResource(when (state) {
            SurfaceState.Loading -> R.string.loading
            SurfaceState.Empty -> R.string.empty_results
            SurfaceState.Error -> R.string.unavailable
        }), style = MaterialTheme.typography.bodyLarge)
        if (state == SurfaceState.Error && retry != null) {
            QidaButton(stringResource(R.string.retry), retry)
        }
    }
}

@Composable
fun QidaCard(content: @Composable ColumnScope.() -> Unit) {
    Card(shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = QidaDimensions.cardElevation)) {
        Column(Modifier.fillMaxWidth().padding(QidaSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(QidaSpacing.medium), content = content)
    }
}

@Composable
fun QidaHeading(text: String) {
    Text(text, Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
}
