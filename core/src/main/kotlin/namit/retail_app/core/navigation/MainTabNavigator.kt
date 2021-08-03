package namit.retail_app.core.navigation

import android.content.Context
import android.content.Intent

interface MainTabNavigator {
    fun getTabActivity(context: Context): Intent
}