package namit.retail_app.payment.presentation.payment

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.domain.GetUserProfileLocalUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.payment.data.PaymentMethodModel
import namit.retail_app.payment.data.repository.PaymentRepositoryImpl
import namit.retail_app.core.enums.PaymentType
import namit.retail_app.payment.domain.*
import kotlinx.coroutines.launch

class PaymentMethodListViewModel(
    private val saveDeliveryPaymentUseCase: SaveDeliveryPaymentUseCase,
    private val getDeliveryPaymentUseCase: GetDeliveryPaymentUseCase,
    private val getUserPaymentListUseCase: GetUserPaymentListUseCase,
    private val getUserProfileLocalUseCase: GetUserProfileLocalUseCase,
    private val getAllowPaymentMethodListUseCase: GetAllowPaymentMethodListUseCase,
    private val removeUserPaymentMethodUseCase: RemoveUserPaymentMethodUseCase
) :
    BaseViewModel() {

    companion object {
        private const val CASH_INDEX = 0
        private const val TMN_INDEX = 1
    }

    val paymentList = MutableLiveData<List<PaymentMethodModel>>()
    val selectedPaymentMethod = MutableLiveData<PaymentMethodModel>()
    val enableAddNewPaymentButton = MutableLiveData<Boolean>()

    private var currentPaymentList = mutableListOf<PaymentMethodModel>()
    private var allowMethodList = mutableListOf<PaymentMethodModel>()

    private var currentPaymentPage: Int = PaymentRepositoryImpl.FIRST_PAGE
    private var isNoMoreProductData = false
    private var isPaymentListLoading = false

    private var isSupportCreditCard = false


    fun loadUserPaymentList() {
        if (isNoMoreProductData || isPaymentListLoading) {
            return
        }

        isPaymentListLoading = true
        launch {
            //Load payment_method and add COD item
            if (currentPaymentPage == PaymentRepositoryImpl.FIRST_PAGE) {
                val allowMethod = getAllowPaymentMethodListUseCase.execute()
                if (allowMethod is UseCaseResult.Success) {
                    allowMethodList = allowMethod.data!!.toMutableList()
                    enableAddNewPaymentButton.value = false

                    allowMethodList.forEach {
                        when (it.type) {
                            PaymentType.CASH -> {
                                currentPaymentList.add(it)
                                paymentList.value = currentPaymentList
                            }
                            PaymentType.CREDIT_CARD -> {
                                enableAddNewPaymentButton.value = true
                                isSupportCreditCard = true
                            }
                            else -> { }
                        }
                    }

                } else {
                    enableAddNewPaymentButton.value = false
                    paymentList.value = listOf()
                    isPaymentListLoading = false
                    isNoMoreProductData = true
                    return@launch
                }
            }

            //Currently, this user payment method only has Credit Card
            //todo need update query to filter when have other payment method
            if (isSupportCreditCard) {
                val userResult = getUserProfileLocalUseCase.execute()
                if (userResult is UseCaseResult.Success && userResult.data!!.id > 0) {
                    val userPaymentResult = getUserPaymentListUseCase.execute(
                        page = currentPaymentPage
                    )
                    if (userPaymentResult is UseCaseResult.Success) {
                        val newPaymentList = userPaymentResult.data!!
                        currentPaymentList.addAll(userPaymentResult.data!!)

                        if (newPaymentList.size < PaymentRepositoryImpl.SIZE_PAYMENT_EACH_REQUEST) {
                            isNoMoreProductData = true
                        }
                        currentPaymentPage++
                    }
                    paymentList.value = currentPaymentList
                }
            } else {
                isNoMoreProductData = true
            }

            isPaymentListLoading = false
        }
    }

    private fun addCashAndTmnDefaultItem() {
        currentPaymentList.add(
            CASH_INDEX, PaymentMethodModel(
                id = -1,
                title = "Cash on Delivery",
                type = PaymentType.CASH
            )
        )
        //todo hide TMN for now
//        currentPaymentList.add(TMN_INDEX, PaymentMethodModel(
//            id = -1,
//            title = "Truemoney Wallet",
//            description = "Not connected",
//            type = PaymentType.TRUE_MONEY
//        ))
    }

    fun selectedPaymentMethod(indexOfPaymentMethod: Int) {
        currentPaymentList.forEachIndexed { index, paymentMethodModel ->
            paymentMethodModel.isSelected = index == indexOfPaymentMethod
        }
        saveDeliveryPaymentUseCase.execute(currentPaymentList[indexOfPaymentMethod])
        paymentList.value = currentPaymentList
        selectedPaymentMethod.value = currentPaymentList[indexOfPaymentMethod]
    }

    fun deletePaymentMethod(indexOfPaymentMethod: Int) {
        val handleData = currentPaymentList[indexOfPaymentMethod]
        launch {
            val removeResult = removeUserPaymentMethodUseCase.execute(handleData.id)
            if (removeResult is UseCaseResult.Success) {
                currentPaymentList.removeAt(indexOfPaymentMethod)

                //Create Blank TMN
                if (handleData.type == PaymentType.TRUE_MONEY) {
                    addBlankTmnItem()
                }

                paymentList.value = currentPaymentList
            }
        }
    }

    fun updatePaymentSwiped(indexSwipe: Int, isExpand: Boolean) {
        if (currentPaymentList[indexSwipe].id < 0) {
            currentPaymentList[indexSwipe].isSwiped = false
            return
        }
        if (isExpand) {
            currentPaymentList.forEachIndexed { index, address ->
                address.isSwiped = indexSwipe == index
            }

            paymentList.value = currentPaymentList
        } else {
            currentPaymentList[indexSwipe].isSwiped = isExpand
        }
    }

    private fun addBlankTmnItem() {
        currentPaymentList.add(
            TMN_INDEX, PaymentMethodModel(
                id = -1,
                title = "Truemoney Wallet",
                description = "Not connected",
                type = PaymentType.TRUE_MONEY
            )
        )
    }
}