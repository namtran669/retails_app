package namit.retail_app.app.navigation

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.navigation.CouponNavigator
import namit.retail_app.coupon.presentation.detail.CouponDetailDialogFragment
import namit.retail_app.coupon.presentation.dialog.CouponMerchantListDialogFragment
import namit.retail_app.coupon.presentation.promo_code.PromoCodeDialog
import namit.retail_app.home.presentation.coupon.CouponFragment

class CouponNavigatorImpl : CouponNavigator {

    override fun getCouponFragment(): Fragment = CouponFragment.getNewInstance()

    override fun openCouponDetail(couponModel: CouponModel): DialogFragment {
        return CouponDetailDialogFragment.newInstance(couponModel)
    }

    override fun openCouponList(merchantInfoItemList: List<MerchantInfoItem>): DialogFragment {
        return CouponMerchantListDialogFragment.newInstance(merchantInfoItemList)
    }

    override fun openCouponList(cartId: Int): DialogFragment {
        return CouponMerchantListDialogFragment.newInstance(cartId)
    }

    override fun openPromoCodeDialog(cartId: Int): DialogFragment {
        return PromoCodeDialog.newInstance(cartId)
    }
}