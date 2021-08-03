package namit.retail_app.home.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import namit.retail_app.core.navigation.SettingsNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.home.R
import namit.retail_app.home.presentation.profile.setting.SettingProfileFragment
import namit.retail_app.home.presentation.settings.language.SelectLanguageFragment
import org.koin.android.ext.android.inject

class SettingsActivity: BaseActivity() {

    companion object {
        private const val KEY_OPEN_FRAGMENT = "KEY_OPEN_FRAGMENT"
        const val EXTRA_OPEN_SETTING_LANGUAGE = "EXTRA_OPEN_SETTING_LANGUAGE"
        const val EXTRA_OPEN_SETTING_PROFILE = "EXTRA_OPEN_SETTING_PROFILE"
        fun openActivity(context: Context, keyPage: String): Intent {
            return Intent(context, SettingsActivity::class.java).apply {
                putExtra(KEY_OPEN_FRAGMENT, keyPage)
            }
        }
    }

    private val settingsNavigator: SettingsNavigator by inject()

    override var containerResId: Int = R.id.settingsFragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initView()

        val keyOpenFragment = intent.getStringExtra(KEY_OPEN_FRAGMENT)
        if (keyOpenFragment == EXTRA_OPEN_SETTING_PROFILE) {
            openSettingsProfile()
        } else if (keyOpenFragment == EXTRA_OPEN_SETTING_LANGUAGE) {
            openSettingsLanguage()
        }
    }

    private fun openSettingsLanguage() {
        addFragment(
            fragment = settingsNavigator.getSettingsFragment(),
            addToBackStack = true,
            tag = SelectLanguageFragment.TAG
        )
    }

    private fun openSettingsProfile() {
        addFragment(
            fragment = settingsNavigator.getSettingProfileFragment(),
            addToBackStack = true,
            tag = SettingProfileFragment.TAG
        )
    }

    private fun initView() {
        initToolbar(toolbarId = R.id.toolbar,
            toolbarTitleTextViewId = R.id.screenTitleTextView,
            toolbarBackImageViewId = R.id.backImageView,
            onBackButtonClick = {
                onBackPressed()
            })
    }
}