package namit.retail_app.app.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import namit.retail_app.core.navigation.SettingsNavigator
import namit.retail_app.home.presentation.profile.setting.SettingProfileFragment
import namit.retail_app.home.presentation.settings.SettingsActivity
import namit.retail_app.home.presentation.settings.SettingsFragment
import namit.retail_app.home.presentation.settings.language.SelectLanguageFragment

class SettingsNavigatorImpl : SettingsNavigator {

    override fun openActivity(context: Context, keyPage: String): Intent =
        SettingsActivity.openActivity(context, keyPage)

    override fun getSettingsFragment(): Fragment = SettingsFragment.getInstance()

    override fun getSettingLanguageFragment(): Fragment = SelectLanguageFragment.getInstance()

    override fun getSettingProfileFragment(): Fragment = SettingProfileFragment.getInstance()
}