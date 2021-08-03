package namit.retail_app.order.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.enums.OrderStatus
import namit.retail_app.core.extension.*
import namit.retail_app.order.R
import kotlinx.android.synthetic.main.item_order_ongoing.view.*
import kotlin.properties.Delegates

class OrderOnGoingListAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<OrderModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onItemSelected: (OrderModel) -> Unit = {}
    var onTrackingClicked: (OrderModel) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == ViewType.SKELETON.value) {
            val view = inflater.inflate(R.layout.item_order_loading, parent, false)
            OrderSkeletonViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_order_ongoing, parent, false)
            OrderOngoingViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSkeletonData(items[position])) {
            ViewType.SKELETON.value
        } else {
            ViewType.DATA.value
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun isSkeletonData(data: OrderModel): Boolean {
        return data.secureKey.isBlank()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].let {
            if (!isSkeletonData(it)) {
                (holder as? OrderOngoingViewHolder)?.bind(it)
            }
        }
    }

    inner class OrderSkeletonViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    inner class OrderOngoingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setSafeOnClickListener {
                onItemSelected.invoke(items[adapterPosition])
            }

            itemView.trackingTextView.setSafeOnClickListener {
                onTrackingClicked.invoke(items[adapterPosition])
            }
        }

        fun bind(order: OrderModel) {
            val adapter = ProductOrderListAdapter()
            adapter.items = order.orderProduct
            itemView.apply {
                var status = ""
                var iconStatus = 0
                var background = 0
                when (order.currentOrderStatus) {
                    OrderStatus.PENDING -> {
                        status = context.getString(R.string.pending_payment)
                        iconStatus = R.drawable.ic_order_pending
                        background = R.drawable.bg_circle_golden
                    }
                    OrderStatus.CONFIRMED -> {
                        status = context.getString(R.string.order_confirmed_upper_case)
                        iconStatus = R.drawable.ic_order_confirm_white
                        background = R.drawable.bg_circle_dodgerblue
                    }
                    OrderStatus.IN_PROGRESS -> {
                        status = context.getString(R.string.order_prepared)
                        iconStatus = R.drawable.ic_order_prepare_white
                        background = R.drawable.bg_circle_dodgerblue
                    }
                    OrderStatus.READY_TO_SHIP -> {
                        status = context.getString(R.string.order_prepared)
                        iconStatus = R.drawable.ic_order_prepare_white
                        background = R.drawable.bg_circle_dodgerblue
                    }
                    OrderStatus.SHIPPING -> {
                        status = context.getString(R.string.order_is_on_the_way)
                        iconStatus = R.drawable.ic_order_deliver_white
                        background = R.drawable.bg_circle_dodgerblue
                    }
                    OrderStatus.COMPLETED -> {
                        status = context.getString(R.string.order_completed)
                        iconStatus = R.drawable.ic_order_success_white
                        background = R.drawable.bg_circle_dodgerblue
                    }
                    OrderStatus.CANCELLED -> {
                        status = context.getString(R.string.order_cancelled)
                        iconStatus = R.drawable.ic_close_white
                        background = R.drawable.bg_circle_sunsetorange
                    }
                    else -> {
                    }
                }

                merchantNameTextView.text = order.orderStoreInfo?.storeName
                arriveTimeTextView.text = status
                quantityProductTextView.text =
                    order.orderProduct.size.toString().plus(context.getString(R.string.items))
                locationPickupTextView.text = order.orderAddress
                totalPriceOrderTextView.text = order.orderPayment?.paymentAmount?.formatCurrency()?.toThaiCurrency()
                orderImageView.loadImage(order.orderStoreInfo?.cover)
                iconStatusImageView.setImageResource(iconStatus)
                iconStatusImageView.setBackgroundResource(background)
                timePickupTextView.text = order.pickupAt?.toOrderDeliveryTime()
            }
        }
    }
}

enum class ViewType(val value: Int) {
    SKELETON(0),
    DATA(1),
}