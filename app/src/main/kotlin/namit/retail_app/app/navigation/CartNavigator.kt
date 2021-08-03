package namit.retail_app.app.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import namit.retail_app.cart.presentation.detail.CartDetailFragment
import namit.retail_app.cart.presentation.main.CartActivity
import namit.retail_app.core.navigation.CartNavigator

class CartNavigatorImpl : CartNavigator {
    override fun getCartDetailFragment(): Fragment {
        return CartDetailFragment.newInstance()
    }

    override fun getCartActivity(context: Context): Intent {
       return CartActivity.getStartIntent(context)
    }
}