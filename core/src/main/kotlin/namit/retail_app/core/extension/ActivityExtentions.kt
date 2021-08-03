package namit.retail_app.core.extension

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.provider.Settings

fun Activity.intentToSettingLocation(requestCode: Int) {
    this.startActivityForResult(
        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
        requestCode
    )
}

fun Activity.intentToSettingAppPermission(requestCode: Int) {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    this.startActivityForResult(intent, requestCode)
}

fun Activity.getScreenHeight(percent: Double): Int {
    val display = this.windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return (size.y * percent).toInt()
}