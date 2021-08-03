package namit.retail_app.core.extension

import android.widget.ImageView
import androidx.annotation.DrawableRes
import namit.retail_app.core.utils.GlideApp

fun ImageView.loadCircleImage(imageUrl: String, @DrawableRes placeHolder: Int = 0) {
    GlideApp.with(context)
        .load(imageUrl)
        .placeholder(placeHolder)
        .circleCrop()
        .into(this)
}

fun ImageView.loadImage(imageUrl: String? = "", placeHolder: Int = 0) {
    GlideApp.with(context)
        .load(imageUrl)
        .placeholder(placeHolder)
        .into(this)
}

fun ImageView.loadCircleImage(imageRes: Int, @DrawableRes placeHolder: Int = 0) {
    GlideApp.with(context)
        .load(imageRes)
        .placeholder(placeHolder)
        .circleCrop()
        .into(this)
}

fun ImageView.disable() {
    isEnabled = false
    this.alpha = 0.3F
}

fun ImageView.enable() {
    isEnabled = true
    this.alpha = 1.0F
}

