package namit.retail_app.address.domain

import android.util.Log
import namit.retail_app.address.data.repository.DeliveryAddressManager
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.utils.UseCaseResult

interface SaveDeliveryAddressUseCase {
    fun execute(addressData: AddressModel): UseCaseResult<Boolean>
}

class SaveDeliveryAddressUseCaseImpl(private val deliveryAddressManager: DeliveryAddressManager) :
    SaveDeliveryAddressUseCase {

    companion object {
        private val TAG = SaveDeliveryAddressUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_SAVE_DELIVERY_ADDRESS =
            "ERROR_CANNOT_SAVE_DELIVERY_ADDRESS"
    }

    override fun execute(addressData: AddressModel): UseCaseResult<Boolean> {
        return try {
            deliveryAddressManager.saveDeliveryLocation(addressData)
            val saveResult = deliveryAddressManager.haveDeliveryLocation()
            if (saveResult) {
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_SAVE_DELIVERY_ADDRESS))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}
