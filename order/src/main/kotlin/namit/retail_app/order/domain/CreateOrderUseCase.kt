package namit.retail_app.order.domain

import android.util.Log
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.data.entity.UserModel
import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.order.data.repository.OrderRepository

interface CreateOrderUseCase {
    suspend fun execute(
        merchantCartId: Int,
        timeSLot: String?,
        address: AddressModel,
        paymentMethodId: Int,
        userInfo: UserModel,
        userPaymentMethodId: Int? = null,
        campaignCode: String? = null
    ): UseCaseResult<OrderModel>
}

class CreateOrderUseCaseImpl(private val orderRepository: OrderRepository) :
    CreateOrderUseCase {

    companion object {
        val TAG: String = CreateOrderUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_CREATE_ORDER = "ERROR_CANNOT_CREATE_ORDER"
        const val ERROR_CANNOT_CREATE_ORDER_WITH_TIME_SLOT = "pickupAt.validateOrderLimitAndFailSelectedTime"
    }

    override suspend fun execute(
        merchantCartId: Int, timeSLot: String?, address: AddressModel,
        paymentMethodId: Int, userInfo: UserModel, userPaymentMethodId: Int?,
        campaignCode: String?
    ): UseCaseResult<OrderModel> {

        return try {
            val repoResult =
                orderRepository.createOrder(
                    merchantCartId = merchantCartId,
                    timeSLot = timeSLot,
                    address = address,
                    paymentMethodId = paymentMethodId,
                    userPaymentMethodId = userPaymentMethodId,
                    userInfo = userInfo,
                    campaignCode = campaignCode
                )
            if (repoResult is RepositoryResult.Success) {
                if (repoResult.data != null) {
                    UseCaseResult.Success(repoResult.data)
                } else {
                    UseCaseResult.Error(Throwable(ERROR_CANNOT_CREATE_ORDER))
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