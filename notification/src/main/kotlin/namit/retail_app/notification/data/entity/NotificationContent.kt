package namit.retail_app.notification.data.entity

//Todo test UI Model
enum class NotificationState(val value: Int){
    NORMAL(0),
    ORDER(1)
}

data class NotificationContent(
    val title: String,
    val type: NotificationState,
    val content: String? = null,
    val createdAt: String,
    val orderStatus: String? = null,
    val isUnSeen:Boolean = false
)