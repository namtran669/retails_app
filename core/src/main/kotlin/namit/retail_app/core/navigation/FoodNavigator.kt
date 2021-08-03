package namit.retail_app.core.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem

interface FoodNavigator {
    fun getFoodActivity(context: Context): Intent

    fun getFoodVerticalFragment(): Fragment

    fun getRestaurantDetailFragment(restaurantData: MerchantInfoItem): Fragment

    fun getMenuPageFragment(restaurantData: MerchantInfoItem, category: CategoryItem): Fragment

}