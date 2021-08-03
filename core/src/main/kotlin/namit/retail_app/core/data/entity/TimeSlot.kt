package namit.retail_app.core.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeSlotWrapper(
    var canDeliveryNow: Boolean = false,
    var deliveryDates: List<TimeSlot> = listOf()
) : Parcelable

@Parcelize
data class TimeSlot(
    var date: String = "",
    var dayOfWeek: String = "",
    var isClose: Boolean? = null,
    var slots: List<SlotDetail> = listOf(),
    var isSelected: Boolean = false,
    var isFull: Boolean = false,
    var isDeliveryNow: Boolean = false
) : Parcelable

@Parcelize
data class SlotDetail(
    var hour: String = "",
    var pickupAt: String = "",
    var count: Int? = null,
    var isFull: Boolean = false,
    var isPick: Boolean = false,
    var shippingPrice: Double? = null,
    var isSelected: Boolean = false
) : Parcelable {
    fun isAvailable(): Boolean = !this.isFull && this.isPick
}


@Parcelize
open class BaseStoryContent(
    open var id: String? = null,
    open var title: String? = null,
    open var imageUrl: String? = null,
    open var createDate: String? = null,
    open var updateDate: String? = null
) : Parcelable