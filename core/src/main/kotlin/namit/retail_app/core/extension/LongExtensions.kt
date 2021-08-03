package namit.retail_app.core.extension

import android.text.format.DateFormat
import java.util.*
import java.util.zip.DataFormatException

fun Long.toDate(dateFormat: String): String {
    return try {
        val date = Date(this)
        DateFormat.format(dateFormat, date).toString()
    } catch (e: DataFormatException) {
        ""
    } catch (e: NullPointerException) {
        ""
    }
}