package namit.retail_app.core.utils

import android.app.Activity
import android.content.res.Configuration
import java.util.*

object LocaleUtils {

    const val English = "en"
    const val Thai = "th"

    private var currentLanguageSetting = ""

    //New way
    fun isThai(): Boolean {
        return currentLanguageSetting == Thai
    }

    fun getCurrentLocale(): Locale {
        return if (currentLanguageSetting == Thai) Locale("th_TH") else Locale.ENGLISH
    }

    fun getCurrentLanguage(): String {
        return currentLanguageSetting
    }

    fun detectStringThEnLanguage(contentTh: String, contentEn: String): String {
        val localeLanguage = getCurrentLanguage()
        return if (localeLanguage.equals("th", true)) {
            contentTh
        } else {
            contentEn
        }
    }

    private fun setCurrentLanguageSetting(language: String) {
        this.currentLanguageSetting = language
    }

    fun applyLanguage(activity: Activity, language: String) {
        setCurrentLanguageSetting(language)
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config: Configuration = activity.resources.configuration
        config.setLocale(locale)
        activity.createConfigurationContext(config)
        //todo code Deprecated updateConfiguration
        activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
    }
}