package namit.retail_app.core.data.repository

import namit.retail_app.core.data.entity.TimeSlot

interface DeliveryTimeManager {
    fun saveDeliveryTime(merchantId: String, deliveryTime: TimeSlot)
    fun getDeliveryTime(merchantId: String): TimeSlot?
    fun removeDeliveryTime(merchantId: String)
    fun haveDeliveryTime(merchantId: String): Boolean
}

class DeliveryTimeManagerImpl : DeliveryTimeManager {
    private val deliveryTimeList = hashMapOf<String, TimeSlot>()

    override fun saveDeliveryTime(merchantId: String, deliveryTime: TimeSlot) {
        this.deliveryTimeList[merchantId] = deliveryTime
    }

    override fun getDeliveryTime(merchantId: String): TimeSlot? {
        return deliveryTimeList[merchantId]
    }

    override fun removeDeliveryTime(merchantId: String) {
        deliveryTimeList.remove(merchantId)
    }

    override fun haveDeliveryTime(merchantId: String): Boolean {
        return deliveryTimeList.containsKey(merchantId)
    }
}