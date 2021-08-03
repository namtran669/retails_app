package namit.retail_app.core.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val DAY_OF_WEEK_FORMAT = "EEEE"

    @SuppressLint("SimpleDateFormat")
    fun convertTodayTomorrowTime(dateTime: Date): String {
        var dayOfWeekText = ""
        val locale = LocaleUtils.getCurrentLocale()
        val sdf = SimpleDateFormat(DAY_OF_WEEK_FORMAT)
        val dayOfWeek = sdf.format(dateTime)

        val calendar = Calendar.getInstance()
        calendar.time = dateTime
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val curDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        //todo Hard code today/tomorrow Thai language wait to clarify
        when {
            (dayOfMonth - curDayOfMonth) == 0 -> {
                if (locale.language.equals("en", true)) {
                    dayOfWeekText = "Today"
                } else if (locale.language.equals("th", true)) {
                    dayOfWeekText = "ในวันนี้"
                }
            }
            (dayOfMonth - curDayOfMonth) == 1 -> {
                if (locale.language.equals("en", true)) {
                    dayOfWeekText = "Tomorrow"
                } else if (locale.language.equals("th", true)) {
                    dayOfWeekText = "วันพรุ่งนี้"
                }
            }
            else -> {
                dayOfWeekText = dayOfWeek
            }
        }
        return dayOfWeekText
    }


    fun convertProductDetailPriceNoteTime(dateTime: String?): String {
        var dateString = ""
        if (dateTime?.contains("T") == true) {
            val dateTimeArray = dateTime.split("T")
            val locale = LocaleUtils.getCurrentLocale()
            val sdf = SimpleDateFormat("yyyy-MM-dd", locale)
            val serverTime = sdf.parse(dateTimeArray[0])
            sdf.applyPattern("dd MMM yyyy")
            val dateText = sdf.format(serverTime)
            dateString = convertTodayTomorrowTime(serverTime) + " - " + dateText
        }
        return dateString
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeStampFromDateTime(mDateTime: String?): Long? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+mm:ss")
        dateFormat.timeZone = TimeZone.getDefault()
        var date: Date
        try {
            date = dateFormat.parse(mDateTime)
            return date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

}
