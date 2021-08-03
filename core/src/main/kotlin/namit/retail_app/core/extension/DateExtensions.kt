package namit.retail_app.core.extension

import android.annotation.SuppressLint
import namit.retail_app.core.utils.LocaleUtils
import java.text.SimpleDateFormat
import java.util.*

const val DATE_TIME_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
const val DATE_TIME_FORMAT_DD_MMM_YYYY = "dd MMM yyyy"
const val DATE_TIME_FORMAT_DD_MMM_YYYY_HH_MM = "dd MMM yyyy, HH:mm"
const val DATE_TIME_FORMAT_MMM_DD = "MMM dd"
const val DAY_OF_WEEK_FULL = "EEEE"
const val DAY_OF_WEEK_SHORT = "EEE"
const val DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val DATE_TIME_FORMAT_HH_MM = "HH:mm"

@SuppressLint("SimpleDateFormat")
fun Date.convertTodayTomorrowTime(format: String): String {
    val sdf = SimpleDateFormat(format)
    val dayOfWeek = sdf.format(this)

    val calendar = Calendar.getInstance()
    calendar.time = this
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val curDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    return when {
        (dayOfMonth - curDayOfMonth) == 0 -> {
            if (LocaleUtils.isThai()) {
                "ในวันนี้"
            } else {
                "Today"
            }
        }
        (dayOfMonth - curDayOfMonth) == 1 -> {
            if (LocaleUtils.isThai()) {
                "วันพรุ่งนี้"
            } else {
                "Tomorrow"
            }
        }
        else -> {
            dayOfWeek
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun Date.checkTodayTomorrowTime(): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val curDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    return when {
        (dayOfMonth - curDayOfMonth) == 0 -> {
            true
        }
        (dayOfMonth - curDayOfMonth) == 1 -> {
            true
        }
        else -> {
            false
        }
    }
}

fun String.convertProductDetailPriceNoteTime(): String {
    var dateString = ""
    if (contains("T")) {
        val dateTimeArray = split("T")
        val locale = LocaleUtils.getCurrentLocale()
        val sdf = SimpleDateFormat("yyyy-MM-dd", locale)
        val serverTime = sdf.parse(dateTimeArray[0])
        sdf.applyPattern("dd MMM yyyy")
        val dateText = sdf.format(serverTime)
        dateString =
            serverTime.convertTodayTomorrowTime(DAY_OF_WEEK_FULL) + " - " + dateText
    }
    return dateString
}

fun String.convertToDateAndApplyFormat(fromFormat: String, toFormat: String): String {
    val locale = LocaleUtils.getCurrentLocale()
    val simpleDateFormat = SimpleDateFormat(fromFormat, locale)
    val dateTime = simpleDateFormat.parse(this)
    simpleDateFormat.applyPattern(toFormat)
    return simpleDateFormat.format(dateTime)
}

fun String.convertToDateFromUTCTime(fromFormat: String, toFormat: String): String {
    val fromSDF = SimpleDateFormat(fromFormat, LocaleUtils.getCurrentLocale())
    fromSDF.timeZone = TimeZone.getTimeZone("UTC")
    val utcTime = fromSDF.parse(this)
    val toSDF = SimpleDateFormat(toFormat, LocaleUtils.getCurrentLocale())
    val newTime = toSDF.format(utcTime)
    return newTime
}

fun String.convertToDate(format: String): Date {
    val locale = LocaleUtils.getCurrentLocale()
    val simpleDateFormat = SimpleDateFormat(format, locale)
    return simpleDateFormat.parse(this)
}

fun Date.convertToString(format: String): String {
    val locale = LocaleUtils.getCurrentLocale()
    val simpleDateFormat = SimpleDateFormat(format, locale)
    return simpleDateFormat.format(this)
}
