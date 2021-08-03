package namit.retail_app.core.data.repository

import android.util.Log
import namit.retail_app.core.provider.AdvertisingIdProvider
import namit.retail_app.core.provider.PreferenceProvider

interface UuidRepository {
    suspend fun generateUuid(): String
    fun getUuid(): String
    fun haveUuid(): Boolean
}

class UuidRepositoryImpl(
    private val advertisingIdProvider: AdvertisingIdProvider,
    private val preferenceProvider: PreferenceProvider
) :
    UuidRepository {

    companion object {
        private const val PREF_CURRENT_UUID = "PREF_CURRENT_UUID"
    }

    override suspend fun generateUuid(): String {
        val advertisingId = advertisingIdProvider.execute()
        Log.e("ARMTIMUS", "advertisingId $advertisingId")
        preferenceProvider.setPreference(key = PREF_CURRENT_UUID, value = advertisingId)
        return advertisingId ?: ""
    }

    override fun getUuid(): String {
        return preferenceProvider.getPreference(
            key = PREF_CURRENT_UUID,
            defValue = ""
        )
    }

    override fun haveUuid(): Boolean {
        return preferenceProvider.getPreference(
            key = PREF_CURRENT_UUID,
            defValue = ""
        ).isNotBlank()
    }
}