package namit.retail_app.app.navigation

import android.content.Context
import android.content.Intent
import namit.retail_app.app.presentation.onboard.OnboardActivity
import namit.retail_app.app.presentation.onboard.OnboardChildFragment
import namit.retail_app.app.presentation.splash.SplashScreenActivity
import namit.retail_app.core.navigation.AppNavigator
import namit.retail_app.core.presentation.base.BaseFragment

class AppNavigatorImpl : AppNavigator {
    override fun getSplashScreenActivity(context: Context): Intent {
        return SplashScreenActivity.openActivity(context = context)
    }

    override fun getOnBoardActivity(context: Context): Intent {
        return OnboardActivity.openActivity(context)
    }

    override fun getOnBoardChildFragment(type: String): BaseFragment {
        return OnboardChildFragment.newInstance(type)
    }
}