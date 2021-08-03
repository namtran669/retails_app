package namit.retail_app.order.presentation.order_detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.presentation.base.BaseActivity
import namit.retail_app.order.R
import kotlinx.android.synthetic.main.activity_order_detail.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class OrderDetailActivity : BaseActivity() {

    companion object {
        private const val INTENT_ORDER_DETAIL = "INTENT_ORDER_DETAIL"

        fun getStartIntent(context: Context, orderData: OrderModel): Intent =
            Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(INTENT_ORDER_DETAIL, orderData)
            }
    }

    private val viewModel: OrderDetailViewModel by viewModel(parameters = {
        parametersOf(intent.getParcelableExtra(INTENT_ORDER_DETAIL))
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        initView()
        bindViewModel()
    }

    private fun initView() {
        orderDetailsimpleToolbar.onBackPressed = {
            finish()
        }
    }

    private fun bindViewModel() {
        viewModel.orderDetail.observe(this, Observer {
            orderDetailView.setOrderDetailData(it)
        })
    }

}
