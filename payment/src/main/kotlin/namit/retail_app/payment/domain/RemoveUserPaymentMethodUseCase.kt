package namit.retail_app.payment.domain

import android.util.Log
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.payment.data.repository.PaymentRepository

interface RemoveUserPaymentMethodUseCase {
    suspend fun execute(userPaymentMethodId:Int): UseCaseResult<Boolean>
}

class RemoveUserPaymentMethodUseCaseImpl(private val paymentRepository: PaymentRepository) :
    RemoveUserPaymentMethodUseCase {

    companion object {
        val TAG: String = AddNewCreditCardUseCaseImpl::class.java.simpleName
        const val ERROR_CANNOT_REMOVE_USER_PAYMENT_METHOD = "ERROR_CANNOT_REMOVE_USER_PAYMENT_METHOD"
    }

    override suspend fun execute(userPaymentMethodId: Int): UseCaseResult<Boolean> {
        return try {
            val removeResult = paymentRepository.removeUserPaymentMethod(userPaymentMethodId)
                if (removeResult) {
                    UseCaseResult.Success(true)
                } else {
                    UseCaseResult.Error(Throwable(ERROR_CANNOT_REMOVE_USER_PAYMENT_METHOD))
                }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }
}