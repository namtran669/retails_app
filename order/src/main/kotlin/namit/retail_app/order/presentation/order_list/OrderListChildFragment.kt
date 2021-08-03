package namit.retail_app.order.presentation.order_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.data.entity.OrderType
import namit.retail_app.core.extension.gone
import namit.retail_app.core.extension.visible
import namit.retail_app.core.navigation.GroceryNavigator
import namit.retail_app.core.navigation.OrderNavigator
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.core.presentation.widget.EndlessProductVerticalListListener
import namit.retail_app.order.R
import namit.retail_app.order.presentation.adapter.OrderHistoryListAdapter
import namit.retail_app.order.presentation.adapter.OrderOnGoingListAdapter
import namit.retail_app.order.presentation.tracking.TrackingOrderDialog
import kotlinx.android.synthetic.main.fragment_order_ongoing.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ObsoleteCoroutinesApi
class OrderListChildFragment : BaseFragment() {
    companion object {
        const val TAG = "OrderListChildFragment"
        private const val ARG_ORDER_TYPE = "ARG_ORDER_TYPE"
        fun getNewInstance(orderType: String): OrderListChildFragment {
            val fragment = OrderListChildFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_ORDER_TYPE, orderType)
            }
            return fragment
        }
    }

    private val childViewModel: OrderListChildViewModel by viewModel(parameters = {
        parametersOf(
            OrderType.valueOf(arguments!!.getString(ARG_ORDER_TYPE)!!)
        )
    })

    private val groceryNavigator: GroceryNavigator by inject()
    private val orderNavigator: OrderNavigator by inject()
    private var orderOngoingAdapter: OrderOnGoingListAdapter? = null
    private var orderCompleteAdapter: OrderHistoryListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_order_ongoing, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        bindViewModel()
        childViewModel.fetchOrderList()
    }

    private fun initView() {
        emptyLayoutView.onClickAction = {
            context?.apply {
                startActivity(groceryNavigator.getGroceryWrapperActivity(this))
            }
        }

        val linearLayoutManager = LinearLayoutManager(context)

        when (OrderType.valueOf(arguments!!.getString(ARG_ORDER_TYPE)!!)) {
            OrderType.ON_GOING -> {
                orderOngoingAdapter = OrderOnGoingListAdapter().apply {
                    onItemSelected = {
                        openTrackingOrderDialog(order = it)
                    }
                    onTrackingClicked = {
                        openTrackingOrderDialog(order = it)
                    }
                }
                orderListRecyclerView?.apply {
                    layoutManager = linearLayoutManager
                    adapter = orderOngoingAdapter
                    isNestedScrollingEnabled = false
                }
            }
            OrderType.COMPLETED -> {
                orderCompleteAdapter =
                    OrderHistoryListAdapter().apply {
                    onItemSelected = {
                        openOrderDetail(it)
                    }
                    onReorderClicked = {
                        openOrderDetail(it)
                    }
                }
                orderListRecyclerView?.apply {
                    layoutManager = linearLayoutManager
                    adapter = orderCompleteAdapter
                    isNestedScrollingEnabled = false
                }
            }
        }

        orderListScrollView.setOnScrollChangeListener(object :
            EndlessProductVerticalListListener(linearLayoutManager) {
            override fun onGoToBottomList() {
                childViewModel.fetchOrderList()
            }
        })

        swipeContainer.setOnRefreshListener {
            childViewModel.reloadOrderList()
        }
    }

    private fun bindViewModel() {
        childViewModel.orderListData.observe(viewLifecycleOwner, Observer {
            if (orderCompleteAdapter != null) {
                orderCompleteAdapter!!.items = it
            } else if (orderOngoingAdapter != null) {
                orderOngoingAdapter!!.items = it
            }
            swipeContainer.isRefreshing = false
        })

        childViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) showLoadingText() else hideBottomText()
        })

        childViewModel.isNoMoreData.observe(viewLifecycleOwner, Observer {
            if (it) showNoMoreDataText() else hideBottomText()
        })

        childViewModel.renderEmptyState.observe(viewLifecycleOwner, Observer { isOnGoing ->
            renderEmptyState(isOnGoing = isOnGoing)
        })

        childViewModel.scrollToTop.observe(viewLifecycleOwner, Observer {
            orderListScrollView.scrollTo(0, 0)
        })
    }

    private fun showLoadingText() {
        loadingTextView.text = context?.getText(R.string.loading)
        loadingTextView.visibility = View.VISIBLE
    }

    private fun hideBottomText() {
        loadingTextView.visibility = View.GONE
    }

    private fun showNoMoreDataText() {
        loadingTextView.text = context?.getText(R.string.no_more_data)
        loadingTextView.visibility = View.VISIBLE
    }

    private fun openTrackingOrderDialog(order: OrderModel) {
        activity?.supportFragmentManager?.let { fragmentManager ->
            (orderNavigator.getTrackingOrderDialog(orderData = order) as TrackingOrderDialog).apply {
                val parentFragment: OrderListFragment? =
                    (this@OrderListChildFragment.parentFragment as OrderListFragment)
                onCancelOrderSuccess = {
                    parentFragment?.refreshOrderListData()
                }

                onOrderCompleted = {
                    parentFragment?.showCompletedOrderFragment()
                    parentFragment?.refreshOrderListData()
                }

            }.show(fragmentManager, TrackingOrderDialog.TAG)
        }
    }

    private fun openOrderDetail(order: OrderModel) {
        activity?.let {
            startActivity(orderNavigator.getOrderDetailActivity(context = it, orderData = order))
        }
    }

    private fun renderEmptyState(isOnGoing: Boolean) {
        orderListRecyclerView.gone()
        emptyLayoutView.apply {
            visible()
            if (isOnGoing) {
                setEmptyTitle(getString(R.string.no_order_found_ongoing))
                setEmptyDetails(getString(R.string.no_order_found_details_ongoing))
            } else {
                setEmptyTitle(getString(R.string.no_order_found_past))
                setEmptyDetails(getString(R.string.no_order_found_details_past))
            }
            setEmptyImage(R.drawable.img_empty_order)
        }
    }

    fun refreshDataList() {
        childViewModel.reloadOrderList()
    }
}
