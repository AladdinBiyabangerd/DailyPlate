package com.aladdin.dailyplate.ui.menu

import android.content.Context
import androidx.compose.runtime.Composable

internal fun debugMenuAvailable(context: Context): Boolean = false
@Composable internal fun DebugMenuToggle(showDemo: Boolean, change: () -> Unit) = Unit
@Composable internal fun DebugWeeklyMenu() = Unit
