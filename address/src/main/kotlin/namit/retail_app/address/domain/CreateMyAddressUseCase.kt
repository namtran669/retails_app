package namit.retail_app.address.domain

import android.util.Log
import namit.retail_app.address.data.repository.AddressRepository
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.utils.UseCaseResult

interface CreateMyAddressUseCase {
    suspend fun execute(address: AddressModel): UseCaseResult<Int>
}

class CreateMyAddressUseCaseImpl(private val addressRepository: AddressRepository) :
    CreateMyAddressUseCase {

    companion object {
        private val TAG = CreateMyAddressUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_CREATE_USER_ADDRESS =
            "ERROR_CANNOT_CREATE_USER_ADDRESS"
    }

    override suspend fun execute(address: AddressModel): UseCaseResult<Int> {
        return try {
            val result = addressRepository.createAddressItem(address)
            if (result.id != -1) {
                UseCaseResult.Success(result.id)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_CREATE_USER_ADDRESS))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}
