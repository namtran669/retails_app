package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.TimeSlot
import namit.retail_app.core.data.repository.DeliveryTimeManager
import namit.retail_app.core.utils.UseCaseResult

interface DeleteDeliveryTimeUseCase {
    fun execute(merchantId: String, timeData: TimeSlot): UseCaseResult<Boolean>
}

class DeleteDeliveryTimeUseCaseImpl(private val deliveryTimeManager: DeliveryTimeManager) :
    DeleteDeliveryTimeUseCase {

    companion object {
        private val TAG = DeleteDeliveryTimeUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_DELETE_DELIVERY_TIME =
            "ERROR_CANNOT_DELETE_DELIVERY_TIME"
    }

    override fun execute(merchantId: String, timeData: TimeSlot): UseCaseResult<Boolean> {
        return try {
            deliveryTimeManager.removeDeliveryTime(merchantId)
            val result = deliveryTimeManager.getDeliveryTime(merchantId)
            if (result == null) {
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_DELETE_DELIVERY_TIME))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}