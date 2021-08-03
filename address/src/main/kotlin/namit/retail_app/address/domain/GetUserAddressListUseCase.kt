package namit.retail_app.address.domain

import android.util.Log
import namit.retail_app.address.data.repository.AddressRepository
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.utils.UseCaseResult

interface GetUserAddressListUseCase {
    suspend fun execute(userId: Int): UseCaseResult<List<AddressModel>>
}

class GetUserAddressListUseCaseImpl(private val addressRepository: AddressRepository) :
    GetUserAddressListUseCase {

    companion object {
        private val TAG = GetUserAddressListUseCaseImpl::class.java.simpleName
        const val ERROR_USER_ADDRESS_LIST_EMPTY =
            "ERROR_USER_ADDRESS_LIST_EMPTY"
    }

    override suspend fun execute(userId: Int): UseCaseResult<List<AddressModel>> {
        return try {
            val userAddressListData = addressRepository.getUserAddressList()
            if (userAddressListData.isNotEmpty()) {
                UseCaseResult.Success(userAddressListData)
            } else {
                UseCaseResult.Error(Throwable(ERROR_USER_ADDRESS_LIST_EMPTY))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}
