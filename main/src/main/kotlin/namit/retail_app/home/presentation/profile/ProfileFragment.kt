package namit.retail_app.home.presentation.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import namit.retail_app.address.presentation.manage.MyAddressDialogFragment
import namit.retail_app.core.config.FAQ_URL
import namit.retail_app.core.navigation.AddressNavigator
import namit.retail_app.core.navigation.PaymentNavigator
import namit.retail_app.core.navigation.SettingsNavigator
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.home.BuildConfig
import namit.retail_app.home.R
import namit.retail_app.home.presentation.settings.SettingsActivity
import namit.retail_app.home.presentation.tab.TabActivity
import namit.retail_app.payment.presentation.payment.PaymentMethodListFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment: BaseFragment() {

    companion object {
        const val TAG = "ProfileFragment"
        fun getInstance(): ProfileFragment = ProfileFragment()
    }

    private val viewModel: ProfileViewModel by viewModel()
    private val settingsNavigator: SettingsNavigator by inject()
    private val addressNavigator: AddressNavigator by inject()
    private val paymentNavigator: PaymentNavigator by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        viewModel.renderUserProfile()
    }

    private fun initView() {
        versionTextView.text = getString(R.string.version_app)
            .plus(" ${BuildConfig.VERSION_NAME}")
            .plus(" (${BuildConfig.VERSION_CODE})")

        paymentConstraintLayout.setOnClickListener {
            context?.let {
                startActivityForResult(
                    paymentNavigator.openPaymentWithPaymentList(context = it),
                    PaymentMethodListFragment.RESULT_CODE_SELECTED_PAYMENT_METHOD
                )
            }
        }

        addressConstraintLayout.setOnClickListener {
            fragmentManager?.let {
                addressNavigator.getMyAddressDialog()
                    .show(it, MyAddressDialogFragment.TAG)
            }
        }

        settingConstraintLayout.setOnClickListener {
            context?.let {
                activity?.startActivityForResult(settingsNavigator.openActivity(it,
                        SettingsActivity.EXTRA_OPEN_SETTING_LANGUAGE),
                    TabActivity.REQUEST_CODE_SETTING)
            }
        }

        helpConstraintLayout.setOnClickListener {
            viewModel.trackClickHelpCenter()
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(FAQ_URL))
            startActivity(browserIntent)
        }
    }

    private fun bindViewModel() {
        viewModel.phoneNumber.observe(viewLifecycleOwner, Observer {
            nameTextView.text = it
        })
    }
}