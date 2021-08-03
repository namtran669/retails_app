package namit.retail_app.coupon.data.entity

import namit.retail_app.core.data.entity.BaseShelf
import namit.retail_app.core.data.entity.CouponModel

class CouponShelfModel : BaseShelf() {
    var hasSeeAll: Boolean = false
    var contentList: List<CouponModel> = listOf()
}