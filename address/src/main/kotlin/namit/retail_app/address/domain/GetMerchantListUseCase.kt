package namit.retail_app.address.domain

import android.util.Log
import namit.retail_app.address.data.repository.MerchantListRepository
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.enums.MerchantType
import namit.retail_app.core.utils.UseCaseResult
import java.util.*

interface GetMerchantListUseCase {
    suspend fun execute(
        lat: Double,
        lng: Double,
        type: MerchantType
    ): UseCaseResult<List<MerchantInfoItem>>
}

class GetMerchantListUseCaseImpl(
    private val merchantRepository: MerchantListRepository
) : GetMerchantListUseCase {

    companion object {
        val TAG = GetMerchantListUseCaseImpl::class.java.simpleName
        private const val ERROR_EMPTY_MERCHANT_CASE = "ERROR_EMPTY_MERCHANT_CASE"
    }

    override suspend fun execute(
        lat: Double,
        lng: Double,
        type: MerchantType
    ): UseCaseResult<List<MerchantInfoItem>> {
        return try {
            val merchantList = merchantRepository.getMerchantList(
                lat, lng,
                type.value.toLowerCase(Locale.ENGLISH)
            )
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
