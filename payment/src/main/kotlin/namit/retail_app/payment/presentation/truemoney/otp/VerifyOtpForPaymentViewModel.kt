package namit.retail_app.payment.presentation.truemoney.otp

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.extension.toPhoneNumberPattern
import namit.retail_app.core.presentation.base.BaseViewModel

class VerifyOtpForPaymentViewModel(
    private val phoneNumber: String
): BaseViewModel() {

    companion object {
        private const val LOCAL_THAILAND_PHONE_CODE = "+66"
        const val LENGTH_OF_OTP_CODE = 6
    }

    val userPhoneNumber = MutableLiveData<String>()
    val changeOtpTextSize = MutableLiveData<Boolean>()
    val enableConfirmButton = MutableLiveData<Boolean>()

    fun renderPhoneNumber() {
        if (phoneNumber.isNotEmpty()) {
            userPhoneNumber.value =
                "$LOCAL_THAILAND_PHONE_CODE " +
                        phoneNumber.substring(1, phoneNumber.length).toPhoneNumberPattern()
        }
    }

    fun validateOtp(otpCode: String) {
        changeOtpTextSize.value = otpCode.isNotEmpty()
        enableConfirmButton.value = otpCode.length == LENGTH_OF_OTP_CODE
    }
}