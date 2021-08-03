package namit.retail_app.core.data.entity

import android.os.Parcelable
import namit.retail_app.core.enums.OrderStatus
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderModel(
    val total: Double? = null,
    val finalPrice: Double? = null,
    val createdAt: String? = null,
    val currentOrderStatus: OrderStatus = OrderStatus.UNKNOWN,
    val secureKey:String = "",
    val pickupAt: String? = "",
    val orderAddress: String? = null,
    val orderProduct: List<OrderProduct> = listOf(),
    val orderStoreInfo: OrderStoreInfo? = null,
    val deliveryFee: Double? = null,
    val orderPayment: OrderPayment? = null
): Parcelable

@Parcelize
data class OrderPayment(
    val paymentAmount: Double? = null,
    val discount: Double? = null,
    val brand: String? = null,
    val paymentMethod: String? = null
): Parcelable

@Parcelize
data class OrderStoreInfo(
    val storeId: String? = null,
    val storeName: String? = null,
    var cover: String? = null
): Parcelable

@Parcelize
data class OrderProduct(
    val title: String?,
    val quantity: Int?,
    val finalPrice: Double?,
    val productId: String?
): Parcelable

enum class OrderType(val value: String) {
    ON_GOING("ON_GOING"), COMPLETED ("COMPLETED")
}
