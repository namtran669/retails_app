package namit.retail_app.cart.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import namit.retail_app.cart.R
import namit.retail_app.cart.presentation.detail.CartDetailFragment
import namit.retail_app.core.navigation.CartNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import org.koin.android.ext.android.inject

class CartActivity : BaseActivity(){

    companion object {
        fun getStartIntent(context: Context): Intent =
            Intent(context, CartActivity::class.java)
    }

    private val cartNavigator: CartNavigator by inject()
    override var containerResId: Int = R.id.cartFragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        displayCartDetail()
    }

    private fun displayCartDetail() {
        val fragment =
            cartNavigator.getCartDetailFragment() as CartDetailFragment
        fragment.apply {
            addFragment(
                fragment = this,
                tag = CartDetailFragment.TAG,
                addToBackStack = true
            )
        }
    }
}