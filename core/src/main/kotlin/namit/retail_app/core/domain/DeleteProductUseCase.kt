package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.repository.CartRepository
import namit.retail_app.core.utils.UseCaseResult

interface DeleteProductUseCase {
    suspend fun execute(cartItemId: Int, secureId: String): UseCaseResult<Boolean>
}

class DeleteProductUseCaseImpl(private val cartRepository: CartRepository) : DeleteProductUseCase {

    companion object {
        val TAG: String = DeleteProductUseCaseImpl::class.java.simpleName
        const val CAN_NOT_DELETE_PRODUCT_IN_CART = "CAN_NOT_DELETE_PRODUCT_IN_CART"
        const val CAN_NOT_FIND_PRODUCT_IN_CART = "CAN_NOT_FIND_PRODUCT_IN_CART"
    }

    override suspend fun execute(cartItemId: Int, secureId: String): UseCaseResult<Boolean> {

//        var itemId = 0
//        val cartList = cartRepository.getCartDetail(secureId)
//        for (item in cartList) {
//            if (product.id.equals(item.productId)
//                && product.optionGroupSelected.isNullOrEmpty() && item.productOptions.isNullOrEmpty()
//            ) {
//                itemId = item.id
//                break
//            }
//        }
//
//        if (itemId == 0) return UseCaseResult.Error(Throwable(CAN_NOT_FIND_PRODUCT_IN_CART))

        return try {
            val result = cartRepository.deleteItemInCart(cartItemId, secureId)
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

}