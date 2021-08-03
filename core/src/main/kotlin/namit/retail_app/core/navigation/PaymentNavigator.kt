package namit.retail_app.core.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

interface PaymentNavigator {
    fun openPaymentWithPaymentList(context: Context): Intent
    fun getInputPhoneNumberDialog(phoneNumber: String): DialogFragment
    fun getPaymentList(): Fragment
    fun getPaymentTypeList(): Fragment
    fun getValidateOtpForPayment(phoneNumber: String): DialogFragment
    fun getAddCreditCardDialog(): DialogFragment
    fun getPaymentCreditError( totalPrice: Double,
                               orderNo: String,
                               cardNumber: String): DialogFragment
}