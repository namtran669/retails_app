package namit.retail_app.order.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.OrderModel
import namit.retail_app.core.enums.OrderStatus
import namit.retail_app.core.extension.*
import namit.retail_app.order.R
import kotlinx.android.synthetic.main.item_order_history.view.*
import kotlin.properties.Delegates

class OrderHistoryListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<OrderModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onItemSelected: (OrderModel) -> Unit = {}
    var onReorderClicked: (OrderModel) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == ViewType.SKELETON.value) {
            val view = inflater.inflate(R.layout.item_order_loading, parent, false)
            OrderSkeletonViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_order_history, parent, false)
            OrderHistoryViewHolder(view)
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
                (holder as? OrderHistoryViewHolder)?.bind(it)
            }
        }
    }

    inner class OrderSkeletonViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    inner class OrderHistoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setSafeOnClickListener {
                onItemSelected.invoke(items[adapterPosition])
            }

            itemView.reOrderTextView.setSafeOnClickListener {
                onReorderClicked.invoke(items[adapterPosition])
            }
        }

        fun bind(order: OrderModel) {
            val adapter = ProductOrderListAdapter()
            adapter.items = order.orderProduct
            itemView.apply {
                var iconStatus = 0
                var status = ""
                when (order.currentOrderStatus) {

                    OrderStatus.COMPLETED -> {
                        status = context.getString(R.string.order_completed)
                        iconStatus = R.drawable.ic_order_checked
                    }
                    OrderStatus.CANCELLED -> {
                        status = context.getString(R.string.order_cancelled)
                        iconStatus = R.drawable.ic_order_cancel
                    }
                    else -> {
                    }
                }
                merchantNameTextView.text = order.orderStoreInfo?.storeName
                arriveTimeTextView.text = status
                quantityProductTextView.text =
                    order.orderProduct.size.toString().plus(context.getString(R.string.items))
                locationPickupTextView.text = order.orderAddress
                totalPriceOrderTextView.text =
                    order.orderPayment?.paymentAmount?.formatCurrency()?.toThaiCurrency()
                orderImageView.loadImage(order.orderStoreInfo?.cover)
                iconStatusImageView.setImageResource(iconStatus)
                timePickupTextView.text = order.pickupAt?.toOrderDeliveryTime()
                
                //Todo: disable reorder button as PO requested
                reOrderTextView.gone()
            }
        }
    }
}
