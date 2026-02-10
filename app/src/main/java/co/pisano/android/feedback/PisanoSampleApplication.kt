package co.pisano.android.feedback

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import co.pisano.android.feedback.util.PisanoSdkBootstrapper

class PisanoSampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Force light mode so SDK bottom-sheet (WebView) won't auto-dark based on system night mode.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        PisanoSdkBootstrapper.ensureInitialized(this)
    }
}


