package namit.retail_app.core.data.entity

data class DeliveryDay(var id: String,
                       var title: String,
                       var date: String,
                       var isSelected: Boolean = false)