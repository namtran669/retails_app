package namit.retail_app.app.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.navigation.CoreNavigator
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import namit.retail_app.core.presentation.dialog.alert.ConfirmDialog
import namit.retail_app.core.presentation.dialog.alert.QuestionDialog
import namit.retail_app.core.presentation.dialog.delivery.DeliveryTimeDialog
import namit.retail_app.core.presentation.food_detail.FoodDetailDialog
import namit.retail_app.core.presentation.product_detail.ProductDetailDialog
import namit.retail_app.core.presentation.search.SearchActivity
import namit.retail_app.core.presentation.search.SearchFragment
import namit.retail_app.core.presentation.widget.LoadingDialog
import kotlinx.coroutines.ObsoleteCoroutinesApi

@UseExperimental(ObsoleteCoroutinesApi::class)
class CoreNavigatorImpl : CoreNavigator {
    override fun getProductDetailDialog(
        product: ProductItem,
        merchantData: MerchantInfoItem
    ): ProductDetailDialog {
        return ProductDetailDialog.newInstance(product, merchantData)
    }

    override fun getDeliveryTimeDialog(merchantId: String): DialogFragment {
        return DeliveryTimeDialog.newInstance(merchantId)
    }

    override fun getFoodDetailDialog(
        product: ProductItem,
        merchantData: MerchantInfoItem
    ): FoodDetailDialog {
        return FoodDetailDialog.newInstance(product, merchantData)
    }

    override fun alertMessageDialog(
        title: String,
        message: String,
        buttonText: String
    ): AlertMessageDialog = AlertMessageDialog.newInstance(title, message, buttonText)

    override fun alertQuestionDialog(
        title: String,
        message: String,
        negativeButtonText: String,
        positiveButtonText: String
    ): QuestionDialog =
        QuestionDialog.newInstance(title, message, negativeButtonText, positiveButtonText)

    override fun alertConfirmDialog(
        title: String,
        message: String,
        negativeButtonText: String,
        positiveButtonText: String
    ): ConfirmDialog {
        return ConfirmDialog.newInstance(
            title,
            message,
            negativeButtonText,
            positiveButtonText
        )
    }

    override fun openSearchActivity(context: Context, merchantInfoItem: MerchantInfoItem): Intent =
        SearchActivity.getStartIntent(context = context, merchantInfoItem = merchantInfoItem)

    override fun getLoadingDialog(haveBlurBackground: Boolean): LoadingDialog =
        LoadingDialog.newInstance(haveBlurBackground)

    override fun getSearchFragment(merchantInfoItem: MerchantInfoItem): Fragment =
        SearchFragment.getNewInstance(merchantInfoItem = merchantInfoItem)
}