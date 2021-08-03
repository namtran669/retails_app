package namit.retail_app.core.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TextMultiLanguage(
    var th: String? = null,
    var en: String? = null
): Parcelable