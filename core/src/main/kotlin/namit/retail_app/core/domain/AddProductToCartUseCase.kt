package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.data.repository.CartRepository
import namit.retail_app.core.utils.UseCaseResult

interface AddProductToCartUseCase {
    suspend fun execute(product: ProductItem, secureId: String, quantity: Int) : UseCaseResult<Boolean>
}

class AddProductToCartUseCaseImpl(private val cartRepository: CartRepository): AddProductToCartUseCase {

    companion object {
        val TAG: String = AddProductToCartUseCaseImpl::class.java.simpleName
        const val CAN_NOT_ADD_PRODUCT_TO_CART = "CAN_NOT_ADD_PRODUCT_TO_CART"
    }
    override suspend fun execute(product: ProductItem, secureId: String, quantity: Int): UseCaseResult<Boolean> {

        return try {
            val result = cartRepository.addItemToCart(product, secureId, quantity)
            if (result) {
                UseCaseResult.Success(result)
            } else {
                UseCaseResult.Error(Throwable(CAN_NOT_ADD_PRODUCT_TO_CART))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }

}