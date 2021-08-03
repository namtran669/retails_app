package namit.retail_app.app.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import namit.retail_app.core.navigation.PaymentNavigator
import namit.retail_app.payment.presentation.PaymentActivity
import namit.retail_app.payment.presentation.credit_card.AddCreditDebitCardDialogFragment
import namit.retail_app.payment.presentation.payment.PaymentMethodListFragment
import namit.retail_app.payment.presentation.payment_credit_error.PaymentCreditErrorDialog
import namit.retail_app.payment.presentation.payment_type.PaymentMethodTypeListFragment
import namit.retail_app.payment.presentation.truemoney.input_phone.InputPhoneNumberDialogFragment
import namit.retail_app.payment.presentation.truemoney.otp.VerifyOtpForPaymentDialogFragment

class PaymentNavigatorImpl : PaymentNavigator {

    override fun openPaymentWithPaymentList(context: Context): Intent {
        return PaymentActivity.getPaymentList(context = context)
    }

    override fun getInputPhoneNumberDialog(phoneNumber: String): DialogFragment {
        return InputPhoneNumberDialogFragment.newInstance(phoneNumber = phoneNumber)
    }

    override fun getPaymentList(): Fragment {
        return PaymentMethodListFragment.newInstance()
    }

    override fun getPaymentTypeList(): Fragment {
        return PaymentMethodTypeListFragment.newInstance()
    }

    override fun getValidateOtpForPayment(phoneNumber: String): DialogFragment {
        return VerifyOtpForPaymentDialogFragment.newInstance(phoneNumber = phoneNumber)
    }

    override fun getAddCreditCardDialog(): DialogFragment {
        return AddCreditDebitCardDialogFragment.newInstance()
    }

    override fun getPaymentCreditError(
        totalPrice: Double,
        orderNo: String,
        cardNumber: String
    ): DialogFragment {
        return PaymentCreditErrorDialog.newInstance(
            totalPrice = totalPrice,
            orderNo = orderNo,
            cardNumber = cardNumber
        )
    }
}