package namit.retail_app.core.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.dialog.alert.ConfirmDialog
import namit.retail_app.core.presentation.dialog.alert.QuestionDialog
import namit.retail_app.core.presentation.food_detail.FoodDetailDialog
import namit.retail_app.core.presentation.product_detail.ProductDetailDialog
import namit.retail_app.core.presentation.widget.LoadingDialog
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
interface CoreNavigator {
    fun getDeliveryTimeDialog(merchantId: String): DialogFragment

    fun getProductDetailDialog(
        product: ProductItem,
        merchantData: MerchantInfoItem
    ): ProductDetailDialog

    fun alertMessageDialog(title: String, message: String, buttonText: String): AlertMessageDialog

    fun alertQuestionDialog(title: String, message: String,
                            negativeButtonText: String, positiveButtonText: String): QuestionDialog

    fun alertConfirmDialog(title: String, message: String, negativeButtonText: String,
                           positiveButtonText: String): ConfirmDialog

    fun getFoodDetailDialog(product: ProductItem, merchantData: MerchantInfoItem): FoodDetailDialog

    fun openSearchActivity(context: Context, merchantInfoItem: MerchantInfoItem): Intent

    fun getLoadingDialog(haveBlurBackground: Boolean): LoadingDialog

    fun getSearchFragment(merchantInfoItem: MerchantInfoItem): Fragment
}