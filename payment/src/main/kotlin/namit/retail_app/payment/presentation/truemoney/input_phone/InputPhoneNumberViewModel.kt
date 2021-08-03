package namit.retail_app.payment.presentation.truemoney.input_phone

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent

class InputPhoneNumberViewModel(
    private var phoneNumber: String
): BaseViewModel() {

    companion object {
        private const val LENGTH_OF_PHONE_NUMBER = 16
    }

    val userPhoneNumber = MutableLiveData<String>()
    val enableConfirmButton = MutableLiveData<Boolean>()
    val openValidateOtpDialog = SingleLiveEvent<String>()

    fun renderPhoneNumber() {
        userPhoneNumber.value = phoneNumber
    }

    fun presentValidateOtp() {
        openValidateOtpDialog.value = phoneNumber
    }

    fun validatePhoneNumber(newPhoneNumber: String) {
        phoneNumber = newPhoneNumber
        enableConfirmButton.value = newPhoneNumber.length == LENGTH_OF_PHONE_NUMBER
    }

}