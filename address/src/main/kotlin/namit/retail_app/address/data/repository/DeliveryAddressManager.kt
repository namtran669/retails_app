package namit.retail_app.address.data.repository

import namit.retail_app.core.data.entity.AddressModel

interface DeliveryAddressManager {
    fun saveDeliveryLocation(deliveryAddress: AddressModel)
    fun getDeliveryLocation(): AddressModel?
    fun removeDeliveryLocation()
    fun haveDeliveryLocation(): Boolean
}

class DeliveryAddressManagerImpl : DeliveryAddressManager {
    private var deliveryAddress: AddressModel? = null

    override fun saveDeliveryLocation(deliveryAddress: AddressModel) {
        this.deliveryAddress = deliveryAddress
    }

    override fun getDeliveryLocation(): AddressModel? = deliveryAddress

    override fun removeDeliveryLocation() {
        deliveryAddress = null
    }

    override fun haveDeliveryLocation(): Boolean {
        return deliveryAddress != null
    }
}