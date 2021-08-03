package namit.retail_app.app.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.navigation.OrderNavigator
import namit.retail_app.order.presentation.order_detail.OrderDetailActivity
import namit.retail_app.order.presentation.order_list.OrderListChildFragment
import namit.retail_app.order.presentation.tracking.TrackingOrderDialog

class OrderNavigatorImpl: OrderNavigator {

    override fun getOrderDetailActivity(context: Context, orderData: OrderModel): Intent =
        OrderDetailActivity.getStartIntent(context = context, orderData = orderData)

    override fun getTrackingOrderDialog(orderData: OrderModel): DialogFragment {
        return TrackingOrderDialog.newInstance(orderData = orderData)
    }

    override fun getOrderChildFragment(orderType: String): Fragment {
        return OrderListChildFragment.getNewInstance(orderType = orderType)
    }

}