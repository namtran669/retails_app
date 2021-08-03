package namit.retail_app.payment.domain

import android.util.Log
import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.payment.data.repository.PaymentRepository

interface AddNewCreditCardUseCase {
    suspend fun execute(cardToken:String, isDefault:Boolean): UseCaseResult<Boolean>
}

class AddNewCreditCardUseCaseImpl(private val paymentRepository: PaymentRepository) :
    AddNewCreditCardUseCase {

    companion object {
        val TAG: String = AddNewCreditCardUseCaseImpl::class.java.simpleName
        const val ERROR_CANNOT_ADD_NEW_CREDIT_CARD = "ERROR_CANNOT_ADD_NEW_CREDIT_CARD"
    }

    override suspend fun execute(
        cardToken:String, isDefault:Boolean
    ): UseCaseResult<Boolean> {
        return try {
            val paymentResult = paymentRepository.addNewCreditCard(cardToken = cardToken, isDefault = isDefault)
            if (paymentResult is RepositoryResult.Success) {
                if (paymentResult.data == true) {
                    UseCaseResult.Success(paymentResult.data)
                } else {
                    UseCaseResult.Error(Throwable(ERROR_CANNOT_ADD_NEW_CREDIT_CARD))
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