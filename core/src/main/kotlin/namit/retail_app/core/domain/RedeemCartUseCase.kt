package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.RedeemCart
import namit.retail_app.core.data.repository.CartRepository
import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.core.utils.UseCaseResult

interface RedeemCartUseCase {
    suspend fun execute(cartId: Int, couponCode: String): UseCaseResult<RedeemCart>
}

class RedeemCartUseCaseImpl(private val cartRepository: CartRepository) : RedeemCartUseCase {

    companion object {
        private const val TAG = "RedeemCartUseCase"
        const val ERROR_REDEEM_COUPON = "ERROR_REDEEM_COUPON"
        //TODO WORK AROUND HERE NEED TO IMPROVE
        const val ERROR_CAMPAIGN_QUOTA_EXCEED = "Campaign User Daily Quota Exceeded"
    }

    override suspend fun execute(cartId: Int, couponCode: String): UseCaseResult<RedeemCart> {
        return try {
            val redeemResult = cartRepository.redeemCart(cartId, couponCode)
            if (redeemResult is RepositoryResult.Success) {
                UseCaseResult.Success(redeemResult.data)
            } else {
                UseCaseResult.Error(Throwable((redeemResult as RepositoryResult.Error).message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(Throwable(ERROR_REDEEM_COUPON))
        }
    }
}