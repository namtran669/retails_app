package namit.retail_app.app.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

import namit.retail_app.auth.presentation.LoginActivity
import namit.retail_app.auth.presentation.input_otp.InputOTPFragment
import namit.retail_app.auth.presentation.login.LoginFragment
import namit.retail_app.auth.presentation.tos.TermOfServiceDialogFragment
import namit.retail_app.auth.presentation.trueid.AuthWithTrueIDActivity
import namit.retail_app.core.navigation.AuthNavigator

class AuthNavigatorImpl : AuthNavigator {

    override fun getTermOfServiceDialog(): DialogFragment {
        return TermOfServiceDialogFragment.newInstance()
    }

    override fun openLogin(context: Context): Intent {
        return LoginActivity.openActivity(context = context)
    }

    override fun openAuthWithTrueID(context: Context): Intent {
        return AuthWithTrueIDActivity.openActivity(context = context)
    }

    override fun getLogin(): Fragment = LoginFragment.newInstance()

    override fun getInputOTPFragment(phoneNumber: String, region:String): Fragment {
        return InputOTPFragment.newInstance(phoneNumber, region)
    }
}