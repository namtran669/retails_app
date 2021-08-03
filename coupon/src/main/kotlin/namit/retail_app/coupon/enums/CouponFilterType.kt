package namit.retail_app.coupon.enums

enum class CouponFilterType(val value: Int) {
    UNKNOWN(0),
    ALL(1),
    FLASH(2),
    SUPERMARKET(3),
    RESTAURANTS(4),
    CONVENIENCE_STORE(5),
    CAFE(6)
}