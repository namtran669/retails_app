package namit.retail_app.order.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.data.entity.OrderProduct
import namit.retail_app.core.enums.OrderStatus
import namit.retail_app.core.extension.*
import namit.retail_app.order.R
import namit.retail_app.order.presentation.adapter.OrderDetailProductItemAdapter
import namit.retail_app.order.utils.OrderUtils
import namit.retail_app.payment.data.PaymentMethodModel
import namit.retail_app.payment.enums.CardType
import namit.retail_app.core.enums.PaymentType
import kotlinx.android.synthetic.main.view_order_detail.view.*

class OrderDetailView constructor(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    var productAdapter: OrderDetailProductItemAdapter

    init {
        LayoutInflater.from(context).inflate(R.layout.view_order_detail, this, true)
        setBackgroundColor(Color.TRANSPARENT)

        productAdapter = OrderDetailProductItemAdapter()
        productItemRecyclerView.adapter = productAdapter
    }

    companion object {
        val TAG: String = OrderTrackingStatusView::class.java.simpleName
    }

    fun setOrderDetailData(data: OrderModel) {
        setOrderStatus(data.currentOrderStatus)
        setProductList(data.orderProduct)
        setOrderNumber(data.secureKey)
        data.orderStoreInfo?.storeName?.let { setNameOrderFrom(it) }
        data.createdAt?.let { setOrderTime(it) }
        data.orderAddress?.let { setDeliveryAddress(it) }
        setSubTotal(data.total ?: 0.0)
        setTotal(data.orderPayment?.paymentAmount ?: 0.0)
        setDeliveryFee(data.deliveryFee ?: 0.0)
        setDeliveryTime(data.pickupAt ?: "")
        setPromoDiscount(data.orderPayment?.discount ?: 0.0)
        data.orderPayment?.let { setPaymentMethod(OrderUtils.convertToPaymentMethodModel(it)) }
        if (data.currentOrderStatus == OrderStatus.COMPLETED) {
            downloadInvoiceTextView.visible()
        } else {
            downloadInvoiceTextView.invisible()
        }
    }

    private fun setNameOrderFrom(name: String) {
        orderFromNameTextView.text = name
    }

    @SuppressLint("SetTextI18n")
    private fun setOrderTime(time: String) {
        orderTimeTextView.text =
            time.convertToDateFromUTCTime(
                DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM_SS,
                DATE_TIME_FORMAT_DD_MMM_YYYY_HH_MM
            )
    }

    private fun setOrderNumber(code: String) {
        orderNumDetailTextView.text = code
    }

    private fun setDeliveryAddress(address: String) {
        deliveryDetailTextView.text = address
    }

    private fun setPromoDiscount(value: Double) {
        promoDiscountTextView.text = "-฿".plus(value.formatCurrency())
    }

    private fun setSubTotal(value: Double) {
        subtotalTextView.text = value.formatCurrency().toThaiCurrency()
    }

    private fun setDeliveryFee(value: Double) {
        deliveryFeeDetailTextView.text = value.formatCurrency().toThaiCurrency()
    }

    private fun setTotal(value: Double) {
        totalSumTextView.text = value.formatCurrency().toThaiCurrency()
    }

    private fun setProductList(productList: List<OrderProduct>) {
        productAdapter.items = productList
    }

    private fun setDeliveryTime(time: String) {
        deliveryTimeTextView.text = time.toOrderDeliveryTime()
    }

    private fun setPaymentMethod(paymentMethod: PaymentMethodModel) {
        if (paymentMethod.type == PaymentType.CASH) {
            paymentTypeTextView.text = context.getString(R.string.cash_on_delivery)
        } else {
            paymentTypeTextView.text = paymentMethod.title
        }
        when (paymentMethod.type) {
            PaymentType.TRUE_MONEY -> {
                R.drawable.ic_payment_true_money
            }
            PaymentType.CASH -> {
                R.drawable.ic_payment_cash
            }
            else -> {
                when (paymentMethod.cardType) {
                    CardType.VISA -> {
                        R.drawable.ic_visa_credit_card
                    }
                    CardType.MASTER_CARD -> {
                        R.drawable.ic_master_credit_card
                    }
                    CardType.JCB -> {
                        R.drawable.ic_jcb_credit_card
                    }
                    else -> {
                        null
                    }
                }
            }
        }?.let {
            paymentImageView.setImageDrawable(ContextCompat.getDrawable(context, it))
        }
    }

    private fun setOrderStatus(status: OrderStatus) {
        when (status) {
            OrderStatus.COMPLETED -> {
                R.drawable.ic_order_checked
            }
            OrderStatus.CANCELLED -> {
                R.drawable.ic_order_cancel
            }
            else -> {
                null
            }
        }?.let {
            iconStatusImageView.visible()
            iconStatusImageView.setImageResource(it)
        } ?: kotlin.run {
            iconStatusImageView.gone()
        }
    }
}
