package namit.retail_app.core.provider

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager

interface PreferenceProvider {

    fun <T> setPreference(key: String, value: T)

    fun <T> setPreferenceSync(key: String, value: T)

    fun clearPreference(key: String)

    fun clearAll()

    fun <T> getPreference(key: String, defValue: T): T
}

class SharedPreferenceProvider(context: Context) : PreferenceProvider {

    private val appPref = PreferenceManager.getDefaultSharedPreferences(context)

    @SuppressLint("CommitPrefEdits")
    override fun <T> setPreference(key: String, value: T) {
        appPref.edit().apply {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
            }
            apply()
        }
    }

    @SuppressLint("ApplySharedPref")
    override fun <T> setPreferenceSync(key: String, value: T) {
        appPref.edit().apply {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
            }
            commit()
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun clearPreference(key: String) {
        appPref.edit().apply {
            remove(key)
            apply()
        }
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    override fun <T> getPreference(key: String, defValue: T): T {
        appPref.apply {
            return when (defValue) {
                is String -> getString(key, defValue)
                is Boolean -> getBoolean(key, defValue)
                is Int -> getInt(key, defValue)
                is Long -> getLong(key, defValue)
                else -> defValue
            } as T
        }
    }

    override fun clearAll() {
        appPref.edit().clear().apply()
    }
}