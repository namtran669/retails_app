package namit.retail_app.core.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductItem(
    var id: Int = SKELETON_PRODUCT_ID,
    var quantityOrder: Int = 0,
    var quantityInStock: Int = 0,
    var merchantId: String = "",
    var merchant: MerchantInfoItem? = null,
    var categoryIds: List<Int> = listOf(),
    var nameEn: String = "",
    var nameTh: String = "",
    var retailPriceWithTax: Double? = null,
    var retailPriceWithoutTax: Double? = null,
    var weightUnit: String? = null,
    var country: String? = null,
    var distributor: String? = null,
    var updatedAt: String? = null,
    var weight: Float? = null,
    var thumbnailUrl: String? = null,
    var optionGroup: List<OptionGroup> = listOf(),
    var optionGroupSelected: List<OptionGroup>? = null,
    var descriptionEn: String? = null,
    var descriptionTh: String? = null,
    var packageItem: Int? = null,
    var packageUnit: String? = null,
    var sku: String? = null,
    var shelfLife: Int? = null,
    var shelfLifeUnit: String? = null,
    var images: List<String> = listOf(),
    var isAlcohol: Boolean = false,
    var discountPercentage: Float? = null,
    var isDiscountPrice: Boolean = false,
    var discountPriceStartAt: String? = null,
    var discountPriceEndAt: String? = null,
    var discountPriceWithTax: Double? = null,
    var showDiscount: Boolean = false,
    var name: String = "",
    var description: String = "",
    var note: String = ""

) : Parcelable {
    companion object {
        const val SKELETON_PRODUCT_ID = -1
    }
}

@Parcelize
data class OptionPick(
    var id: Int = -1,
    var nameEn: String = "",
    var nameTh: String = "",
    var price: Double? = null,
    var currency: String? = null,
    var description: String? = null,
    var isSelected: Boolean = false,
    var name: String = ""
) : Parcelable

@Parcelize
data class OptionGroup(
    var id: Int = -1,
    var nameEn: String = "",
    var nameTh: String = "",
    var selection: OptionSelection? = null,
    var type: OptionType = OptionType.OPTIONAL,
    var maxLimit: Int? = null,
    var minLimit: Int? = null,
    var options: List<OptionPick> = listOf(),
    var name: String = ""
) : Parcelable


enum class OptionType(val value: String) {
    REQUIRED("REQUIRED"), OPTIONAL("OPTIONAL")
}

enum class OptionSelection(val value: String) {
    SINGLE("SINGLE"), MULTIPLE("MULTIPLE")
}
