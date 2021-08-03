package namit.retail_app.core.data.repository

import namit.retail_app.core.data.entity.SlotDetail
import namit.retail_app.core.data.entity.TimeSlot
import namit.retail_app.core.data.entity.TimeSlotWrapper
import namit.retail_app.core.utils.LocaleUtils
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.GetMerchantTimeSlotQuery

interface TimeSlotRepository {
    suspend fun loadMerchantTimeSlots(storeId: String): TimeSlotWrapper
}

class TimeSlotRepositoryImpl(private val apollo: ApolloClient) : TimeSlotRepository {
    override suspend fun loadMerchantTimeSlots(storeId: String): TimeSlotWrapper {
        val query = GetMerchantTimeSlotQuery
            .builder()
            .storeId(storeId)
            .locale(LocaleUtils.getCurrentLanguage())
            .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()

        val timeSlotResponse = TimeSlotWrapper()
        timeSlotResponse.canDeliveryNow = response.data()?.time_slots()?.can_deliver_now() ?: false

        val dateRawList = response.data()?.time_slots()?.delivery_dates() ?: listOf()
        val deliveryDateList = mutableListOf<TimeSlot>()

        //Add delivery now item
        deliveryDateList.add(
            TimeSlot(
                isDeliveryNow = true,
                isFull = timeSlotResponse.canDeliveryNow.not()
            )
        )

        dateRawList.forEach { timeSlot ->

            if (!timeSlot.fragments().timeSlotFragment().date().isNullOrEmpty()
                && !timeSlot.fragments().timeSlotFragment().day_of_week().isNullOrEmpty()
            ) {

                val slotDetails = timeSlot.fragments().timeSlotFragment().slots()
                val slotDetailsResult = mutableListOf<SlotDetail>()
                slotDetails?.forEach { slotDetail ->

                    if (!slotDetail.fragments().slotDetailFragment().hour().isNullOrEmpty()
                        && !slotDetail.fragments().slotDetailFragment().pickup_at().isNullOrEmpty()
                    ) {
                        slotDetailsResult.add(
                            SlotDetail(
                                hour = slotDetail.fragments().slotDetailFragment().hour()!!,
                                pickupAt = slotDetail.fragments().slotDetailFragment().pickup_at()!!,
                                isFull = slotDetail.fragments().slotDetailFragment().is_full
                                    ?: false,
                                isPick = slotDetail.fragments().slotDetailFragment().is_pick
                                    ?: false
                            )
                        )
                    }
                }

                deliveryDateList.add(
                    TimeSlot(
                        date = timeSlot.fragments().timeSlotFragment().date()!!,
                        dayOfWeek = timeSlot.fragments().timeSlotFragment().day_of_week()!!,
                        slots = slotDetailsResult
                    )
                )
            }
        }

        timeSlotResponse.deliveryDates = deliveryDateList

        return timeSlotResponse
    }
}