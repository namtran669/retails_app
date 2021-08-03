package namit.retail_app.order.utils

import namit.retail_app.core.data.entity.OrderPayment
import namit.retail_app.payment.data.PaymentMethodModel
import namit.retail_app.payment.enums.CardType
import namit.retail_app.core.enums.PaymentType

object OrderUtils {
    fun convertToPaymentMethodModel(payment: OrderPayment): PaymentMethodModel {
        if (payment.paymentMethod?.contains(
                PaymentType.CASH.value,
                ignoreCase = true
            ) == true
        ) {
            return PaymentMethodModel(
                id = -1,
                title = payment.paymentMethod ?: "",
                type = PaymentType.CASH
            )
        } else {
            val cardType = when (payment.brand) {
                CardType.VISA.value -> CardType.VISA
                CardType.MASTER_CARD.value -> CardType.MASTER_CARD
                CardType.JCB.value -> CardType.JCB
                else -> CardType.UNKNOWNS
            }
            return PaymentMethodModel(
                id = -1,
                title = payment.brand ?: "",
                type = PaymentType.CREDIT_CARD,
                cardType = cardType
            )
        }
    }
}