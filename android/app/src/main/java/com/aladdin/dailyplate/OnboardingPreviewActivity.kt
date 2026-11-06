package com.aladdin.dailyplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aladdin.dailyplate.ui.preview.OnboardingPreview

/** Debug manifest selects this offline UI launcher; release startup is unchanged. */
class OnboardingPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, getColor(R.color.simple_text)),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, getColor(R.color.simple_text)),
        )
        setContent { OnboardingPreview() }
    }
}
