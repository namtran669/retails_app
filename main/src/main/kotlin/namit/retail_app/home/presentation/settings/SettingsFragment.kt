package namit.retail_app.home.presentation.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.core.navigation.SettingsNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.utils.LocaleUtils
import namit.retail_app.home.R
import namit.retail_app.home.presentation.settings.language.SelectLanguageFragment
import namit.retail_app.home.presentation.tab.TabActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment() {

    companion object {
        const val TAG = "SettingsFragment"
        fun getInstance(): SettingsFragment = SettingsFragment()
    }

    private val viewModel: SettingsViewModel by viewModel()
    private val settingsNavigator: SettingsNavigator by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()

        viewModel.getLanguage()
        viewModel.getNotificationState()
    }

    private fun initView() {
        (activity as BaseActivity).setToolbarTitle(getString(R.string.settings))

        languageConstraintLayout.setOnClickListener {
            (activity as BaseActivity).apply {
                replaceFragment(
                    fragment = settingsNavigator.getSettingLanguageFragment(),
                    addToBackStack = true,
                    tag = SelectLanguageFragment.TAG
                )
            }
        }

        notificationSwitchButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationState(isChecked)
        }

        logoutTextView.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun bindViewModel() {
        viewModel.renderCurrentLanguage.observe(viewLifecycleOwner, Observer {
            if (TextUtils.equals(LocaleUtils.English, it)) {
                currentLanguageTextView.text = getString(R.string.english)
            } else {
                currentLanguageTextView.text = getString(R.string.thai)
            }
        })

        viewModel.renderNotificationState.observe(viewLifecycleOwner, Observer { isEnable ->
            notificationSwitchButton.apply {
                setCheckedNoEvent(isEnable)
                setBackColorRes(if (isEnable) R.color.curiousBlue else R.color.tropicalBlue)
            }
        })

        viewModel.showErrorMessage.observe(viewLifecycleOwner, Observer {
            showSnackBar(it, Snackbar.LENGTH_LONG)
        })

        viewModel.presentToTabActivity.observe(viewLifecycleOwner, Observer {
            openMainTab()
        })
    }

    private fun openMainTab() {
        activity?.let {
            val intent = Intent()
            intent.putExtra(TabActivity.EXTRA_LOG_OUT, true)
            it.setResult(Activity.RESULT_OK, intent)
            it.finish()
        }
    }
}