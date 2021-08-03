package namit.retail_app.core.enums

enum class PaymentType(val value: String) {
    CASH("CASH_ON_DELIVERY"),
    CREDIT_CARD("CREDIT_CARD"),
    TRUE_MONEY("TRUE_MONEY_WALLET"),
    TRUE_POINT("TRUE_POINT")
}