package namit.retail_app.core.navigation

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.data.entity.MerchantInfoItem

interface CouponNavigator {
    fun getCouponFragment(): Fragment

    fun openCouponDetail(couponModel: CouponModel): DialogFragment

    fun openCouponList(merchantInfoItemList: List<MerchantInfoItem>): DialogFragment

    fun openCouponList(cartId: Int): DialogFragment

    fun openPromoCodeDialog(cartId: Int): DialogFragment
}