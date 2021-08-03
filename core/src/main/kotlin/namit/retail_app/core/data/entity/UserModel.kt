package namit.retail_app.core.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    var id: Int = -1,
    var mobile: String = "",
    var accessToken: String? = null
) : Parcelable