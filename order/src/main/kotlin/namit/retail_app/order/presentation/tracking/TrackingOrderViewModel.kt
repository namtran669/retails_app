package namit.retail_app.order.presentation.tracking

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.enums.OrderStatus
import namit.retail_app.core.extension.timer
import namit.retail_app.core.extension.toMillisecond
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.order.domain.CancelOrderUseCase
import namit.retail_app.order.domain.GetOrderStatusUseCase
import kotlinx.coroutines.launch

class TrackingOrderViewModel(
    private val orderData: OrderModel,
    private val getOrderStatusUseCase: GetOrderStatusUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase
) : BaseViewModel() {
    companion object{
        const val INTERVAL_REFRESH_SECOND = 10
    }

    val orderDetail = MutableLiveData<OrderModel>()
    val orderStatus = MutableLiveData<OrderStatus>()
    val showErrorMessage = MutableLiveData<String>()
    val cancelOrderSuccess = SingleLiveEvent<Unit>()

    init {
        orderDetail.value = orderData
    }

    fun checkingOrderStatus() {
        launch {
            var status: OrderStatus = OrderStatus.CONFIRMED
            timer(INTERVAL_REFRESH_SECOND.toMillisecond()) {
                if (status.value == OrderStatus.COMPLETED.value || status.value == OrderStatus.CANCELLED.value) {
                    cancel()
                } else {
                    val result = getOrderStatusUseCase.execute(orderData.secureKey)
                    if (result is UseCaseResult.Success) {
                        status = OrderStatus.valueOf(result.data!!)
                        orderStatus.value = status
                    }
                }
            }.start()
        }
    }

    fun cancelOrder(){
        launch {
            val cancelResult = cancelOrderUseCase.execute(orderData.secureKey)
            when(cancelResult){
                is UseCaseResult.Success ->{
                    cancelOrderSuccess.call()
                }
                is UseCaseResult.Error ->{
                    showErrorMessage.value = cancelResult.exception.message
                }
            }
        }
    }

}