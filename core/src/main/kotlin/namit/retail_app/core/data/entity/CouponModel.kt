package namit.retail_app.core.data.entity

import android.os.Parcelable
import namit.retail_app.core.enums.CouponType
import namit.retail_app.core.enums.MerchantType
import kotlinx.android.parcel.Parcelize

@Parcelize
class CouponModel(
    var code: String? = null, // WF1snvfplC
    var name: String? = null, // "Get Free Delivery on $100 purchase"
    var description: String? = null, // "Get Free Delivery on purchase above $100",
    var isFlashDeals: Boolean = false,
    var isRanOut: Boolean = false,
    var endDate: Long? = null,
    var endTime: String? = null,
    var merchantInfoItem: MerchantInfoItem? = null,
    var couponValue: String? = null,
    var couponType: CouponType = CouponType.SKELETON_LOADING,
    var couponMerchantType: MerchantType = MerchantType.GROCERY
) : Parcelable