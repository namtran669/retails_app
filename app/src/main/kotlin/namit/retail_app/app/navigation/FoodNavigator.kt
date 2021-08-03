package namit.retail_app.app.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.navigation.FoodNavigator
import namit.retail_app.food.presentation.main.FoodActivity
import namit.retail_app.food.presentation.restaurant.RestaurantDetailFragment
import namit.retail_app.food.presentation.restaurant.menu.MenuPageFragment
import namit.retail_app.food.presentation.vertical.FoodVerticalFragment

class FoodNavigatorImpl : FoodNavigator {
    override fun getFoodActivity(context: Context): Intent {
        return FoodActivity.getStartIntent(context)
    }

    override fun getFoodVerticalFragment(): Fragment {
        return FoodVerticalFragment.getNewInstance()
    }

    override fun getRestaurantDetailFragment(restaurantData: MerchantInfoItem): Fragment {
        return RestaurantDetailFragment.getNewInstance(restaurantData)
    }

    override fun getMenuPageFragment(
        restaurantData: MerchantInfoItem,
        category: CategoryItem
    ): Fragment {
        return MenuPageFragment.getNewInstance(
            restaurantData = restaurantData,
            categoryData = category
        )
    }
}