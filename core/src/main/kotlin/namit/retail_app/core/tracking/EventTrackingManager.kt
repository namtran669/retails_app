package namit.retail_app.core.tracking

import android.content.Context
import android.os.Bundle
import namit.retail_app.core.enums.CouponType
import namit.retail_app.core.enums.MerchantType
import namit.retail_app.core.enums.PaymentType
import namit.retail_app.core.enums.StoryContentType
import com.google.firebase.analytics.FirebaseAnalytics
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.CurrencyType
import java.util.*

interface EventTrackingManager {
    fun trackLoggedIn(userId: Int, loginMethod: String, phoneNumber: String)

    fun trackLoggedOut(userId: Int, loginMethod: String, phoneNumber: String)

    fun trackLocationService(available: String)

    fun trackVertical(merchantType: MerchantType)

    fun trackCouponView(couponType: CouponType)

    fun trackCouponRedeem(
        userId: Int,
        amountDiscount: Double,
        couponFormat: String,
        couponType: CouponType
    )

    fun trackMerchantView(
        merchantId: String,
        merchantName: String
    )

    fun trackCategory(
        merchantId: String,
        merchantName: String,
        categoryId: Int,
        categoryName: String
    )

    fun trackMerchantTimeSlotSelection(timeSlotType: String, userId: Int)

    fun trackAddToCart(
        productId: Int,
        productName: String,
        merchantId: String,
        merchantName: String,
        quantity: Int,
        categoryIds: List<Int>,
        options: List<String>
    )

    fun trackCartView(
        cartId: List<Int>,
        productId: List<Int>,
        productName: List<String>,
        merchantId: List<String>,
        merchantName: List<String>,
        categoryIds: List<List<Int>>
    )

    fun trackCreateOrder(
        paymentStatus: String,
        paymentMethod: PaymentType,
        orderAmount: Double,
        userId: Int,
        orderId: String,
        couponCode: String,
        timeSlot: String,
        location: String
    )

    fun trackHelpCenter()

    fun trackCMSView(storyContentType: StoryContentType)

    fun trackSearch(merchantId: String, merchantName: String, foundItem: Boolean)

    fun trackProductView(merchantId: String, merchantName: String, productId: Int, productName: String)
}

