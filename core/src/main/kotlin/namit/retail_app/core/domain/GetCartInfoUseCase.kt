package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.CartModel
import namit.retail_app.core.data.repository.CartRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetCartInfoUseCase {
    suspend fun execute(secureId: String, page: Int): UseCaseResult<CartModel>
}

class GetCartInfoUseCaseImpl(private val cartRepository: CartRepository) : GetCartInfoUseCase {

    companion object {
        val TAG: String = GetCartInfoUseCaseImpl::class.java.simpleName
        const val ERROR_CART_IS_EMPTY = "ERROR_CART_IS_EMPTY"
    }

    override suspend fun execute(secureId: String, page: Int): UseCaseResult<CartModel> {
        return try {
            val cartInfo = cartRepository.loadCartList(secureId = secureId, page = page)
            if (cartInfo.merchants.isNotEmpty()) {
                UseCaseResult.Success(cartInfo)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CART_IS_EMPTY))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }

}