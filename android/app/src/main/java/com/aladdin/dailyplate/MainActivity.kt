package com.aladdin.dailyplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aladdin.dailyplate.ui.BootstrapScreen
import com.aladdin.dailyplate.ui.theme.QidaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, getColor(R.color.simple_text),
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, getColor(R.color.simple_text),
            ),
        )
        setContent { QidaTheme { BootstrapScreen() } }
    }
}