class EventTrackingManagerImpl(private val context: Context,
                               private val firebaseAnalytics: FirebaseAnalytics) : EventTrackingManager {

    private fun trackEvent(
        context: Context,
        eventName: String,
        eventValues: HashMap<String, Any>,
        eventContentItem: BranchUniversalObject? = null
    ) {
        val bundleDataForFireBase = Bundle()
        val branchEvent = BranchEvent(eventName)
        eventValues.forEach {
            bundleDataForFireBase.putString(it.key, it.value.toString())
            branchEvent.addCustomDataProperty(it.key, it.value.toString())
        }
        eventContentItem?.let {
            branchEvent.addContentItems(it)
        }
        branchEvent.logEvent(context)
        firebaseAnalytics.logEvent(eventName, bundleDataForFireBase)
    }

    private fun trackEvent(
        context: Context,
        eventName: String,
        eventValues: HashMap<String, Any>,
        eventContentItem: BranchUniversalObject? = null,
        revenue: Double
    ) {
        val bundleDataForFireBase = Bundle()
        val branchEvent = BranchEvent(eventName)
        eventValues.forEach {
            bundleDataForFireBase.putString(it.key, it.value.toString())
            branchEvent.addCustomDataProperty(it.key, it.value.toString())
        }
        eventContentItem?.let {
            branchEvent.addContentItems(it)
        }
        branchEvent.setCurrency(CurrencyType.THB)
        branchEvent.setRevenue(revenue)
        branchEvent.logEvent(context)

        firebaseAnalytics.logEvent(eventName, bundleDataForFireBase)
    }

    override fun trackLoggedIn(userId: Int, loginMethod: String, phoneNumber: String) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_AUTHENTICATION
        eventValue[TrackingProperty.KEY_USER_ID] = userId
        eventValue[TrackingProperty.KEY_USER_ID] = userId
        eventValue[TrackingProperty.KEY_LOGIN_METHOD] = loginMethod
        eventValue[TrackingProperty.KEY_PHONE_NUMBER] = phoneNumber

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_LOGGED_ID,
            eventValues = eventValue
        )
    }

    override fun trackLoggedOut(userId: Int, loginMethod: String, phoneNumber: String) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_AUTHENTICATION
        eventValue[TrackingProperty.KEY_USER_ID] = userId
        eventValue[TrackingProperty.KEY_LOGIN_METHOD] = loginMethod
        eventValue[TrackingProperty.KEY_PHONE_NUMBER] = phoneNumber

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_LOGGED_OUT,
            eventValues = eventValue
        )
    }

    override fun trackLocationService(available: String) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_LOCATION
        eventValue[TrackingProperty.KEY_AVAILABLE] = available

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_SERVICE_LOCATION,
            eventValues = eventValue
        )
    }

    override fun trackVertical(merchantType: MerchantType) {
        if (merchantType != MerchantType.UNKNOWN || merchantType != MerchantType.MERCHANT) {
            val eventValue = HashMap<String, Any>()
            eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_VERTICAL
            eventValue[TrackingProperty.KEY_VERTICAL_TYPE] =
                when (merchantType) {
                    MerchantType.CONVENIENCE -> {
                        TrackingValue.VALUE_VERTICAL_CONVENIENCE_STORE
                    }
                    MerchantType.GROCERY -> {
                        TrackingValue.VALUE_VERTICAL_GROCERY
                    }
                    MerchantType.RESTAURANT -> {
                        TrackingValue.VALUE_VERTICAL_FOOD
                    }
                    else -> {
                        TrackingValue.VALUE_VERTICAL_CAFE
                    }
                }

            trackEvent(
                context = context,
                eventName = TrackingEvent.EVENT_VERTICAL_VIEW,
                eventValues = eventValue
            )
        }
    }

    override fun trackCouponView(couponType: CouponType) {
        if (couponType != CouponType.UNKNOWN
            || couponType != CouponType.SKELETON_LOADING
        ) {
            val eventValue = HashMap<String, Any>()
            eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_COUPONS
            eventValue[TrackingProperty.KEY_COUPON_TYPE] =
                when (couponType) {
                    CouponType.FIXED -> {
                        TrackingValue.VALUE_COUPON_FIXED
                    }
                    CouponType.PERCENTAGE -> {
                        TrackingValue.VALUE_COUPON_PERCENTAGE
                    }
                    else -> {
                        TrackingValue.VALUE_COUPON_DELIVERY
                    }
                }

            trackEvent(
                context = context,
                eventName = TrackingEvent.EVENT_COUPON_VIEW,
                eventValues = eventValue
            )
        }
    }

    override fun trackCouponRedeem(
        userId: Int,
        amountDiscount: Double,
        couponFormat: String,
        couponType: CouponType
    ) {
        if (couponType != CouponType.UNKNOWN
            || couponType != CouponType.SKELETON_LOADING
        ) {
            val eventValue = HashMap<String, Any>()
            eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_COUPONS
            eventValue[TrackingProperty.KEY_USER_ID] = userId
            eventValue[TrackingProperty.KEY_AMOUNT_DISCOUNT] = amountDiscount
            eventValue[TrackingProperty.KEY_COUPON_FORMAT] = couponFormat
            eventValue[TrackingProperty.KEY_COUPON_TYPE] =
                when (couponType) {
                    CouponType.FIXED -> {
                        TrackingValue.VALUE_COUPON_FIXED
                    }
                    CouponType.PERCENTAGE -> {
                        TrackingValue.VALUE_COUPON_PERCENTAGE
                    }
                    else -> {
                        TrackingValue.VALUE_COUPON_DELIVERY
                    }
                }

            trackEvent(
                context = context,
                eventName = TrackingEvent.EVENT_COUPON_REDEMPTION,
                eventValues = eventValue
            )
        }
    }

    override fun trackMerchantView(
        merchantId: String,
        merchantName: String
    ) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_MERCHANT
        eventValue[TrackingProperty.KEY_MERCHANT_ID] = merchantId
        eventValue[TrackingProperty.KEY_MERCHANT_NAME] = merchantName

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_GO_TO_MERCHANT,
            eventValues = eventValue
        )
    }

    override fun trackCategory(
        merchantId: String,
        merchantName: String,
        categoryId: Int,
        categoryName: String
    ) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_CATEGORY
        eventValue[TrackingProperty.KEY_MERCHANT_ID] = merchantId
        eventValue[TrackingProperty.KEY_MERCHANT_NAME] = merchantName
        eventValue[TrackingProperty.KEY_CATEGORY_ID] = categoryId
        eventValue[TrackingProperty.KEY_CATEGORY_NAME] = categoryName

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_CATEGORY,
            eventValues = eventValue
        )
    }

    override fun trackMerchantTimeSlotSelection(timeSlotType: String, userId: Int) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_MERCHANT
        eventValue[TrackingProperty.KEY_TIME_SLOT_TYPE] = timeSlotType
        eventValue[TrackingProperty.KEY_USER_ID] = userId

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_TIME_SLOT_SELECTION,
            eventValues = eventValue
        )
    }

    override fun trackAddToCart(
        productId: Int,
        productName: String,
        merchantId: String,
        merchantName: String,
        quantity: Int,
        categoryIds: List<Int>,
        options: List<String>
    ) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_CART
        eventValue[TrackingProperty.KEY_PRODUCT_ID] = productId
        eventValue[TrackingProperty.KEY_PRODUCT_NAME] = productName
        eventValue[TrackingProperty.KEY_MERCHANT_ID] = merchantId
        eventValue[TrackingProperty.KEY_MERCHANT_NAME] = merchantName
        eventValue[TrackingProperty.KEY_QUANTITY] = quantity
        eventValue[TrackingProperty.KEY_CATEGORY_ID] = categoryIds
        eventValue[TrackingProperty.KEY_OPTIONS] = options
        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_ADD_TO_CART,
            eventValues = eventValue
        )
    }

    override fun trackCartView(
        cartId: List<Int>,
        productId: List<Int>,
        productName: List<String>,
        merchantId: List<String>,
        merchantName: List<String>,
        categoryIds: List<List<Int>>
    ) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_CART
        eventValue[TrackingProperty.KEY_CART_ID] = cartId
        eventValue[TrackingProperty.KEY_PRODUCT_ID] = productId
        eventValue[TrackingProperty.KEY_PRODUCT_NAME] = productName
        eventValue[TrackingProperty.KEY_MERCHANT_ID] = merchantId
        eventValue[TrackingProperty.KEY_MERCHANT_NAME] = merchantName
        eventValue[TrackingProperty.KEY_CATEGORY_ID] = categoryIds

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_CART_VIEW,
            eventValues = eventValue
        )
    }

    override fun trackCreateOrder(
        paymentStatus: String,
        paymentMethod: PaymentType,
        orderAmount: Double,
        userId: Int,
        orderId: String,
        couponCode: String,
        timeSlot: String,
        location: String
    ) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_ORDER
        eventValue[TrackingProperty.KEY_PAYMENT_STATUS] = paymentStatus
        eventValue[TrackingProperty.KEY_ORDER_AMOUNT] = orderAmount
        eventValue[TrackingProperty.KEY_USER_ID] = userId
        eventValue[TrackingProperty.KEY_ORDER_ID] = orderId
        eventValue[TrackingProperty.KEY_COUPON_CODE] = couponCode
        eventValue[TrackingProperty.KEY_TIME_SLOT_TYPE] = timeSlot
        eventValue[TrackingProperty.KEY_LOCATION] = location
        eventValue[TrackingProperty.KEY_PAYMENT_METHOD] =
            when (paymentMethod) {
                PaymentType.TRUE_MONEY -> {
                    TrackingValue.VALUE_PAYMENT_METHOD_TMW
                }
                PaymentType.CREDIT_CARD -> {
                    TrackingValue.VALUE_PAYMENT_METHOD_CREDIT_CARD
                }
                else -> {
                    TrackingValue.VALUE_PAYMENT_METHOD_COD
                }
            }

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_CREATE_ORDER,
            eventValues = eventValue,
            revenue = orderAmount
        )
    }

    override fun trackHelpCenter() {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_CUSTOMER_SERVICE
        eventValue[TrackingProperty.KEY_HELP_VIEW] = ""

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_HELP_CENTER,
            eventValues = eventValue
        )
    }

    override fun trackCMSView(storyContentType: StoryContentType) {
        if (storyContentType != StoryContentType.UNKNOWN) {
            val eventValue = HashMap<String, Any>()
            eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_CMS
            eventValue[TrackingEvent.EVENT_CMS_VIEW] =
                when (storyContentType) {
                    StoryContentType.ANNOUCEMENT -> {
                        TrackingValue.VALUE_CMS_ANNOUNCEMENTS
                    }
                    StoryContentType.STORY -> {
                        TrackingValue.VALUE_CMS_STORIES
                    }
                    else -> {
                        TrackingValue.VALUE_CMS_PROMOTION
                    }
                }

            trackEvent(
                context = context,
                eventName = TrackingEvent.EVENT_CMS_VIEW,
                eventValues = eventValue
            )
        }
    }

    override fun trackSearch(merchantId: String, merchantName: String, foundItem: Boolean) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_SEARCH
        eventValue[TrackingProperty.KEY_MERCHANT_ID] = merchantId
        eventValue[TrackingProperty.KEY_MERCHANT_NAME] = merchantName
        eventValue[TrackingProperty.KEY_FOUND_ITEM] =
            if (foundItem) TrackingValue.VALUE_YES else TrackingValue.VALUE_NO

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_SEARCH,
            eventValues = eventValue
        )
    }

    override fun trackProductView(
        merchantId: String,
        merchantName: String,
        productId: Int,
        productName: String
    ) {
        val eventValue = HashMap<String, Any>()
        eventValue[TrackingModule.MODULE] = TrackingModule.MODULE_PRODUCT
        eventValue[TrackingProperty.KEY_MERCHANT_ID] = merchantId
        eventValue[TrackingProperty.KEY_MERCHANT_NAME] = merchantName
        eventValue[TrackingProperty.KEY_PRODUCT_ID] = productId
        eventValue[TrackingProperty.KEY_PRODUCT_NAME] = productName

        trackEvent(
            context = context,
            eventName = TrackingEvent.EVENT_PRODUCT_VIEW,
            eventValues = eventValue
        )
    }
}