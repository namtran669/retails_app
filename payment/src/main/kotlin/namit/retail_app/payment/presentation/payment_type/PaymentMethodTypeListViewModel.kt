package namit.retail_app.payment.presentation.payment_type

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.payment.data.PaymentMethodTypeModel
import namit.retail_app.core.enums.PaymentType

class PaymentMethodTypeListViewModel : BaseViewModel() {

    val paymentTypeList = MutableLiveData<List<PaymentMethodTypeModel>>()
    val openAddTrueMoney = SingleLiveEvent<Unit>()
    val openAddCreditDebitCard = SingleLiveEvent<Unit>()
    private var currentPaymentTypeList = mutableListOf<PaymentMethodTypeModel>()

    fun renderPaymentTypeList() {
        val mockDataList = listOf(
            PaymentMethodTypeModel(
                title = "Credit / debit card",
                type = PaymentType.CREDIT_CARD
            )
            //todo hide this option for now
//            ,
//            PaymentMethodTypeModel(
//                title = "Truemoney Wallet",
//                type = PaymentType.TRUE_MONEY
//            )
        )
        currentPaymentTypeList.addAll(mockDataList)
        paymentTypeList.value = currentPaymentTypeList
    }

    fun selectedPaymentMethod(type: PaymentType) {
        if (type == PaymentType.TRUE_MONEY) {
            openAddTrueMoney.call()
        } else {
            openAddCreditDebitCard.call()
        }
    }

}