package namit.retail_app.auth.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import namit.retail_app.auth.R
import namit.retail_app.auth.presentation.login.LoginFragment
import namit.retail_app.auth.presentation.trueid.AuthWithTrueIDActivity
import namit.retail_app.core.navigation.AuthNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import org.koin.android.ext.android.inject

class LoginActivity : BaseActivity() {

    companion object {
        fun openActivity(context: Context): Intent =
            Intent(context, LoginActivity::class.java)
    }

    private val authNavigator: AuthNavigator by inject()

    override var containerResId: Int = R.id.loginFragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
        openLogin()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AuthWithTrueIDActivity.REQUEST_AUTH_WITH_TRUEID) {
            //TODO send access token to verify
            Toast.makeText(this,
                data?.getStringExtra(AuthWithTrueIDActivity.KEY_RESULT_AUTH_WITH_TRUEID),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun openLogin() {
        addFragment(
            fragment = authNavigator.getLogin() as LoginFragment,
            addToBackStack = true,
            tag = LoginFragment.TAG
        )
    }

    private fun initView() {
        initToolbar(toolbarId = R.id.loginToolbar,
            toolbarBackImageViewId = R.id.backImageView,
            onBackButtonClick = {
                onBackPressed()
            })
    }
}