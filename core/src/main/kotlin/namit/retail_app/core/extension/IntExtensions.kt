package namit.retail_app.core.extension

import android.content.res.Resources

fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

fun Int.pxToDp(): Int {
    return (this / Resources.getSystem().displayMetrics.density).toInt()
}

fun Int.toMillisecond(): Long {
    return this.times(1000).toLong()
}