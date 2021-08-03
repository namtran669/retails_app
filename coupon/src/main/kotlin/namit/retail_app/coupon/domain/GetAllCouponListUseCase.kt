package namit.retail_app.coupon.domain

import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.coupon.data.repository.CouponRepository

interface GetAllCouponListUseCase {
    suspend fun execute(page: Int): UseCaseResult<List<CouponModel>>
}

class GetAllCouponListUseCaseImpl(
    private val couponRepository: CouponRepository
): GetAllCouponListUseCase {

    companion object {
        const val ERROR_EMPTY_COUPON = "ERROR_EMPTY_COUPON"
    }

    override suspend fun execute(page: Int): UseCaseResult<List<CouponModel>> {
        return try {
            val foodStoryList = couponRepository.loadAllCouponList(page = page)
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