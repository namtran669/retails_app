package namit.retail_app.core.enums

enum class CouponType(val value: String) {
    SKELETON_LOADING("SKELETON_LOADING"),
    UNKNOWN("UNKNOWN"),
    FIXED("FIXED"),
    PERCENTAGE("PERCENTAGE"),
    DELIVERY_FEE("DELIVERY_FEE")

    //TODO Improve more type later
//    FREEBIE(4),
//    CASH_BACK(5)
}