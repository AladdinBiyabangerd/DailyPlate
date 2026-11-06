package com.aladdin.dailyplate.ui.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/** Bare optional choice, white activity row, or illustrated-form Yes/No card per approved CSS. */
@Composable internal fun TabletRadio(text: String, selected: Boolean, modifier: Modifier = Modifier,
    bare: Boolean = false, answerCard: Boolean = false, onClick: () -> Unit) {
    Surface(modifier.selectable(selected, role = Role.RadioButton, onClick = onClick),
        shape = if (answerCard) PreviewStyle.radius else PreviewStyle.button,
        color = if (bare) Color.Transparent else MaterialTheme.colorScheme.surface,
        shadowElevation = if (answerCard) 8.dp else 0.dp,
        border = if (selected && answerCard) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null) {
        Row(Modifier.heightIn(min = if (bare) 48.dp else 56.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            RadioButton(selected, null, Modifier.size(24.dp)); Spacer(Modifier.width(8.dp))
            Label(text, size = if (answerCard) 20 else 16, bold = answerCard, modifier = Modifier.weight(1f, false))
        }
    }
}
