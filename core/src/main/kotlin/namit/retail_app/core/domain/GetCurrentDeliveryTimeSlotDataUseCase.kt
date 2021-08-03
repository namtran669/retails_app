package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.TimeSlotWrapper
import namit.retail_app.core.data.repository.TimeSlotRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetCurrentDeliveryTimeSlotDataUseCase {
    suspend fun execute(merchantId: String): UseCaseResult<TimeSlotWrapper>
}

class GetCurrentDeliveryTimeSlotDataUseCaseImpl(
    private val timeSlotRepository: TimeSlotRepository
) : GetCurrentDeliveryTimeSlotDataUseCase {

    companion object {
        private val TAG = GetCurrentDeliveryTimeSlotDataUseCaseImpl::class.java.simpleName
        private const val ERROR_CURRENT_TIMESLOT_EMPTY_CASE = "ERROR_CURRENT_TIMESLOT_EMPTY_CASE"
    }

    override suspend fun execute(merchantId: String): UseCaseResult<TimeSlotWrapper> {
        return try {
            val timeSlotResponse = timeSlotRepository.loadMerchantTimeSlots(merchantId)
            if (timeSlotResponse.deliveryDates.isNotEmpty()) {
                //check delivery item first
                if (timeSlotResponse.deliveryDates[0].isFull) {
                    day_loop@ for (day in timeSlotResponse.deliveryDates) {
                        for (time in day.slots) {
                            if (!time.isFull && time.isPick) {
                                day.isSelected = true
                                time.isSelected = true
                                break@day_loop
                            }
                        }
                    }
                } else {
                    timeSlotResponse.deliveryDates[0].isSelected = true
                }
                UseCaseResult.Success(timeSlotResponse)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CURRENT_TIMESLOT_EMPTY_CASE))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }
}
