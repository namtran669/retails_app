package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.repository.MerchantRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetMerchantByStoreIdUseCase {
    suspend fun execute(storeId: String): UseCaseResult<MerchantInfoItem>
}

class GetMerchantByStoreIdUseCaseImpl(
    private val merchantRepository: MerchantRepository
): GetMerchantByStoreIdUseCase {

    companion object {
        val TAG:String = GetMerchantByStoreIdUseCaseImpl::class.java.simpleName
        const val ERROR_EMPTY_MERCHANT_CASE = "ERROR_EMPTY_MERCHANT_CASE"
    }

    override suspend fun execute(storeId: String): UseCaseResult<MerchantInfoItem> {
        return try {
            val merchantResult = merchantRepository.getMerchantByStoreId(storeId)
            if (merchantResult.id.isNotBlank()) {
                UseCaseResult.Success(merchantResult)
            } else {
                UseCaseResult.Error(Throwable(ERROR_EMPTY_MERCHANT_CASE))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }

}