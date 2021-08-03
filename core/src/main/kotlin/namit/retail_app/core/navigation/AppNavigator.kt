package namit.retail_app.core.navigation

import android.content.Context
import android.content.Intent
import namit.retail_app.core.presentation.base.BaseFragment

interface AppNavigator {
    fun getOnBoardActivity(context: Context): Intent

    fun getOnBoardChildFragment(type: String): BaseFragment

    fun getSplashScreenActivity(context: Context): Intent
}