package namit.retail_app.auth.presentation.trueid

import com.tdcm.truelifelogin.interfaces.LoginServiceListener
import com.tdcm.truelifelogin.models.Events
import com.tdcm.truelifelogin.models.Screens

interface TrueIdLoginServiceListenerWrapper: LoginServiceListener {
    override fun onLoginSuccess(json: String?, expiresSecond: Int) {}

    override fun onFindTrueIDApp(isFound: Boolean) {}

    override fun onMappingSuccess(msg: String?) {}

    override fun onGetInfoSuccess(json: String?, expiresSecond: Int) {}

    override fun onCanceled() {}

    override fun onForgotError(errorObject: String?) {}

    override fun onRegisterError(errorMessage: String?) {}

    override fun onRefreshAccessToken(isSuccess: Boolean) {}

    override fun onForgotSuccess(loginCode: String?, clientId: String?) {}

    override fun onRevokeAlready() {}

    override fun onLogoutRespond(isSuccess: Boolean, json: String?) {}

    override fun onGetInfoFailed(errorMessage: String?) {}

    override fun onMappingAlready(msg: String?) {}

    override fun onMappingFailed(msg: String?) {}

    override fun onRefreshAccessTokenFailed(errorMessage: String?) {}

    override fun onReceivedEvent(events: Events?) {}

    override fun onLoginError(msg: String?) {}

    override fun onRegisterSuccess(loginCode: String?, clientId: String?) {}

    override fun onReceivedScreen(screens: Screens?) {}
}