package namit.retail_app.order.presentation.order_detail

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.presentation.base.BaseViewModel

class OrderDetailViewModel(private val orderData: OrderModel) : BaseViewModel() {

    val orderDetail = MutableLiveData<OrderModel>()

    init {
        orderDetail.value = orderData
    }

}