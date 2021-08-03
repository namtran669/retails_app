package namit.retail_app.core.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantCategory(
    var id: Int? = null,
    var title: TextMultiLanguage? = null,
    var imageUrl: List<String>? = null
) : Parcelable

@Parcelize
data class CategoryItem(
    var id: Int = -1,
    var merchantId: String = "",
    var merchantName: String = "",
    var parentId: Int? = null,
    var iconUrl: String? = null,
    var nameEn: String = "",
    var nameTh: String = "",
    var descriptionEn: String? = null,
    var descriptionTh: String? = null,
    var productCount: Int = 0,
    var breadcrumbChildList: MutableList<CategoryItem>? = null,
    var productList: List<ProductItem>? = null,
    var isSelected: Boolean = false,
    var name: String = ""
) : Parcelable