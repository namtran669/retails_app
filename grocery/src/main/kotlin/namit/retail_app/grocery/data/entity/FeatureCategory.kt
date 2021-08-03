package namit.retail_app.grocery.data.entity

//todo model for UI only, currently the sys miss data for this (confirmed from PO)
data class FeatureCategory(
    val title: String? = null,
    val count: Int = 0,
    var isSelected:Boolean = false
)