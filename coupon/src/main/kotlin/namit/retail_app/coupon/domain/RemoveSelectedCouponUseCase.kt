package namit.retail_app.coupon.domain

import android.util.Log
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.coupon.data.repository.CouponRepository

interface RemoveSelectedCouponUseCase {
    fun execute(): UseCaseResult<Unit>
}

class RemoveSelectedCouponUseCaseImpl(private val couponRepository: CouponRepository): RemoveSelectedCouponUseCase {

    companion object {
        private const val TAG = "RemoveSelectedCoupon"
    }

    override fun execute(): UseCaseResult<Unit> {
        return try {
            UseCaseResult.Success(couponRepository.removeSelectedCoupon())
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(Throwable(e.message))
        }
    }
}