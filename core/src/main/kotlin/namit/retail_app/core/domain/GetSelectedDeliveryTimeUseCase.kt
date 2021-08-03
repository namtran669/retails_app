package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.TimeSlot
import namit.retail_app.core.data.repository.DeliveryTimeManager
import namit.retail_app.core.utils.UseCaseResult

interface GetSelectedDeliveryTimeUseCase {
    fun execute(merchantId: String): UseCaseResult<TimeSlot>
}

class GetSelectedDeliveryTimeUseCaseImpl(private val deliveryTimeManager: DeliveryTimeManager) :
    GetSelectedDeliveryTimeUseCase {

    companion object {
        private val TAG = GetSelectedDeliveryTimeUseCase::class.java.simpleName
        private const val ERROR_CANNOT_GET_DELIVERY_TIME =
            "ERROR_CANNOT_GET_DELIVERY_TIME"
    }

    override fun execute(merchantId: String): UseCaseResult<TimeSlot> {
        return try {
            val result = deliveryTimeManager.getDeliveryTime(merchantId)
            if (result != null) {
                UseCaseResult.Success(result)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_GET_DELIVERY_TIME))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }
}