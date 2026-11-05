package com.aladdin.dailyplate.ui

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.aladdin.dailyplate.ui.theme.QidaTheme
import com.aladdin.dailyplate.ui.theme.QidaSpacing
import android.graphics.Bitmap
import android.graphics.Canvas
import android.content.res.Configuration
import android.os.Looper
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.aladdin.dailyplate.MainActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = Application::class, qualifiers = "w360dp-h800dp-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class FoundationTest {
    @Test fun stableRoutesRestoreEveryBackStepAndIgnoreUnknownIds() {
        var state = NavigationState()
        Destination.entries.filter { it != Destination.Welcome }.forEach { state = state.open(it) }
        var restored = NavigationState.restore(state.encode())
        assertEquals(state, restored)
        state.stack.asReversed().forEach { destination ->
            assertEquals(destination, restored.current)
            restored = restored.back()
        }
        assertEquals(Destination.Welcome, restored.current)
        assertEquals(NavigationState(), NavigationState.restore("corrupt"))
    }

    @Test fun renderWelcomeInEveryLanguageWithLargeTextWithoutPackaging() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        for (scale in listOf(1f, 1.5f)) for (language in listOf("az", "en", "ru")) {
            context.getSharedPreferences("presentation", 0).edit().putString("language", language).commit()
            val configuration = Configuration(context.resources.configuration).apply { fontScale = scale }
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            val controller = Robolectric.buildActivity(MainActivity::class.java).setup().visible()
            val activity = controller.get()
            shadowOf(Looper.getMainLooper()).idle()
            val view = activity.window.decorView
            view.measure(View.MeasureSpec.makeMeasureSpec(360, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY))
            view.layout(0, 0, 360, 800)
            shadowOf(Looper.getMainLooper()).idle()
            val bitmap = Bitmap.createBitmap(360, 800, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val output = File("build/reports/k06/welcome-$language-$scale.png")
            requireNotNull(output.parentFile).mkdirs()
            output.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            assertTrue(output.length() > 1000)
            // Configuration recreation retains saved navigation and selected language.
            controller.recreate()
            assertEquals(language, context.getSharedPreferences("presentation", 0).getString("language", null))
            controller.pause().stop().destroy()
        }
    }
    @Test fun renderLoadingEmptyAndWriteErrorCards() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        context.getSharedPreferences("presentation", 0).edit().putString("language", "az").commit()
        for (state in SurfaceState.entries) {
            val controller = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible()
            val activity = controller.get()
            activity.setContent {
                QidaTheme {
                    Column(Modifier.padding(QidaSpacing.large)) {
                        QidaState(state, if (state == SurfaceState.Error) context.getString(com.aladdin.dailyplate.R.string.ob_write_error) else null, retry = {})
                    }
                }
            }
            shadowOf(Looper.getMainLooper()).idle()
            val view = activity.window.decorView
            view.measure(View.MeasureSpec.makeMeasureSpec(360, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY))
            view.layout(0, 0, 360, 800)
            shadowOf(Looper.getMainLooper()).idle()
            val bitmap = Bitmap.createBitmap(360, 800, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))
            val file = File("build/reports/ui-parity/state-$state.png")
            requireNotNull(file.parentFile).mkdirs()
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            assertTrue(file.length() > 1000)
            controller.pause().stop().destroy()
        }
    }
}
