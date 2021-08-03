package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.repository.CartRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetDeliveryFeeUseCase {
    suspend fun execute(cartId: Int): UseCaseResult<Double>
}

class GetDeliveryFeeUseCaseImpl(private val cartRepository: CartRepository) :
    GetDeliveryFeeUseCase {

    companion object {
        val TAG: String = GetDeliveryFeeUseCase::class.java.simpleName
    }

    override suspend fun execute(cartId: Int): UseCaseResult<Double> {
        return try {
            val result = cartRepository.getDeliveryFee(cartId)
            UseCaseResult.Success(result)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }

}