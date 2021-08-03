package namit.retail_app.coupon.domain

import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.coupon.data.repository.CouponRepository

interface GetCouponListByMerchantUseCase {
    suspend fun execute(page: Int, merchantIds: List<String>): UseCaseResult<List<CouponModel>>
}

class GetCouponListByMerchantUseCaseImpl(
    private val couponRepository: CouponRepository
): GetCouponListByMerchantUseCase {

    companion object {
        const val ERROR_EMPTY_COUPON = "ERROR_EMPTY_COUPON"
    }

    override suspend fun execute(page: Int, merchantIds: List<String>): UseCaseResult<List<CouponModel>> {
        return try {
            val foodStoryList = couponRepository.loadCouponByMerchant(page, merchantIds)
            if (foodStoryList.isNotEmpty()) {
                UseCaseResult.Success(foodStoryList)
            } else {
                UseCaseResult.Error(Throwable(ERROR_EMPTY_COUPON))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UseCaseResult.Error(Throwable(e.message))
        }
    }
}