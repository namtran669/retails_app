package namit.retail_app.app.navigation

import android.content.Context
import android.content.Intent
import namit.retail_app.core.navigation.MainTabNavigator
import namit.retail_app.home.presentation.tab.TabActivity

class MainTabNavigatorImpl: MainTabNavigator {

    override fun getTabActivity(context: Context): Intent =
        TabActivity.getStartIntent(context = context)

}