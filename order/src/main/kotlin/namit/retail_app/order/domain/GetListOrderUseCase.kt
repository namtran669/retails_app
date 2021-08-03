package namit.retail_app.order.domain

import android.util.Log
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.order.data.repository.OrderRepository

interface GetListOrderUseCase {
    suspend fun execute(page: Int, type: String): UseCaseResult<List<OrderModel>>
}

class GetListOrderUseCaseImpl(private val orderRepository: OrderRepository) : GetListOrderUseCase {

    companion object {
        val TAG: String = CreateOrderUseCaseImpl::class.java.simpleName
        const val ERROR_EMPTY_ORDER_LIST = "ERROR_EMPTY_ORDER_LIST"
        const val SIZE_ITEM_PER_REQUEST = 10
    }

    override suspend fun execute(
        page: Int, type: String
    ): UseCaseResult<List<OrderModel>> {
        return try {
            val repoResult = orderRepository.getListOrder(
                page = page,
                limit = SIZE_ITEM_PER_REQUEST,
                type = type
            )
            if (repoResult is RepositoryResult.Success) {
                if (repoResult.data.isNullOrEmpty().not()) {
                    UseCaseResult.Success(repoResult.data)
                } else {
                    UseCaseResult.Error(Throwable(ERROR_EMPTY_ORDER_LIST))
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