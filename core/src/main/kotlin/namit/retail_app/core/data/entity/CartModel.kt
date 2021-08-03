package namit.retail_app.core.data.entity

import android.os.Parcelable
import namit.retail_app.core.enums.CouponType
import hasura.type.CartItemOptionInput
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartModel(
    val totalPrice: Double = 0.0,
    val amount: Int = 0,
    val merchants: MutableList<CartMerchantModel> = mutableListOf()
) : Parcelable

@Parcelize
data class CartMerchantModel(
    val id: Int = -1,
    val userId: Int? = null,
    val secureId: String = "",
    val name: String? = null,
    var merchantId: String = "",
    var deliveryFee: Double = 0.0,
    var isSelected: Boolean = false,
    var products: MutableList<CartProductModel> = mutableListOf(),
    var merchant: MerchantInfoItem? = null
) : Parcelable

@Parcelize
data class CartProductModel(
    val id: Int = -1,
    var quantity: Int = 0,
    val product: ProductItem? = null,
    var isSwiped: Boolean = false
) : Parcelable

@Parcelize
data class CartItem(
    val id: Int = -1,
    val cartId: Int = 0,
    val createdAt: String = "",
    val note: String = "",
    val productId: Int = 0,
    val productOptions: OptionGroup,
    val quantity: Int = 0,
    val updatedAt: String = ""
) : Parcelable

data class CartItemInput(
    var productId: Int = 0,
    var cartItemId: Int = 0,
    var productOption: List<CartItemOptionInput>? = null,
    var note: String = "",
    var quantity: Int = 1,
    var secureId: String = ""
)

data class CartItemDetail(
    var cartId: Int = 0,
    val id: Int = 0,
    val note: String = "",
    val productId: Int = 0,
    val productOptions: List<OptionGroup>? = null,
    var quantity: Int = 0
)

data class RedeemCart(
    var discount: Double = 0.0,
    var merchantId: String = "",
    var couponType: CouponType = CouponType.UNKNOWN,
    var code: String? = null
)

private fun convertToCartOptionItem(
    optionGroup: Int?,
    optionsList: List<Int?>
): CartItemOptionInput {
    return CartItemOptionInput.builder().option_group(optionGroup)
        .options(optionsList).build()
}

fun convertToCartOptionGroup(group: List<OptionGroup>?): List<CartItemOptionInput> {
    val list = mutableListOf<CartItemOptionInput>()
    group?.forEach {
        val optionIds = mutableListOf<Int?>()
        it.options.forEach {
            optionIds.add(it.id)
        }

        list.add(convertToCartOptionItem(it.id, optionIds.toList()))
    }
    return list
}