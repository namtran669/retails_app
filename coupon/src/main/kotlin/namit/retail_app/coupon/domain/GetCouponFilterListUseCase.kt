package namit.retail_app.coupon.domain

import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.coupon.data.entity.CouponFilterModel
import namit.retail_app.coupon.data.repository.CouponRepository
import namit.retail_app.coupon.enums.CouponFilterType

interface GetCouponFilterListUseCase {
    suspend fun execute(): UseCaseResult<List<CouponFilterModel>>
}

class GetCouponFilterListUseCaseImpl(
    private val couponRepository: CouponRepository
) : GetCouponFilterListUseCase {

    companion object {
        const val ERROR_EMPTY_COUPON = "ERROR_EMPTY_COUPON"
    }

    override suspend fun execute(): UseCaseResult<List<CouponFilterModel>> {
        return try {
            val foodStoryList = couponRepository.loadVerticalList()
            val result = mutableListOf<CouponFilterModel>()
            if (foodStoryList.isNotEmpty()) {
                result.add(
                    CouponFilterModel(
                        id = -1,
                        filterType = CouponFilterType.ALL,
                        isSelected = true,
                        name = ""
                    )
                )
                //TODO Implement next phase
//                result.add(S
//                    CouponFilterModel(
//                        id = -2,
//                        filterType = CouponFilterType.FLASH
//                    )
//                )
                result.addAll(foodStoryList)
                UseCaseResult.Success(result)
            } else {
                UseCaseResult.Error(Throwable(ERROR_EMPTY_COUPON))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UseCaseResult.Error(Throwable(e.message))
        }
    }
}