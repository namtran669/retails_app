package namit.retail_app.address.domain

import android.util.Log
import namit.retail_app.address.data.repository.AddressRepository
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.utils.UseCaseResult

interface DeleteMyAddressUseCase {
    suspend fun execute(address: AddressModel): UseCaseResult<Boolean>
}

class DeleteUserAddressUseCaseImpl(private val addressRepository: AddressRepository) :
    DeleteMyAddressUseCase {

    companion object {
        private val TAG = DeleteUserAddressUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_DELETE_USER_ADDRESS =
            "ERROR_CANNOT_DELETE_USER_ADDRESS"
    }

    override suspend fun execute(address: AddressModel): UseCaseResult<Boolean> {
        return try {
            val result = addressRepository.deleteAddressItem(address)
            if (result) {
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_DELETE_USER_ADDRESS))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}
