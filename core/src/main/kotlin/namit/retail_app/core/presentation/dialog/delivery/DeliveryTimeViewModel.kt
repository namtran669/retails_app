package namit.retail_app.core.presentation.dialog.delivery

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.SlotDetail
import namit.retail_app.core.data.entity.TimeSlot
import namit.retail_app.core.domain.GetCurrentDeliveryTimeSlotDataUseCase
import namit.retail_app.core.domain.GetSelectedDeliveryTimeUseCase
import namit.retail_app.core.domain.GetUserProfileLocalUseCase
import namit.retail_app.core.domain.SaveDeliveryTimeUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.tracking.TrackingValue
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class DeliveryTimeViewModel(
    private val merchantId: String,
    private val getTimeSlotUseCase: GetCurrentDeliveryTimeSlotDataUseCase,
    private val saveDeliveryTimeUseCase: SaveDeliveryTimeUseCase,
    private val userProfileLocalUseCase: GetUserProfileLocalUseCase,
    private val eventTrackingManager: EventTrackingManager,
    private val getSelectedDeliveryTimeUseCase: GetSelectedDeliveryTimeUseCase
) : BaseViewModel() {

    val deliveryDaySlotList = MutableLiveData<List<TimeSlot>>()
    val deliveryTimeSlotList = MutableLiveData<List<SlotDetail>>()
    val deliveryDaySelectedIndex = MutableLiveData<Int>()
    val deliveryTimeSlotSelectedIndex = MutableLiveData<Int>()
    val selectedTimeSlot = MutableLiveData<TimeSlot>()
    val deliveryNowLiveData = MutableLiveData<TimeSlot>()
    val confirmButtonStatus = MutableLiveData<Boolean>()
    val showLoadingView = MutableLiveData<Boolean>()

    private var daySlotListData = listOf<TimeSlot>()

    fun loadDeliveryData() {
        launch {
            showLoadingView.value = true
            confirmButtonStatus.value = false
            val timeSlotListResult =
                getTimeSlotUseCase.execute(merchantId)

            if (timeSlotListResult is UseCaseResult.Success) {
                val deliveryDateList = timeSlotListResult.data!!.deliveryDates
                daySlotListData = validateDayUnavailable(deliveryDateList)
                val getSelectedTimeSlot = getSelectedDeliveryTimeUseCase.execute(merchantId)
                if (getSelectedTimeSlot is UseCaseResult.Success) {
                    val selectedTimeSlot = getSelectedTimeSlot.data!!
                    val selectedSlot = selectedTimeSlot.slots.firstOrNull { it.isSelected }

                    if (selectedTimeSlot.isDeliveryNow) {
                        deliveryNowLiveData.value = selectedTimeSlot
                    } else {
                        daySlotListData.firstOrNull { it.isDeliveryNow }?.isSelected = false
                        daySlotListData.firstOrNull {
                            it.date == selectedTimeSlot.date
                        }?.apply {
                            isSelected = true
                            slots.firstOrNull {
                                it.pickupAt == selectedSlot?.pickupAt
                            }?.isSelected = true
                            deliveryTimeSlotList.value = slots
                            deliveryTimeSlotSelectedIndex.value = slots.indexOfFirst {
                                it.isSelected
                            }
                        } ?: run {
                            daySlotListData.firstOrNull {
                                !it.isFull
                            }?.apply {
                                isSelected = true
                                deliveryTimeSlotList.value = slots
                                deliveryTimeSlotSelectedIndex.value = slots.indexOfFirst {
                                    it.isSelected
                                }
                            }?.slots?.firstOrNull {
                                !it.isFull && it.isPick
                            }?.isSelected = true
                        }
                    }
                    confirmButtonStatus.value = true
                    deliveryDaySlotList.value = daySlotListData
                    deliveryDaySelectedIndex.value = daySlotListData.indexOfFirst {
                        it.isSelected
                    }
                } else {
                    //Render day section
                    deliveryDaySlotList.value = daySlotListData
                    deliveryDaySelectedIndex.value = daySlotListData.indexOfFirst {
                        it.isSelected
                    }
                    //Render time section
                    for (day in daySlotListData) {
                        if (day.isDeliveryNow && day.isSelected) {
                            deliveryNowLiveData.value = day
                            confirmButtonStatus.value = true
                            break
                        } else if (day.isSelected) {
                            deliveryTimeSlotList.value = day.slots
                            deliveryTimeSlotSelectedIndex.value =
                                day.slots.indexOfFirst { it.isSelected }
                            confirmButtonStatus.value = true
                            break
                        }
                    }
                }

                showLoadingView.value = false
            }
        }
    }

    fun saveSelectedTimeSlot() {
        var userId = -1
        val userProfileResult = userProfileLocalUseCase.execute()
        if (userProfileResult is UseCaseResult.Success) {
            userId = userProfileResult.data!!.id
        }

        deliveryDaySlotList.value?.first { it.isSelected }?.let { day ->
            if (day.isDeliveryNow) {
                selectedTimeSlot.value = day
                saveDeliveryTimeUseCase.execute(merchantId, day)
                eventTrackingManager.trackMerchantTimeSlotSelection(
                    timeSlotType = TrackingValue.VALUE_TIME_SLOT_DELIVERY_NOW,
                    userId = userId
                )
            } else {
                day.slots.firstOrNull { it.isSelected }?.let { _ ->
                    selectedTimeSlot.value = day
                    saveDeliveryTimeUseCase.execute(merchantId, day)
                }
                eventTrackingManager.trackMerchantTimeSlotSelection(
                    timeSlotType = TrackingValue.VALUE_TIME_SLOT_SCHEDULED,
                    userId = userId
                )
            }
        }
    }

    fun updateDaySlotSection(indexSelect: Int) {
        daySlotListData.forEachIndexed { index, address ->
            address.isSelected = indexSelect == index
        }

        daySlotListData[indexSelect].apply {
            if (isDeliveryNow) {
                deliveryNowLiveData.value = this
            } else {
                deliveryTimeSlotList.value = slots
            }
            deliveryDaySlotList.value = daySlotListData
            confirmButtonStatus.value = isFull.not()
        }
    }


    private fun validateDayUnavailable(inputData: List<TimeSlot>): List<TimeSlot> {
        for (day in inputData) {
            //just validate normal day
            if (!day.isDeliveryNow) {
                var timeFullCount = 0
                for (time in day.slots) {
                    if (!time.isAvailable()) timeFullCount++
                }
                if (timeFullCount == day.slots.size) day.isFull = true
            }
        }
        return inputData
    }
}
