package namit.retail_app.core.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import namit.retail_app.core.BuildConfig

fun Context.intentToSettingAppPermission() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    this.startActivity(intent)
}

fun Context.intentToPlayStore() {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(BuildConfig.URL_UPDATE_APP)
    startActivity(intent)
}