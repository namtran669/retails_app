package namit.retail_app.core.data.repository

import namit.retail_app.core.provider.PreferenceProvider

interface AccessTokenRepository {
    fun saveAccessTokenToLocal(token: String)
    fun getAccessTokenFromLocal(): String
    fun removeAccessToken()
    fun hasAccessToken(): Boolean
}

class AccessTokenRepositoryImpl(private val preferenceProvider: PreferenceProvider) :
    AccessTokenRepository {

    companion object {
        const val PREF_ACCESS_TOKEN = "PREF_ACCESS_TOKEN"
    }

    override fun saveAccessTokenToLocal(token: String) {
        preferenceProvider.setPreference(
            key = PREF_ACCESS_TOKEN,
            value = "Bearer ".plus(token)
        )
    }

    override fun getAccessTokenFromLocal(): String {

        return preferenceProvider.getPreference(
            key = PREF_ACCESS_TOKEN,
            defValue = "")
    }

    override fun removeAccessToken() {
        preferenceProvider.clearPreference(PREF_ACCESS_TOKEN)
    }

    override fun hasAccessToken(): Boolean {
        return preferenceProvider.getPreference(
            key = PREF_ACCESS_TOKEN,
            defValue = ""
        ).isNotEmpty()
    }

}