package sa.gov.ksaa.dal

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class ThisApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("ar")
// Call this on the main thread as it may require Activity.restart()
        AppCompatDelegate.setApplicationLocales(appLocale)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}