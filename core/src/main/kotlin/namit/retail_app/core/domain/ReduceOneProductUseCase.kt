package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.CartItemDetail
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.data.repository.CartRepository
import namit.retail_app.core.utils.UseCaseResult

interface ReduceOneProductUseCase {
    suspend fun execute(product: ProductItem, secureId: String): UseCaseResult<Boolean>
}

class ReduceOneProductUseCaseImpl(private val cartRepository: CartRepository) :
    ReduceOneProductUseCase {

    companion object {
        val TAG: String = ReduceOneProductUseCaseImpl::class.java.simpleName
        const val CAN_NOT_REDUCE_PRODUCT_IN_CART = "CAN_NOT_REDUCE_PRODUCT_IN_CART"
        const val CAN_NOT_DELETE_PRODUCT_IN_CART = "CAN_NOT_DELETE_PRODUCT_IN_CART"
        const val PRODUCT_QUANTITY_IS_ZERO = "PRODUCT_QUANTITY_IS_ZERO"
    }

    override suspend fun execute(product: ProductItem, secureId: String): UseCaseResult<Boolean> {

        var cartItem = CartItemDetail()
        val cartList = cartRepository.getCartDetail(secureId)
        for (item in cartList) {
            // A product which added from List product always has size is empty and vice versa
            if (product.id.equals(item.productId) && product.optionGroupSelected.isNullOrEmpty()
                && item.productOptions.isNullOrEmpty()) {
                cartItem = item
                cartItem.quantity -= 1
                break
            }
        }

        if(cartItem.id < 0) return UseCaseResult.Error(Throwable(CAN_NOT_REDUCE_PRODUCT_IN_CART))

        if (cartItem.quantity == 0) {

            return try {
                val result = cartRepository.deleteItemInCart(cartItem.id, secureId)
                if (result) {
                    UseCaseResult.Success(result)
                } else {
                    UseCaseResult.Error(Throwable(CAN_NOT_DELETE_PRODUCT_IN_CART))
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                UseCaseResult.Error(e)
            }
        }

        return try {
            val result = cartRepository.reduceOne(product, secureId, cartItem.quantity, cartItem.id)
            if (result) {
                UseCaseResult.Success(result)
            } else {
                UseCaseResult.Error(Throwable(CAN_NOT_REDUCE_PRODUCT_IN_CART))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }

}