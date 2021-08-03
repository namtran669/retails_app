package namit.retail_app.home.presentation.settings.language

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.core.navigation.MainTabNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.utils.LocaleUtils
import namit.retail_app.home.R
import kotlinx.android.synthetic.main.fragment_settings_language.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SelectLanguageFragment : BaseFragment() {
    companion object {
        const val TAG = "SelectLanguageFragment"
        fun getInstance(): SelectLanguageFragment = SelectLanguageFragment()
    }

    private val viewModel: SelectLanguageViewModel by viewModel()
    private val mainTabNavigator: MainTabNavigator by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_language, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindViewModel()
    }

    private fun initView() {
        (activity as BaseActivity).setToolbarTitle(getString(R.string.language))

        viewModel.getLanguage()

        englishRadioButton.setOnClickListener {
            englishRadioButton.isChecked = true
            thaiRadioButton.isChecked = false
            viewModel.setLanguage(LocaleUtils.English)
        }

        thaiRadioButton.setOnClickListener {
            englishRadioButton.isChecked = false
            thaiRadioButton.isChecked = true
            viewModel.setLanguage(LocaleUtils.Thai)
        }
    }

    private fun bindViewModel() {
        viewModel.renderCurrentLanguage.observe(viewLifecycleOwner, Observer {
            if (TextUtils.equals(LocaleUtils.English, it)) {
                englishRadioButton.isChecked = true
                thaiRadioButton.isChecked = false
            } else {
                englishRadioButton.isChecked = false
                thaiRadioButton.isChecked = true
            }
        })

        viewModel.presentToTabActivity.observe(viewLifecycleOwner, Observer {
            Handler().postDelayed({
                openMainTab(it)
            }, 500)
        })
    }

    private fun openMainTab(language: String) {
        activity?.let {
            LocaleUtils.applyLanguage(it, language)
            startActivity(
                mainTabNavigator.getTabActivity(it)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            it.finish()
            it.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

}