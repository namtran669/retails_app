package namit.retail_app.address.domain

import android.util.Log
import namit.retail_app.address.data.repository.DeliveryAddressManager
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.utils.UseCaseResult

interface GetDeliveryAddressUseCase {
    fun execute(): UseCaseResult<AddressModel>
}

class GetDeliveryAddressUseCaseImpl(private val deliveryAddressManager: DeliveryAddressManager) :
    GetDeliveryAddressUseCase {

    companion object {
        private val TAG = GetDeliveryAddressUseCaseImpl::class.java.simpleName
        private const val ERROR_NO_DELIVERY_ADDRESS =
            "ERROR_NO_DELIVERY_ADDRESS"
    }

    override fun execute(): UseCaseResult<AddressModel> {
        return try {
            if (deliveryAddressManager.haveDeliveryLocation()) {
                val deliveryAddressData = deliveryAddressManager.getDeliveryLocation()
                UseCaseResult.Success(deliveryAddressData)
            } else {
                UseCaseResult.Error(Throwable(ERROR_NO_DELIVERY_ADDRESS))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }
}
