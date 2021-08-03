package namit.retail_app.payment.domain

import android.util.Log
import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.payment.data.PaymentMethodModel
import namit.retail_app.payment.data.repository.PaymentRepository

interface GetUserPaymentListUseCase {
    suspend fun execute(page: Int): UseCaseResult<List<PaymentMethodModel>>
}

class GetUserPaymentListUseCaseImpl(private val paymentRepository: PaymentRepository) :
    GetUserPaymentListUseCase {

    companion object {
        val TAG: String = GetUserPaymentListUseCaseImpl::class.java.simpleName
        const val ERROR_EMPTY_USER_PAYMENT_LIST = "ERROR_EMPTY_USER_PAYMENT_LIST"
    }

    override suspend fun execute( page: Int
    ): UseCaseResult<List<PaymentMethodModel>> {
        return try {
            val paymentResult = paymentRepository.getUserPaymentList(page = page)
            if (paymentResult is RepositoryResult.Success) {
                if (paymentResult.data.isNullOrEmpty().not()) {
                    UseCaseResult.Success(paymentResult.data)
                } else {
                    UseCaseResult.Error(Throwable(ERROR_EMPTY_USER_PAYMENT_LIST))
                }
            } else {
                UseCaseResult.Error(Throwable((paymentResult as RepositoryResult.Error).message))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }
}