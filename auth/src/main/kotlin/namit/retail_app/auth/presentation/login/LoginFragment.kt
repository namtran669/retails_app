package namit.retail_app.auth.presentation.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import namit.retail_app.auth.R
import namit.retail_app.auth.presentation.input_otp.InputOTPFragment
import namit.retail_app.auth.presentation.trueid.AuthWithTrueIDActivity
import namit.retail_app.auth.widget.PhoneNumberView
import namit.retail_app.core.navigation.AuthNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject

class LoginFragment : BaseFragment() {

    companion object {
        const val TAG = "LoginFragment"
        const val DEFAULT_PHONE_REGION = "TH"
        const val REQUEST_AUTH = 1994

        fun newInstance(): LoginFragment = LoginFragment()
    }

    private val authNavigator: AuthNavigator by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_login, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        enterPhoneNumberLayout.addActionListener(object :
            PhoneNumberView.PhoneNumberAction {
            override fun onConfirmButtonClicked(phoneNumber: String) {
                //Currently, app support Thailand only
                (activity as BaseActivity).replaceFragment(
                    fragment = authNavigator.getInputOTPFragment(
                        phoneNumber = phoneNumber,
                        region = DEFAULT_PHONE_REGION) as InputOTPFragment,
                    addToBackStack = true,
                    tag = InputOTPFragment.TAG)
            }
        })

        loginTrueIdClickableView.setOnClickListener {
            activity?.startActivityForResult(
                authNavigator.openAuthWithTrueID(context!!),
                AuthWithTrueIDActivity.REQUEST_AUTH_WITH_TRUEID
            )
        }

        skipButton.setOnClickListener {
            val returnIntent = Intent()
            activity?.apply {
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }
    }

    private fun initView() {
        (activity as BaseActivity).apply {
            hideToolbarBackImageView()
        }
    }
}
