package namit.retail_app.payment.presentation

import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.payment.presentation.payment.PaymentMethodListFragment

class PaymentViewModel(
    private val pageTag: String
): BaseViewModel() {

    val openPaymentList = SingleLiveEvent<Unit>()

    fun renderPage() {
        if (pageTag == PaymentMethodListFragment.TAG) {
            openPaymentList.call()
        }
    }
}