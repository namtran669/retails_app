package namit.retail_app.payment.data.repository

import namit.retail_app.payment.data.PaymentMethodModel

interface DeliveryPaymentManager {
    fun saveDeliveryPayment(payment: PaymentMethodModel)
    fun getDeliveryPayment(): PaymentMethodModel?
    fun removeDeliveryPayment()
    fun haveDeliveryPayment(): Boolean
}

class DeliveryPaymentManagerImpl : DeliveryPaymentManager {
    private var deliveryPayment: PaymentMethodModel? = null

    override fun saveDeliveryPayment(payment: PaymentMethodModel) {
        this.deliveryPayment = payment
    }

    override fun getDeliveryPayment(): PaymentMethodModel? {
        return deliveryPayment
    }

    override fun removeDeliveryPayment() {
        deliveryPayment = null
    }

    override fun haveDeliveryPayment(): Boolean {
        return deliveryPayment != null
    }

}