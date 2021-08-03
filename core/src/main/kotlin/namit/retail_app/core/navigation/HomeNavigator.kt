package namit.retail_app.core.navigation

import androidx.fragment.app.Fragment

interface HomeNavigator {
    fun getHomeFragment(): Fragment

    fun getOrderFragment(): Fragment

    fun getProfileFragment(): Fragment
}