package namit.retail_app.core.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager


private val WIDTH = 0
private val HEIGHT = 1

fun getScreenSize(context: Context): IntArray {
    val metrics = DisplayMetrics()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(metrics)
    val screenValue = IntArray(2)
    screenValue[WIDTH] = metrics.widthPixels
    screenValue[HEIGHT] = metrics.heightPixels
    return screenValue
}

fun getWidthScreenSize(context: Context) : Int {
    return getScreenSize(context)[WIDTH]
}

fun getHeightScreenSize(context: Context) : Int {
    return getScreenSize(context)[HEIGHT]
}