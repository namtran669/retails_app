package namit.retail_app.payment.data.entity

import namit.retail_app.payment.enums.PaymentCardType

data class PaymentCard(var id: String,
                       var title: String,
                       var type: PaymentCardType
)