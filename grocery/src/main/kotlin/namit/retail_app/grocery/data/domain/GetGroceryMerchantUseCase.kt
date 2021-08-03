package namit.retail_app.grocery.data.domain

import android.util.Log
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.grocery.data.repository.GroceryMerchantRepository

interface GetGroceryMerchantUseCase {
    suspend fun execute(lat: Double, lng: Double): UseCaseResult<List<MerchantInfoItem>>
}

class GetGroceryMerchantUseCaseImpl(
    private val merchantRepository: GroceryMerchantRepository
): GetGroceryMerchantUseCase {

    companion object {
        val TAG = GetGroceryMerchantUseCaseImpl::class.java.simpleName
       private const val ERROR_EMPTY_MERCHANT_CASE = "ERROR_EMPTY_MERCHANT_CASE"
    }

    override suspend fun execute(lat: Double, lng: Double): UseCaseResult<List<MerchantInfoItem>> {
        return try {
            val merchantList = merchantRepository.getMerchantList(lat, lng)
            if (merchantList.isNotEmpty()) {
                UseCaseResult.Success(merchantList)
            } else {
                UseCaseResult.Error(Throwable(ERROR_EMPTY_MERCHANT_CASE))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }

}
