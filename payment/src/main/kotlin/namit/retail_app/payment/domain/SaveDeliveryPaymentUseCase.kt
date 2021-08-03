package namit.retail_app.payment.domain

import android.util.Log
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.payment.data.PaymentMethodModel
import namit.retail_app.payment.data.repository.DeliveryPaymentManager

interface SaveDeliveryPaymentUseCase {
    fun execute(paymentData: PaymentMethodModel): UseCaseResult<Boolean>
}

class SaveDeliveryPaymentUseCaseImpl(private val deliveryPaymentManager: DeliveryPaymentManager) :
    SaveDeliveryPaymentUseCase {

    companion object {
        private val TAG = SaveDeliveryPaymentUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_SAVE_DELIVERY_PAYMENT =
            "ERROR_CANNOT_SAVE_DELIVERY_PAYMENT"
    }

    override fun execute(paymentData: PaymentMethodModel): UseCaseResult<Boolean> {
        return try {
            deliveryPaymentManager.saveDeliveryPayment(paymentData)
            val saveResult = deliveryPaymentManager.haveDeliveryPayment()
            if (saveResult) {
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_SAVE_DELIVERY_PAYMENT))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}