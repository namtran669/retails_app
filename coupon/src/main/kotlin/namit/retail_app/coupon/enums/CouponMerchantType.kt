package namit.retail_app.coupon.enums

/*
Green = Convenience
Pink = Food
Blue = Grocery
Red = Cafe
*/
enum class CouponMerchantType(val value: Int) {
    GROCERY(0), FOOD(1), CONVENIENCE(2), CAFE(3)
}