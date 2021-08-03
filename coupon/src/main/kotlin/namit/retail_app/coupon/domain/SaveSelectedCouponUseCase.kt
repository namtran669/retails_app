package namit.retail_app.coupon.domain

import android.util.Log
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.coupon.data.repository.CouponRepository

interface SaveSelectedCouponUseCase {
    fun execute(couponModel: CouponModel): UseCaseResult<Unit>
}

class SaveSelectedCouponUseCaseImpl(private val couponRepository: CouponRepository) :
    SaveSelectedCouponUseCase {

    companion object {
        private const val TAG = "GetSelectedCoupon"
    }

    override fun execute(couponModel: CouponModel): UseCaseResult<Unit> {
        return try {
            UseCaseResult.Success(couponRepository.saveSelectedCoupon(couponModel))
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(Throwable(e.message))
        }
    }
}