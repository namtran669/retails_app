package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.CartModel
import namit.retail_app.core.data.repository.CartRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetCartInfoLocalUseCase {
    suspend fun execute(): UseCaseResult<CartModel>
}

class GetCartInfoLocalUseCaseImpl(private val cartRepository: CartRepository) : GetCartInfoLocalUseCase {

    companion object {
        val TAG: String = GetCartInfoLocalUseCaseImpl::class.java.simpleName
        const val ERROR_CART_LOCAL_IS_EMPTY = "ERROR_CART_LOCAL_IS_EMPTY"
    }

    override suspend fun execute(): UseCaseResult<CartModel> {
        return try {
            val cartInfo = cartRepository.getCurrentCartInfo()
            if (cartInfo != null && cartInfo.merchants.isNotEmpty()) {
                UseCaseResult.Success(cartInfo)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CART_LOCAL_IS_EMPTY))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }

}