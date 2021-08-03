package namit.retail_app.food.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import namit.retail_app.core.navigation.FoodNavigator
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.food.R
import namit.retail_app.food.presentation.vertical.FoodVerticalFragment
import org.koin.android.ext.android.inject

class FoodActivity : BaseActivity() {
    companion object {
        fun getStartIntent(context: Context): Intent =
            Intent(context, FoodActivity::class.java)
    }

    private val foodNavigator: FoodNavigator by inject()
    override var containerResId: Int = R.id.foodFragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        displayFoodVertical()
    }

    private fun displayFoodVertical() {
        val fragment =
            foodNavigator.getFoodVerticalFragment() as FoodVerticalFragment
        fragment.apply {
            addFragment(
                fragment = this,
                tag = FoodVerticalFragment.TAG,
                addToBackStack = true
            )
        }
    }
}