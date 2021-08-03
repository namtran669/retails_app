package namit.retail_app.address.presentation.edit

import androidx.lifecycle.MutableLiveData
import namit.retail_app.address.domain.CreateMyAddressUseCase
import namit.retail_app.address.domain.SaveDeliveryAddressUseCase
import namit.retail_app.address.domain.UpdateMyAddressUseCase
import namit.retail_app.core.data.entity.AddressListType
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.domain.GetUserProfileLocalUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class EditAddressBottomDialogViewModel(
    private var editAddressData: AddressModel?,
    private val saveToDeliveryAddress: Boolean,
    private val createUserAddressUseCase: CreateMyAddressUseCase,
    private val updateMyAddressUseCase: UpdateMyAddressUseCase,
    private val getUserProfileLocalUseCase: GetUserProfileLocalUseCase,
    private val saveDeliveryAddressUseCase: SaveDeliveryAddressUseCase
) : BaseViewModel() {

    companion object {
        private const val VALID_LENGTH_OF_PHONE_NUMBER = 16
    }

    val addressContent = MutableLiveData<AddressModel>()
    val addressFieldLiveData = MutableLiveData<String>()
    val dismissDialog = SingleLiveEvent<Unit>()
    val showPopupMessage = SingleLiveEvent<String>()
    val showUpdateSavedAddressDialog = SingleLiveEvent<AddressModel>()
    val shouldUpdateLocationData = SingleLiveEvent<Boolean>()
    val showLabelMissing = SingleLiveEvent<Unit>()
    val showPhoneNumberInvalid = SingleLiveEvent<Unit>()

    private var handleAddressData = AddressModel()

    init {
        editAddressData?.let {
            renderAddressInfo(it)
            handleAddressData = it
        }
    }

    private fun getUserId(): Int {
        var userId = -1
        val result = getUserProfileLocalUseCase.execute()
        if (result is UseCaseResult.Success) {
            userId = result.data?.id ?: -1
        }
        return userId

    }

    private fun saveAddressData() {
        launch {
            if (editAddressData != null && editAddressData!!.id > 0) {
                //case UPDATE item
                handleAddressData.apply {
                    when (val result = updateMyAddressUseCase.execute(this)) {
                        is UseCaseResult.Success -> {
                            if (saveToDeliveryAddress) {
                                saveDeliveryAddress(editAddressData!!)
                            }
                            dismissDialog.call()
                        }
                        is UseCaseResult.Error -> showPopupMessage.value = result.exception.message
                    }
                }
            } else {
                // case CREATE item
                when (val result = createUserAddressUseCase.execute(handleAddressData)) {
                    is UseCaseResult.Success -> {
                        if (saveToDeliveryAddress) {
                            saveDeliveryAddress(editAddressData!!)
                        }
                        dismissDialog.call()
                    }
                    is UseCaseResult.Error -> showPopupMessage.value = result.exception.message
                }
            }
        }
    }

    fun saveAddressDetail(data: String) {
        handleAddressData.addressDetail = data
    }

    fun savePhoneNumber(data: String) {
        handleAddressData.phone = data
    }

    fun saveNote(data: String) {
        handleAddressData.note = data
    }

    fun saveName(data: String) {
        handleAddressData.name = data
    }

    fun saveDefault(isDefault: Boolean) {
        handleAddressData.isDefault = isDefault
    }

    fun saveAddressType(type: AddressListType) {
        if (handleAddressData.type != AddressListType.OTHER && type == AddressListType.OTHER) {
            handleAddressData.name = ""
        }
        handleAddressData.type = type
        renderAddressInfo(handleAddressData)
    }

    fun openUpdateSavedAddressDialog() {
        showUpdateSavedAddressDialog.value = handleAddressData
    }

    fun updateLocationData(newData: AddressModel) {
        handleAddressData.lat = newData.lat
        handleAddressData.lng = newData.lng
        handleAddressData.landMark = newData.landMark
        handleAddressData.address = newData.address
        renderAddressInfo(handleAddressData)
    }

    fun triggerUpdateLocationData() {
        shouldUpdateLocationData.value = true
    }

    private fun renderAddressInfo(data: AddressModel) {
        addressContent.value = data
        showAddressContentField(data)
    }

    private fun showAddressContentField(data: AddressModel) {
        if (!data.landMark.isNullOrBlank()) {
            addressFieldLiveData.value = data.landMark!!
        } else if (data.address != null) {
            addressFieldLiveData.value = data.address!!
        }
    }

    fun validateInputData() {
        if (!validateAddressLabel()) {
            showLabelMissing.call()
        } else if (!validateAddressPhone()) {
            showPhoneNumberInvalid.call()
        } else {
            saveAddressData()
        }
    }

    private fun validateAddressLabel(): Boolean {
        return !handleAddressData.name.isNullOrBlank()
    }

    private fun validateAddressPhone(): Boolean {
        return if (!handleAddressData.phone.isNullOrBlank()) {
            handleAddressData.phone!!.length == VALID_LENGTH_OF_PHONE_NUMBER
        } else {
            false
        }
    }

    private fun saveDeliveryAddress(addressModel: AddressModel) =
        saveDeliveryAddressUseCase.execute(addressModel)
}