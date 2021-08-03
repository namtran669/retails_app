package namit.retail_app.order.presentation.order_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import namit.retail_app.core.data.entity.OrderType
import namit.retail_app.core.navigation.OrderNavigator
import namit.retail_app.core.presentation.base.BaseFragment
import namit.retail_app.order.R
import namit.retail_app.order.presentation.adapter.OrderPagerAdapter
import kotlinx.android.synthetic.main.fragment_order_list.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.android.ext.android.inject


@ObsoleteCoroutinesApi
class OrderListFragment : BaseFragment() {
    companion object {
        const val TAG = "OrderListFragment"
        fun getNewInstance(): OrderListFragment = OrderListFragment()
        const val ONGOING_LIST_FRAGMENT_POSITION = 0
        const val COMPLETED_LIST_FRAGMENT_POSITION = 1
    }

    private val orderNavigator: OrderNavigator by inject()
    private lateinit var viewPagerAdapter: OrderPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_order_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
        initView()
    }

    private fun initView() {
        onGoingTabTextView.setOnClickListener {
            showOnGoingOrderFragment()
        }

        completedTabTextView.setOnClickListener {
            showCompletedOrderFragment()
        }
    }

    private fun initViewPager() {
        viewPagerAdapter = OrderPagerAdapter(childFragmentManager)
        viewPagerAdapter.addFrag(orderNavigator.getOrderChildFragment(OrderType.ON_GOING.value))
        viewPagerAdapter.addFrag(orderNavigator.getOrderChildFragment(OrderType.COMPLETED.value))

        orderViewPager.adapter = viewPagerAdapter
        orderViewPager.offscreenPageLimit = viewPagerAdapter.count
        orderViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    ONGOING_LIST_FRAGMENT_POSITION -> {
                        enableOnGoingStatusBg()
                        disablePastStatusBg()
                    }

                    COMPLETED_LIST_FRAGMENT_POSITION -> {
                        enablePastStatusBg()
                        disableOnGoingStatusBg()
                    }
                }
            }

        })
    }

    private fun disableOnGoingStatusBg() {
        context?.let {
            onGoingTabTextView.setBackgroundColor(
                ContextCompat.getColor(
                    it,
                    android.R.color.transparent
                )
            )
            onGoingTabTextView.setTextColor(ContextCompat.getColor(it, R.color.trout70))
        }
    }

    private fun disablePastStatusBg() {
        context?.let {
            completedTabTextView.setBackgroundColor(
                ContextCompat.getColor(
                    it,
                    android.R.color.transparent
                )
            )
            completedTabTextView.setTextColor(ContextCompat.getColor(it, R.color.trout70))
        }
    }

    private fun enableOnGoingStatusBg() {
        context?.let {
            onGoingTabTextView.setBackgroundResource(R.drawable.bg_order_status_selected)
            onGoingTabTextView.setTextColor(ContextCompat.getColor(it, R.color.dodgerBlue))
        }
    }

    private fun enablePastStatusBg() {
        context?.let {
            completedTabTextView.setBackgroundResource(R.drawable.bg_order_status_selected)
            completedTabTextView.setTextColor(ContextCompat.getColor(it, R.color.dodgerBlue))
        }
    }

    fun refreshOrderListData() {
        viewPagerAdapter.refreshDataListAllTab()
    }

    fun showCompletedOrderFragment() {
        orderViewPager.setCurrentItem(COMPLETED_LIST_FRAGMENT_POSITION, true)
    }

    private fun showOnGoingOrderFragment() {
        orderViewPager.setCurrentItem(ONGOING_LIST_FRAGMENT_POSITION, true)
    }
}
