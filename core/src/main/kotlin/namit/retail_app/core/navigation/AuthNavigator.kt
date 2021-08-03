package namit.retail_app.core.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

interface AuthNavigator {
    fun getTermOfServiceDialog(): DialogFragment

    fun openLogin(context: Context): Intent

    fun openAuthWithTrueID(context: Context): Intent

    fun getLogin(): Fragment

    fun getInputOTPFragment(phoneNumber: String, region: String): Fragment
}