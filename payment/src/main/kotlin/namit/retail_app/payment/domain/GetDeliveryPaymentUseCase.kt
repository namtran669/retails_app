package namit.retail_app.payment.domain

import android.util.Log
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.payment.data.PaymentMethodModel
import namit.retail_app.payment.data.repository.DeliveryPaymentManager

interface GetDeliveryPaymentUseCase {
    fun execute(): UseCaseResult<PaymentMethodModel>
}

class GetDeliveryPaymentUseCaseImpl(private val deliveryPaymentManager: DeliveryPaymentManager) :
    GetDeliveryPaymentUseCase {

    companion object {
        private val TAG = GetDeliveryPaymentUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_GET_DELIVERY_PAYMENT =
            "ERROR_CANNOT_GET_DELIVERY_PAYMENT"
    }

    override fun execute(): UseCaseResult<PaymentMethodModel> {
        return try {
            val result = deliveryPaymentManager.getDeliveryPayment()
            if (result != null) {
                UseCaseResult.Success(result)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_GET_DELIVERY_PAYMENT))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}