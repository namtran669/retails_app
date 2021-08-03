package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.repository.CartRepository
import namit.retail_app.core.utils.UseCaseResult

interface ClaimCartUseCase {
    suspend fun execute(secureId: String) : UseCaseResult<Boolean>
}

class ClaimCartUseCaseImpl(private val cartRepository: CartRepository): ClaimCartUseCase {

    companion object {
        val TAG: String = ClaimCartUseCaseImpl::class.java.simpleName
        const val CAN_NOT_CLAIM_CART_FOR_USER = "CAN_NOT_CLAIM_CART_FOR_USER"
    }
    override suspend fun execute(secureId: String): UseCaseResult<Boolean> {
        return try {
            val result = cartRepository.claimCart(secureId)
            if (result) {
                UseCaseResult.Success(result)
            } else {
                UseCaseResult.Error(Throwable(CAN_NOT_CLAIM_CART_FOR_USER))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }

}