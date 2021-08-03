package namit.retail_app.core.manager

import namit.retail_app.core.provider.PreferenceProvider

interface AppSettingManager {
    fun isFirstOpen(): Boolean
    fun saveFirstOpen(firstOpen: Boolean)
    fun isAcceptTermOfService(version: String): Boolean
    fun acceptedTermOfService(version: String)
    fun getLanguage(): String
    fun setLanguage(language: String)
    fun isEnableNotification(): Boolean
    fun setEnableNotification(isEnable: Boolean)
}

class AppSettingManagerImpl(private val preferenceProvider: PreferenceProvider) :
    AppSettingManager {

    companion object {
        private const val PREF_FIRST_OPEN = "PREF_FIRST_OPEN"
        private const val PREF_TERM_OF_SERVICE = "PREF_TERM_OF_SERVICE"
        private const val PREF_LANGUAGE = "PREF_LANGUAGE"
        private const val PREF_NOTIFICATION = "PREF_NOTIFICATION"
    }

    override fun saveFirstOpen(firstOpen: Boolean) {
        preferenceProvider.setPreference(
            key = PREF_FIRST_OPEN,
            value = firstOpen
        )
    }

    override fun isFirstOpen(): Boolean {
        return preferenceProvider.getPreference(
            key = PREF_FIRST_OPEN,
            defValue = true
        )
    }

    override fun isAcceptTermOfService(version: String): Boolean {
        return preferenceProvider.getPreference(
            key = "${PREF_TERM_OF_SERVICE}_$version",
            defValue = false
        )
    }

    override fun acceptedTermOfService(version: String) {
        preferenceProvider.setPreference(
            key = "${PREF_TERM_OF_SERVICE}_$version",
            value = true
        )
    }

    override fun getLanguage(): String {
        return preferenceProvider.getPreference(
            key = PREF_LANGUAGE,
            defValue = "default"
        )
    }

    override fun setLanguage(language: String) {
        return preferenceProvider.setPreference(
            key = PREF_LANGUAGE,
            value = language
        )
    }

    override fun isEnableNotification(): Boolean {
        return preferenceProvider.getPreference(
            key = PREF_NOTIFICATION,
            defValue = false
        )
    }

    override fun setEnableNotification(isEnable: Boolean) {
        preferenceProvider.setPreference(
            key = PREF_NOTIFICATION,
            value = isEnable
        )
    }
}