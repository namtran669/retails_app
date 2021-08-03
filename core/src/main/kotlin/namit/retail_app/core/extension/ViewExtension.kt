package namit.retail_app.core.extension

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import namit.retail_app.core.utils.SafeClickListener

inline fun Context.getStyledAttributes(
    set: AttributeSet?,
    attrs: IntArray,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
    func: TypedArray.() -> Unit
) {
    if (set == null) {
        return
    }

    val typedArray: TypedArray = obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes)
    try {
        typedArray.func()
    } finally {
        typedArray.recycle()
    }
}

fun View.expandWidth(duration: Int, targetWidth: Int) {
    val prevWidth = width
    visibility = View.VISIBLE
    val valueAnimator = ValueAnimator.ofInt(prevWidth, targetWidth)
    valueAnimator.addUpdateListener { animation ->
        layoutParams.width = animation.animatedValue as Int
        requestLayout()
    }
    val interpolator = AccelerateDecelerateInterpolator()
    valueAnimator.interpolator = interpolator
    valueAnimator.duration = duration.toLong()
    valueAnimator.start()
}

fun View.collageWidth(duration: Int, targetWidth: Int) {
    val prevWidth = width
    visibility = View.VISIBLE
    val valueAnimator = ValueAnimator.ofInt(prevWidth, targetWidth)
    valueAnimator.addUpdateListener { animation ->
        layoutParams.width = animation.animatedValue as Int
        requestLayout()
    }
    val interpolator = AccelerateDecelerateInterpolator()
    valueAnimator.interpolator = interpolator
    valueAnimator.duration = duration.toLong()
    valueAnimator.start()
}

fun View.setSafeOnClickListener(intervalTime: Int = 1000, onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener(defaultInterval = intervalTime) {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun View.visible() {
    visibility = View.VISIBLE
    isClickable = true
}

fun View.invisible() {
    visibility = View.INVISIBLE
    isClickable = false
}

fun View.gone() {
    visibility = View.GONE
    isClickable = false
}

fun View.visibleWhen(condition: Boolean) {
    if (condition) {
        visible()
    } else {
        gone()
    }
}

fun View.goneWhen(condition: Boolean) {
    if (condition) {
        gone()
    } else {
        visible()
    }
}

fun View.disable() {
    isEnabled = false
    alpha = 0.3f
}

fun View.enable() {
    isEnabled = true
    alpha = 1.0f
}

fun View.enableWhen(condition: Boolean) {
    if (condition) {
        enable()
    } else {
        disable()
    }
}

fun View.disableWhen(condition: Boolean) {
    if (condition) {
        disable()
    } else {
        enable()
    }
}

fun View.toGrayScale() {
    val grayScalePaint = Paint()
    val cm = ColorMatrix()
    cm.setSaturation(0F)
    grayScalePaint.colorFilter = ColorMatrixColorFilter(cm)
    this.setLayerType(View.LAYER_TYPE_HARDWARE, grayScalePaint)
}