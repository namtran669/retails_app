package namit.retail_app.payment.presentation.payment_credit_error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import namit.retail_app.core.extension.formatCurrency
import namit.retail_app.core.extension.toThaiCurrency
import namit.retail_app.core.presentation.base.BaseDialog
import namit.retail_app.payment.R
import kotlinx.android.synthetic.main.dialog_payment_credit_error.*

class PaymentCreditErrorDialog : BaseDialog() {

    companion object {
        val TAG: String = PaymentCreditErrorDialog::class.java.simpleName
        private const val ARG_TOTAL_PRICE = "ARG_TOTAL_PRICE"
        private const val ARG_ORDER_NO = "ARG_ORDER_NO"
        private const val ARG_CREDIT_CARD_LAST_NUMBER = "ARG_CREDIT_CARD_LAST_NUMBER"

        fun newInstance(
            totalPrice: Double,
            orderNo: String,
            cardNumber: String
        ): PaymentCreditErrorDialog {
            val fragment = PaymentCreditErrorDialog()
            fragment.arguments = Bundle().apply {
                putDouble(ARG_TOTAL_PRICE, totalPrice)
                putString(ARG_ORDER_NO, orderNo)
                putString(ARG_CREDIT_CARD_LAST_NUMBER, cardNumber)
            }
            return fragment
        }
    }

    private var totalPrice: Double = 0.0
    private var orderNo: String = ""
    private var cardNumber: String = ""

    var onDismiss: () -> Unit = {}
    var onClickChangePayment: () -> Unit = {}
    var onClickTryAgain: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        totalPrice = arguments?.getDouble(ARG_TOTAL_PRICE, 0.0) ?: 0.0
        orderNo = arguments?.getString(ARG_ORDER_NO, "") ?: ""
        cardNumber = arguments?.getString(ARG_CREDIT_CARD_LAST_NUMBER, "") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_payment_credit_error, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        totalPriceTextView.text =
            totalPrice.formatCurrency().toThaiCurrency()
        orderNoTextView.text = orderNo
        retryButton.text = resources.getString(R.string.retry_with_this_card, cardNumber)

        retryButton.setOnClickListener {
            onClickTryAgain.invoke()
            dismiss()
        }
        changePaymentButton.setOnClickListener {
            onClickChangePayment.invoke()
            dismiss()
        }
        tryAgainTextView.setOnClickListener {
            onDismiss.invoke()
            dismiss()
        }
    }
}