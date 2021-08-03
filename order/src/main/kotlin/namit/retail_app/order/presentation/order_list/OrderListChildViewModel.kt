package namit.retail_app.order.presentation.order_list

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.data.entity.OrderType
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.order.domain.GetListOrderUseCase
import namit.retail_app.order.domain.GetListOrderUseCaseImpl
import kotlinx.coroutines.launch

class OrderListChildViewModel(
    private val orderType: OrderType,
    private val getListOrderUseCase: GetListOrderUseCase
) : BaseViewModel() {

    companion object {
        const val FIRST_PAGE = 0
    }

    val renderEmptyState = MutableLiveData<Boolean>()
    val orderListData = MutableLiveData<List<OrderModel>>()
    val isNoMoreData = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val scrollToTop = SingleLiveEvent<Unit>()

    private val orderList = mutableListOf<OrderModel>()

    private var currentPage = FIRST_PAGE
    private var isNoMore = false
    private var isStillLoading = false

    fun fetchOrderList() {

        if (!(isNoMore || isStillLoading)) {
            val skeletonDataList = mutableListOf<OrderModel>()
            if (currentPage == FIRST_PAGE) {
                skeletonDataList.add(OrderModel())
                skeletonDataList.add(OrderModel())
                skeletonDataList.add(OrderModel())
            } else {
                skeletonDataList.clear()
                skeletonDataList.addAll(orderList)
                skeletonDataList.add(OrderModel())
                skeletonDataList.add(OrderModel())
            }
            orderListData.value = skeletonDataList

            launch {
                isStillLoading = true
                isLoading.value = true
                val type: String = if (orderType == OrderType.ON_GOING) "ongoing" else "completed"
                val orderListResult =
                    getListOrderUseCase.execute(currentPage, type)
                if (orderListResult is UseCaseResult.Success) {
                    orderList.addAll(orderListResult.data!!)
                    currentPage++
                    orderListData.value = orderList
                    isStillLoading = false
                    isLoading.value = false
                } else {
                    isLoading.value = false
                    if (currentPage == FIRST_PAGE) {
                        renderEmptyState.value = orderType == OrderType.ON_GOING
                    } else {
                        val errorMessage =
                            (orderListResult as UseCaseResult.Error).exception.message ?: ""
                        if (errorMessage == GetListOrderUseCaseImpl.ERROR_EMPTY_ORDER_LIST) {
                            notifyNoMoreData()
                        }
                        orderListData.value = orderList
                    }
                }
            }
        }
    }

    fun reloadOrderList() {
        isNoMore = false
        currentPage = FIRST_PAGE
        isStillLoading = false
        orderList.clear()
        fetchOrderList()
        scrollToTop.call()
    }

    private fun notifyNoMoreData() {
        isNoMore = true
        isNoMoreData.value = isNoMore
    }
}