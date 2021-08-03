package namit.retail_app.order.domain

import android.util.Log
import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.order.data.repository.OrderRepository

interface GetOrderStatusUseCase {
    suspend fun execute(secureKey: String): UseCaseResult<String>
}

class GetOrderStatusUseCaseImpl(private val orderRepository: OrderRepository) :
    GetOrderStatusUseCase {

    companion object {
        val TAG: String = CreateOrderUseCaseImpl::class.java.simpleName
        const val ERROR_CAN_NOT_GET_ORDER_STATUS = "ERROR_CAN_NOT_GET_ORDER_STATUS"
    }

    override suspend fun execute(secureKey: String): UseCaseResult<String> {
        return try {
            val repoResult = orderRepository.getOrderStatus(secureKey = secureKey)
            if (repoResult is RepositoryResult.Success) {
                if (repoResult.data.isNullOrEmpty().not()) {
                    UseCaseResult.Success(repoResult.data)
                } else {
                    UseCaseResult.Error(Throwable(ERROR_CAN_NOT_GET_ORDER_STATUS))
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