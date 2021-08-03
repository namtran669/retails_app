package namit.retail_app.coupon.data.entity

import namit.retail_app.coupon.enums.CouponFilterType

data class CouponFilterModel(
    var id: Int,
    var nameEn: String = "",
    var nameTh: String = "",
    var slug: String = "",
    var filterType: CouponFilterType = CouponFilterType.UNKNOWN,
    var isSelected: Boolean = false,
    var name: String
)