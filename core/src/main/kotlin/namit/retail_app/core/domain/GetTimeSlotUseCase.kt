package namit.retail_app.core.domain

import namit.retail_app.core.data.entity.TimeSlotWrapper
import namit.retail_app.core.data.repository.TimeSlotRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetDeliveryDataUseCase {
    suspend fun execute(merchantId: String): UseCaseResult<TimeSlotWrapper>
}

class GetDeliveryDataUseCaseImpl(
    private val timeSlotRepository: TimeSlotRepository
) : GetDeliveryDataUseCase {

    companion object {
        const val ERROR_TIMESLOT_EMPTY_CASE = "ERROR_TIMESLOT_EMPTY_CASE"
    }

    override suspend fun execute(merchantId: String): UseCaseResult<TimeSlotWrapper> {
        return try {
            val timeSlotResponse =
                timeSlotRepository.loadMerchantTimeSlots(merchantId)
            if (timeSlotResponse.deliveryDates.isNotEmpty()) {
                UseCaseResult.Success(timeSlotResponse)
            } else {
                UseCaseResult.Error(Throwable(ERROR_TIMESLOT_EMPTY_CASE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UseCaseResult.Error(Throwable(e.message))
        }
    }

}
