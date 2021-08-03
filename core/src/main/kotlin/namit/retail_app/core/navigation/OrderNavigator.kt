package namit.retail_app.core.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import namit.retail_app.core.data.entity.OrderModel

interface OrderNavigator {
    fun getOrderDetailActivity(context: Context, orderData: OrderModel): Intent
    fun getTrackingOrderDialog(orderData: OrderModel) : DialogFragment
    fun getOrderChildFragment(orderType: String): Fragment
}