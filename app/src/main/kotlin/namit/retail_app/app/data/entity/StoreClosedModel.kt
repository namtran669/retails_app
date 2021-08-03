package namit.retail_app.app.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreClosedModel(var titleTH: String = "",
                            var titleEN: String = "",
                            var messageTH: String = "",
                            var messageEN: String = "",
                            var status: Boolean = false): Parcelable