package namit.retail_app.coupon.domain

import android.util.Log
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.coupon.data.repository.CouponRepository

interface GetSelectedCouponUseCase {
    fun execute(): UseCaseResult<CouponModel>
}

class GetSelectedCouponUseCaseImpl(private val couponRepository: CouponRepository) :
    GetSelectedCouponUseCase {

    companion object {
        private const val TAG = "GetSelectedCoupon"
        private const val ERROR_CANNOT_GET_SELECTED_COUPON = "ERROR_CANNOT_GET_SELECTED_COUPON"
    }

    override fun execute(): UseCaseResult<CouponModel> {
        return try {
            if (couponRepository.hasSelectedCoupon()) {
                UseCaseResult.Success(couponRepository.getSelectedCoupon())
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_GET_SELECTED_COUPON))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(Throwable(e.message))
        }
    }

}