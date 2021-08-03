package namit.retail_app.payment.presentation.card

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.payment.data.entity.PaymentCard
import namit.retail_app.payment.enums.PaymentCardType

class PaymentCardViewModel(): BaseViewModel() {

    val paymentCollectionList = MutableLiveData<List<PaymentCard>>()

    fun loadPaymentCollection(userId: String) {
        val mockData = listOf(
            PaymentCard(
                id = "",
                title = "",
                type = PaymentCardType.CREDIT
            ),
            PaymentCard(
                id = "",
                title = "",
                type = PaymentCardType.REGISTER
            ),
            PaymentCard(
                id = "",
                title = "",
                type = PaymentCardType.POINT
            )
        )

        paymentCollectionList.value = mockData
    }
}