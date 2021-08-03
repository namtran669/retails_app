package namit.retail_app.core.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

interface SettingsNavigator {
    fun openActivity(context: Context, keyPage: String): Intent
    fun getSettingsFragment(): Fragment
    fun getSettingLanguageFragment(): Fragment
    fun getSettingProfileFragment(): Fragment
}