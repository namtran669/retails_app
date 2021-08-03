package namit.retail_app.app.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.navigation.GroceryNavigator
import namit.retail_app.grocery.presentation.category_all.GroceryAllCategoryFragment
import namit.retail_app.grocery.presentation.category_detail.GroceryCategoryDetailFragment
import namit.retail_app.grocery.presentation.category_sub.GrocerySubCategoryFragment
import namit.retail_app.grocery.presentation.category_sub_detail.GrocerySubCategoryDetailFragment
import namit.retail_app.grocery.presentation.main.GroceryMainFragment
import namit.retail_app.grocery.presentation.main_wrapper.GroceryWrapperActivity
import namit.retail_app.grocery.presentation.merchant.GroceryMerchantDetailFragment
import namit.retail_app.grocery.presentation.warning.GroceryWarningDialog

class GroceryNavigatorImpl : GroceryNavigator {

    override fun getGroceryWarningDialog(): DialogFragment {
        return GroceryWarningDialog.newInstance()
    }

    override fun getGroceryMerchantFragment(merchant: MerchantInfoItem): Fragment {
        return GroceryMerchantDetailFragment.getNewInstance(merchant)
    }

    override fun getGroceryMainFragment(): Fragment {
        return GroceryMainFragment.getNewInstance()
    }

    override fun getGroceryCategoryDetailFragment(
        selectedCategory: CategoryItem,
        merchantData: MerchantInfoItem
    ): Fragment {
        return GroceryCategoryDetailFragment.getNewInstance(selectedCategory, merchantData)
    }

    override fun getGroceryWrapperActivity(context: Context): Intent {
        return GroceryWrapperActivity.newInstance(context)
    }

    override fun getGroceryActivityWithMerchantInfo(
        context: Context,
        merchant: MerchantInfoItem
    ): Intent {
        return GroceryWrapperActivity.newInstanceWithMerchantInfo(context, merchant)
    }

    override fun getGroceryCategoryAllFragment(
        merchantInfoItem: MerchantInfoItem
    ): Fragment {
        return GroceryAllCategoryFragment.getNewInstance(merchantInfoItem)
    }

    override fun getGrocerySubCategoryFragment(
        merchantInfoItem: MerchantInfoItem,
        parentCategory: CategoryItem,
        childCategoryList: List<CategoryItem>
    ): Fragment {
        return GrocerySubCategoryFragment.getNewInstance(
            merchantInfoItem,
            parentCategory,
            childCategoryList
        )
    }

    override fun getGrocerySubCategoryDetailFragment(
        merchantInfoItem: MerchantInfoItem,
        parentCategory: CategoryItem,
        selectedCategory: CategoryItem
    ): Fragment {
        return GrocerySubCategoryDetailFragment.getNewInstance(
            merchantInfoItem = merchantInfoItem,
            parentCategory = parentCategory,
            selectedCategory = selectedCategory
        )
    }

}