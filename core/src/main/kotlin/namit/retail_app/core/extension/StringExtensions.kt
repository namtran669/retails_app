package namit.retail_app.core.extension

import android.telephony.PhoneNumberUtils
import android.util.Base64
import android.util.Log
import namit.retail_app.core.config.BASE_IMAGE_URL
import namit.retail_app.core.config.GRAPHQL_STRAPI_API_URL
import java.nio.charset.StandardCharsets
import java.util.*

fun String.toGraphQLStrapiImageUrl(): String {
    return GRAPHQL_STRAPI_API_URL.replace("/graphql", this)
}

fun String.toPhoneNumberPattern(): String {
    return PhoneNumberUtils.formatNumber(this, Locale.getDefault().country)
}

fun String.toCreditCardNumberPattern(): String {
    return this.replace("....".toRegex(), "$0 ")
}

fun String.toThaiCurrency(): String {
    return "฿$this"
}

fun String.applyWithBaseUrl(): String {
    return BASE_IMAGE_URL.plus(this)
}

fun String.removeNationNumber(): String {
    return "0${subSequence(2, length)}"
}

fun String.toOrderDeliveryTime(): String {
    return try {
        val currentDate = this.convertToDate(format = DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM_SS)

        var dayMonthTxt = ""
        if (!currentDate.checkTodayTomorrowTime()) {
            dayMonthTxt = " ${currentDate.convertToString(DATE_TIME_FORMAT_DD_MMM_YYYY_HH_MM)}"
        }

        "${currentDate.convertTodayTomorrowTime(DAY_OF_WEEK_FULL)}${dayMonthTxt}"
    } catch (ex: Exception) {
        Log.e("To Delivery Time", "Can not convert delivery time", ex)
        ""
    }
}

fun String.base64ToPlain(): String {
    val data: ByteArray = Base64.decode(this, Base64.DEFAULT)
    return String(data, StandardCharsets.UTF_8)
}