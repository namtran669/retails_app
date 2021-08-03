package namit.retail_app.core.viewmodel

import namit.retail_app.core.data.entity.TimeSlot
import namit.retail_app.core.data.entity.TimeSlotWrapper
import namit.retail_app.core.domain.GetCurrentDeliveryTimeSlotDataUseCase
import namit.retail_app.core.domain.GetSelectedDeliveryTimeUseCase
import namit.retail_app.core.domain.GetUserProfileLocalUseCase
import namit.retail_app.core.domain.SaveDeliveryTimeUseCase
import namit.retail_app.core.presentation.dialog.delivery.DeliveryTimeViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.testutils.BaseViewModelTest
import namit.retail_app.testutils.JsonFactory
import namit.retail_app.testutils.TestObserver
import namit.retail_app.testutils.testObserver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class DeliveryTimeViewModelTest : BaseViewModelTest() {
    private lateinit var timeSlotListObserver: TestObserver<List<TimeSlot>>
    private lateinit var selectedTimeSlotObserver: TestObserver<TimeSlot>

    private val getCurrentDeliveryTimeSlotDataUseCase: GetCurrentDeliveryTimeSlotDataUseCase = mock()
    private val saveDeliveryTimeUseCase: SaveDeliveryTimeUseCase = mock()
    private val userProfileLocalUseCase: GetUserProfileLocalUseCase = mock()
    private val eventTrackingManager: EventTrackingManager = mock()
    private val getSelectedDeliveryTimeUseCase: GetSelectedDeliveryTimeUseCase = mock()

    private lateinit var viewModel: DeliveryTimeViewModel

    private val merchantId = "L123456"

    override fun setup() {
        super.setup()
        viewModel = DeliveryTimeViewModel(
            merchantId = merchantId,
            getTimeSlotUseCase = getCurrentDeliveryTimeSlotDataUseCase,
            saveDeliveryTimeUseCase = saveDeliveryTimeUseCase,
            userProfileLocalUseCase = userProfileLocalUseCase,
            eventTrackingManager = eventTrackingManager,
            getSelectedDeliveryTimeUseCase = getSelectedDeliveryTimeUseCase
        )
        initTestObserver()
    }

    private fun initTestObserver() {
        timeSlotListObserver = viewModel.deliveryDaySlotList.testObserver()
        selectedTimeSlotObserver = viewModel.selectedTimeSlot.testObserver()
    }

    @Test
    fun loadTimeSlotList_success() = runBlocking {
        val responseList = mutableListOf(
            TimeSlot(), TimeSlot(), TimeSlot(), TimeSlot(), TimeSlot(), TimeSlot(), TimeSlot()
        )
        whenever(getCurrentDeliveryTimeSlotDataUseCase.execute(merchantId)).thenReturn(
            UseCaseResult.Success(
                TimeSlotWrapper().apply {
                    deliveryDates = responseList
                }
            )
        )

        viewModel.loadDeliveryData()

        Assert.assertEquals(7, timeSlotListObserver.observedValues[0]!!.size)

        //other
        assert(selectedTimeSlotObserver.observedValues.isEmpty())
    }

    @Test
    fun getSelectedTimeSlot_success() = runBlocking {
        val responseType = object : TypeToken<List<TimeSlot>>() {}.type
        val timeSlotJson = JsonFactory.getStringFromJsonTestResource("time_slot_data_get_time_selected.json")
        val timeSlotListObj = Gson().fromJson<List<TimeSlot>>(timeSlotJson, responseType)

        whenever(getCurrentDeliveryTimeSlotDataUseCase.execute(merchantId)).thenReturn(
            UseCaseResult.Success(
                TimeSlotWrapper().apply {
                    deliveryDates = timeSlotListObj
                }
            )
        )

        viewModel.loadDeliveryData()
        viewModel.saveSelectedTimeSlot()

        val selectedTimeSlot = selectedTimeSlotObserver.observedValues[0]

        Assert.assertEquals(true, selectedTimeSlot?.isSelected)
        Assert.assertEquals("2020-01-18", selectedTimeSlot?.date)
        Assert.assertEquals(true, selectedTimeSlot?.slots!![1].isSelected)
        Assert.assertEquals("12:00 - 14:00", selectedTimeSlot.slots!![1].hour)

        //other
    }

}