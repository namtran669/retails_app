package namit.retail_app.order.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.OrderProduct
import namit.retail_app.order.R
import kotlinx.android.synthetic.main.item_product_order.view.*
import kotlin.properties.Delegates

class ProductOrderListAdapter: RecyclerView.Adapter<ProductOrderListAdapter.OrderOngoingViewHolder>() {

    var items by Delegates.observable(listOf<OrderProduct>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderOngoingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_order, parent, false)
        return OrderOngoingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: OrderOngoingViewHolder, position: Int) {
        holder.bind(items.get(position))
    }

    inner class OrderOngoingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(product: OrderProduct) {
            itemView.orderNoTextView.text = product.quantity.toString()
            itemView.orderNameTextView.text = product.title
        }
    }
}
