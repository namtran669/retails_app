package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.TimeSlot
import namit.retail_app.core.data.repository.DeliveryTimeManager
import namit.retail_app.core.utils.UseCaseResult

interface SaveDeliveryTimeUseCase {
    fun execute(merchantId: String, timeData: TimeSlot): UseCaseResult<Boolean>
}

class SaveDeliveryTimeUseCaseImpl(private val deliveryTimeManager: DeliveryTimeManager) :
    SaveDeliveryTimeUseCase {

    companion object {
        private val TAG = SaveDeliveryTimeUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_SAVE_DELIVERY_TIME =
            "ERROR_CANNOT_SAVE_DELIVERY_TIME"
    }

    override fun execute(merchantId: String, timeData: TimeSlot): UseCaseResult<Boolean> {
        return try {
            deliveryTimeManager.saveDeliveryTime(merchantId, timeData)
            val saveResult = deliveryTimeManager.haveDeliveryTime(merchantId)
            if (saveResult) {
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_SAVE_DELIVERY_TIME))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}