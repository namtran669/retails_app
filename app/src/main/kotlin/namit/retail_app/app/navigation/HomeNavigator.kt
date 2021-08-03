package namit.retail_app.app.navigation

import androidx.fragment.app.Fragment
import namit.retail_app.core.navigation.HomeNavigator
import namit.retail_app.home.presentation.home.HomeFragment
import namit.retail_app.home.presentation.profile.ProfileFragment
import namit.retail_app.order.presentation.order_list.OrderListFragment


class HomeNavigatorImpl: HomeNavigator {

    override fun getHomeFragment(): Fragment = HomeFragment.getNewInstance()

    override fun getOrderFragment(): Fragment = OrderListFragment.getNewInstance()

    override fun getProfileFragment(): Fragment = ProfileFragment.getInstance()
}
