package namit.retail_app.core.enums

enum class OrderStatus(val value: String) {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    IN_PROGRESS("IN_PROGRESS"),
    READY_TO_SHIP("READY_TO_SHIP"),
    SHIPPING("SHIPPING"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    UNKNOWN("UNKNOWN")
}