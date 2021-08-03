package namit.retail_app.core.tracking

class TrackingModule {
    companion object {
        const val MODULE = "Module"
        const val MODULE_AUTHENTICATION     = "Authentication"
        const val MODULE_LOCATION           = "Location"
        const val MODULE_VERTICAL           = "Vertical"
        const val MODULE_COUPONS            = "Coupons"
        const val MODULE_MERCHANT           = "Merchant"
        const val MODULE_CATEGORY           = "Category"
        const val MODULE_CART               = "Cart"
        const val MODULE_ORDER              = "Order"
        const val MODULE_CUSTOMER_SERVICE   = "Customer Service"
        const val MODULE_CMS                = "CMS"
        const val MODULE_SEARCH             = "Search"
        const val MODULE_PRODUCT            = "Product"
    }
}

class TrackingEvent {
    companion object {
        const val EVENT_LOGGED_ID           = "Logged In"
        const val EVENT_SERVICE_LOCATION    = "Service Location"
        const val EVENT_VERTICAL_VIEW       = "Vertical view"
        const val EVENT_COUPON_VIEW         = "Coupon view"
        const val EVENT_COUPON_REDEMPTION   = "Coupon redemption"
        const val EVENT_GO_TO_MERCHANT      = "Go to Merchant"
        const val EVENT_CATEGORY            = "Category"
        const val EVENT_TIME_SLOT_SELECTION = "Time slot selection"
        const val EVENT_ADD_TO_CART         = "Add to cart"
        const val EVENT_CART_VIEW           = "Cart View"
        const val EVENT_CREATE_ORDER        = "Create Order"
        const val EVENT_LOGGED_OUT          = "Logged out"
        const val EVENT_HELP_CENTER         = "Help Center"
        const val EVENT_CMS_VIEW            = "CMS View"
        const val EVENT_SEARCH              = "Search"
        const val EVENT_PRODUCT_VIEW        = "Product_view"
    }
}

class TrackingProperty {
    companion object {
        //---CMS---
        const val KEY_CMS_TYPE              = "CMS_type"
        //---AUTH---
        const val KEY_USER_ID               = "user_id"
        const val KEY_LOGIN_METHOD          = "login_method"
        const val KEY_PHONE_NUMBER          = "phonenumber"
        //---LOCATION---
        const val KEY_AVAILABLE             = "available"
        //---VERTICAL---
        const val KEY_VERTICAL_TYPE         = "vertical_type"
        //---COUPON---
        const val KEY_COUPON_TYPE           = "coupon_type"
        const val KEY_COUPON_FORMAT         = "coupon_format"
        const val KEY_AMOUNT_DISCOUNT       = "amount_discount"
        //---MERCHANT---
        const val KEY_CATEGORY_ID           = "category_id"
        const val KEY_CATEGORY_NAME         = "category_name"
        const val KEY_TIME_SLOT_TYPE        = "time_slot_type"
        //---CART---
        const val KEY_CART_ID               = "cart_id"
        const val KEY_PRODUCT_ID            = "product_id"
        const val KEY_PRODUCT_NAME          = "product_name"
        const val KEY_MERCHANT_ID           = "merchant_id"
        const val KEY_MERCHANT_NAME         = "merchant_name"
        const val KEY_QUANTITY              = "quantity"
        const val KEY_OPTIONS               = "options"
        const val KEY_FOUND_ITEM            = "founditem"
        //---ORDER---
        const val KEY_PAYMENT_STATUS        = "payment_status"
        const val KEY_PAYMENT_METHOD        = "payment_method"
        const val KEY_ORDER_AMOUNT          = "order_amount"
        const val KEY_ORDER_ID              = "order_id"
        const val KEY_COUPON_CODE           = "coupon_code"
        const val KEY_LOCATION              = "location"
        const val KEY_HELP_VIEW             = "help_view"
    }
}

class TrackingValue {
    companion object {
        const val VALUE_LOGGED_IN_TYPE_TRUE_ID      = "TrueID"
        const val VALUE_LOGGED_IN_TYPE_PHONE_NUMBER = "Phonenumber"
        const val VALUE_YES                         = "yes"
        const val VALUE_NO                          = "no"
        const val VALUE_VERTICAL_CONVENIENCE_STORE  = "Convenience store"
        const val VALUE_VERTICAL_GROCERY            = "Grocery"
        const val VALUE_VERTICAL_CAFE               = "Cafe"
        const val VALUE_VERTICAL_FOOD               = "Food"
        const val VALUE_COUPON_FIXED                = "Fixed"
        const val VALUE_COUPON_PERCENTAGE           = "Percentage"
        const val VALUE_COUPON_DELIVERY             = "Delivery"
        const val VALUE_TIME_SLOT_DELIVERY_NOW      = "Delivery now"
        const val VALUE_TIME_SLOT_SCHEDULED         = "scheduled"
        const val VALUE_CAMPAIGN_PROMO_CODE         = "Promo Code"
        const val VALUE_CAMPAIGN_COLLECTABLE        = "Collectable"
        const val VALUE_CMS_ANNOUNCEMENTS           = "Announcements"
        const val VALUE_CMS_PROMOTION               = "Promotion"
        const val VALUE_CMS_STORIES                 = "Stories"

        //Success, pending, error,
        const val VALUE_PAYMENT_STATUS_SUCCESS      = "Success"
        const val VALUE_PAYMENT_STATUS_PENDING      = "pending"
        const val VALUE_PAYMENT_STATUS_ERROR        = "error"

        const val VALUE_PAYMENT_METHOD_COD          = "cod"
        const val VALUE_PAYMENT_METHOD_DEBIT_CARD   = "debit card"
        const val VALUE_PAYMENT_METHOD_CREDIT_CARD  = "credit card"
        const val VALUE_PAYMENT_METHOD_TMW          = "TMW"
    }
}