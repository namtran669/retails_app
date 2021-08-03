package namit.retail_app.auth.presentation.trueid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import namit.retail_app.auth.R
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.core.utils.LocaleUtils
import com.tdcm.truelifelogin.authentication.LoginService
import com.tdcm.truelifelogin.constants.SDKEnvironment
import com.tdcm.truelifelogin.interfaces.VerifyListener

class AuthWithTrueIDActivity : BaseActivity(), TrueIdLoginServiceListenerWrapper, VerifyListener {

    companion object {
        private const val TRUEID_REDIRECT_URL = "https://www.trueid.net"
        private val TRUEID_AUTH_SCOPE = listOf("public_profile", "mobile", "email", "references")
        const val REQUEST_AUTH_WITH_TRUEID = 3001
        const val KEY_RESULT_AUTH_WITH_TRUEID = "KEY_RESULT_AUTH_WITH_TRUEID"
        const val FAILED_TO_AUTHENTICATION_WITH_TRUEID = "FAILED_TO_AUTHENTICATION_WITH_TRUEID"
        fun openActivity(context: Context): Intent =
            Intent(context, AuthWithTrueIDActivity::class.java)
    }

    private lateinit var trueIdService: LoginService
    private val environment = SDKEnvironment.STAGING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_trueid)

        trueIdService = LoginService(this, TRUEID_AUTH_SCOPE, TRUEID_REDIRECT_URL, environment)

        startLoginWithTrueID()
    }

    private fun startLoginWithTrueID() {
        //TODO Just mock data for now
        val language = if (LocaleUtils.isThai()) "th" else "en"
        val latitude = "0.0"
        val longitude = "0.0"
        val isAuto = true
        trueIdService.login(language, latitude, longitude, isAuto)
    }

    //LOGIN
    override fun onLoginSuccess(json: String?, expiresSecond: Int) {
        Log.e("ARMTIMUS", "onLoginSuccess json $json")
        trueIdService.selfVerify(this)
    }

    //FORGOT PASSWORD
    override fun onForgotError(errorObject: String?) = authenticationFailed()

    //REGISTER
    override fun onRegisterError(errorMessage: String?) = authenticationFailed()

    //It always error when we login success then uninstall then install and open login process again.
    override fun onLoginError(msg: String?) = authenticationFailed()

    override fun onCanceled() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun authenticationFailed() {
        val returnIntent = Intent()
        returnIntent.putExtra(KEY_RESULT_AUTH_WITH_TRUEID, FAILED_TO_AUTHENTICATION_WITH_TRUEID)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onVerifyFailed(errorMsg: String?) = authenticationFailed()

    override fun onVerifySuccess(accessToken: String?) {
        accessToken?.let {
            val returnIntent = Intent()
            returnIntent.putExtra(KEY_RESULT_AUTH_WITH_TRUEID, it)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } ?: authenticationFailed()
    }
}