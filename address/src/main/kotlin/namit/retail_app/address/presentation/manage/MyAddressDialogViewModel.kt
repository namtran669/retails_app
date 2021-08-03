package namit.retail_app.address.presentation.manage

import androidx.lifecycle.MutableLiveData
import namit.retail_app.address.domain.DeleteMyAddressUseCase
import namit.retail_app.address.domain.GetDeliveryAddressUseCase
import namit.retail_app.address.domain.GetUserAddressListUseCase
import namit.retail_app.address.domain.GetUserAddressListUseCaseImpl.Companion.ERROR_USER_ADDRESS_LIST_EMPTY
import namit.retail_app.address.domain.SaveDeliveryAddressUseCase
import namit.retail_app.core.data.entity.AddressListType
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.domain.GetUserProfileLocalUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class MyAddressDialogViewModel(
    private val saveDeliveryAddressUseCase: SaveDeliveryAddressUseCase,
    private val getDeliveryAddressUseCase: GetDeliveryAddressUseCase,
    private val getUserAddressListUseCase: GetUserAddressListUseCase,
    private val deleteMyAddressUseCase: DeleteMyAddressUseCase,
    private val getUserProfileLocalUseCase: GetUserProfileLocalUseCase
) : BaseViewModel() {

    val favoriteListLiveData = MutableLiveData<List<AddressModel>>()
    val otherListLiveData = MutableLiveData<List<AddressModel>>()
    val showNoAddressText = SingleLiveEvent<Unit>()
    val openTitleFavoriteType = SingleLiveEvent<Boolean>()
    val openTitleOtherType = SingleLiveEvent<Boolean>()
    val openEditAddressDialog = SingleLiveEvent<AddressModel>()
    val dismissDialog = SingleLiveEvent<Unit>()

    private var deliveryAddress: AddressModel? = null
    private val favoriteAddressList = mutableListOf<AddressModel>()
    private val otherAddressList = mutableListOf<AddressModel>()

    fun loadAddressList() {
        launch {
            val userProfileResult = getUserProfileLocalUseCase.execute()
            if (userProfileResult is UseCaseResult.Success) {
                val userId = userProfileResult.data!!.id
                val listResult = getUserAddressListUseCase.execute(userId)

                if (listResult is UseCaseResult.Success) {

                    val userAddressList = listResult.data!!

                    loadDeliveryAddress()

                    favoriteAddressList.clear()
                    otherAddressList.clear()

                    userAddressList.forEach { address ->
                        deliveryAddress?.let { deliveryAddress ->
                            //Only address from API have id
                            if (deliveryAddress.id > 0 && address.id == deliveryAddress.id) {
                                address.isSelected = true
                            }
                        }

                        when(address.type) {
                            AddressListType.OTHER -> otherAddressList.add(address)
                            AddressListType.WORK -> favoriteAddressList.add(address)
                            AddressListType.HOME -> favoriteAddressList.add(address)
                        }
                    }

                    renderMyAddressList()
                } else if (listResult is UseCaseResult.Error && listResult.exception.message == ERROR_USER_ADDRESS_LIST_EMPTY) {
                    showNoAddressText.call()
                }
            } else {
                showNoAddressText.call()
            }
        }
    }

    fun presentEditAddressOption(addressSelect: AddressModel? = null) {
        openEditAddressDialog.value = addressSelect
    }

    private fun saveDeliveryAddress(addressData: AddressModel) {
        saveDeliveryAddressUseCase.execute(addressData)
    }

    private fun loadDeliveryAddress() {
        val deliveryData = getDeliveryAddressUseCase.execute()
        if (deliveryData is UseCaseResult.Success) {
            deliveryAddress = deliveryData.data!!
        }
    }

    fun removeAddressOption(addressData: AddressModel) {
        launch {
            val result = deleteMyAddressUseCase.execute(addressData)
            if (result is UseCaseResult.Success && result.data!!) {
                when(addressData.type ) {
                    AddressListType.OTHER -> otherAddressList.remove(addressData)
                    else ->    favoriteAddressList.remove(addressData)
                }

                renderMyAddressList()
            }
        }
    }

    fun updateFavoriteSelection(indexSelect: Int) {
        favoriteAddressList.forEachIndexed { index, address ->
            address.isSelected = indexSelect == index
        }

        deliveryAddress = favoriteAddressList[indexSelect].apply {
            saveDeliveryAddress(this)
        }

        otherAddressList.forEach { it.isSelected = false }

        renderMyAddressList()
        dismissDialog.call()
    }

    fun updateOtherSelection(indexSelect: Int) {
        otherAddressList.forEachIndexed { index, address ->
            address.isSelected = indexSelect == index
        }

        deliveryAddress = otherAddressList[indexSelect].apply {
            saveDeliveryAddress(this)
        }

        favoriteAddressList.forEach { it.isSelected = false }

        renderMyAddressList()
        dismissDialog.call()
    }

    fun updateFavoriteSwipe(indexSwipe: Int, isExpand: Boolean) {
        if (isExpand) {
            favoriteAddressList.forEachIndexed { index, address ->
                address.isSwiped = indexSwipe == index
            }
            otherAddressList.forEach {
                it.isSwiped = !isExpand
            }

            renderMyAddressList()
        } else {
            favoriteAddressList[indexSwipe].isSwiped = isExpand
        }
    }

    fun updateOtherSwipe(indexSwipe: Int, isExpand: Boolean) {
        if (isExpand) {
            otherAddressList.forEachIndexed {index, address ->
                address.isSwiped = indexSwipe == index
            }

            favoriteAddressList.forEach {
                it.isSwiped = !isExpand
            }

            renderMyAddressList()
        } else {
            otherAddressList[indexSwipe].isSwiped = isExpand
        }
    }

    private fun renderMyAddressList() {
        if (favoriteAddressList.isNotEmpty()) {
            favoriteListLiveData.value = favoriteAddressList
            openTitleFavoriteType.value = true
        } else {
            openTitleFavoriteType.value = false
        }

        if (otherAddressList.isNotEmpty()) {
            otherListLiveData.value = otherAddressList
            openTitleOtherType.value = true
        } else {
            openTitleOtherType.value = false
        }

        if(favoriteAddressList.isEmpty() && otherAddressList.isEmpty()) {
            showNoAddressText.call()
        }
    }
}