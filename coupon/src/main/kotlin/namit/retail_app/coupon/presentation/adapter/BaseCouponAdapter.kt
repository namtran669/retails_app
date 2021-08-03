package namit.retail_app.coupon.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.enums.CouponType

abstract class BaseCouponAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val VIEW_TYPE_SKELETON_LOADING = -1
    protected val VIEW_TYPE_UNKNOWN = 0
    protected val VIEW_TYPE_FIXED = 1
    protected val VIEW_TYPE_PERCENTAGE = 2
    protected val VIEW_TYPE_DELIVERY_FEE = 3

    protected fun getCouponItemViewType(couponType: CouponType): Int {
        return when (couponType) {
            CouponType.SKELETON_LOADING -> {
                VIEW_TYPE_SKELETON_LOADING
            }
            CouponType.FIXED -> {
                VIEW_TYPE_FIXED
            }
            CouponType.PERCENTAGE -> {
                VIEW_TYPE_PERCENTAGE
            }
            CouponType.DELIVERY_FEE -> {
                VIEW_TYPE_DELIVERY_FEE
            }
            else -> {
                VIEW_TYPE_UNKNOWN
            }
        }
    }

}