package namit.retail_app.order.domain

import android.util.Log
import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.order.data.repository.OrderRepository

interface CancelOrderUseCase {
    suspend fun execute(secureKey: String): UseCaseResult<Boolean>
}

class CancelOrderUseCaseImpl(private val orderRepository: OrderRepository) :
    CancelOrderUseCase {

    companion object {
        val TAG: String = CreateOrderUseCaseImpl::class.java.simpleName
        const val ERROR_CAN_NOT_CANCEL_ORDER = "ERROR_CAN_NOT_CANCEL_ORDER"
    }

    override suspend fun execute(secureKey: String): UseCaseResult<Boolean> {
        return try {
            val repoResult = orderRepository.cancelOrder(secureKey = secureKey)
            if (repoResult is RepositoryResult.Success) {
                if (repoResult.data == true) {
                    UseCaseResult.Success(repoResult.data)
                } else {
                    UseCaseResult.Error(Throwable(ERROR_CAN_NOT_CANCEL_ORDER))
                }
            } else {
                UseCaseResult.Error(Throwable((repoResult as RepositoryResult.Error).message))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }
}