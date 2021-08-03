package namit.retail_app.core.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem

interface GroceryNavigator {

    fun getGroceryWarningDialog(): DialogFragment

    fun getGroceryMerchantFragment(merchant: MerchantInfoItem): Fragment

    fun getGroceryMainFragment(): Fragment

    fun getGroceryCategoryDetailFragment(selectedCategory: CategoryItem, merchantData: MerchantInfoItem): Fragment

    fun getGroceryWrapperActivity(context: Context): Intent

    fun getGroceryActivityWithMerchantInfo(context: Context, merchant: MerchantInfoItem): Intent

    fun getGroceryCategoryAllFragment(merchantInfoItem: MerchantInfoItem): Fragment

    fun getGrocerySubCategoryFragment(
        merchantInfoItem: MerchantInfoItem,
        parentCategory: CategoryItem,
        childCategoryList: List<CategoryItem>
    ): Fragment

    fun getGrocerySubCategoryDetailFragment(
        merchantInfoItem: MerchantInfoItem,
        parentCategory: CategoryItem,
        selectedCategory: CategoryItem
    ): Fragment
}