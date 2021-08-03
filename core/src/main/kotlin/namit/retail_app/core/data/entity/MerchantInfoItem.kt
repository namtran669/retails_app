package namit.retail_app.core.data.entity

import android.os.Parcelable
import namit.retail_app.core.enums.MerchantType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantInfoItem(
    var id: String = "",
    var title: String = "",
    var imageUrl: String? = null,
    var cover: String? = null,
    var description: String? = null,
    var distance: Double = 0.0,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var openingPeriod: String? = null,
    var type: MerchantType = MerchantType.UNKNOWN
) : Parcelable


