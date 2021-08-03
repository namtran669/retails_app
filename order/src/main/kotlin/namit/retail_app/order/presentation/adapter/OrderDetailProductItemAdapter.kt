package namit.retail_app.order.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.OrderProduct
import namit.retail_app.core.extension.formatCurrency
import namit.retail_app.core.extension.toThaiCurrency
import namit.retail_app.order.R
import kotlinx.android.synthetic.main.item_product_order_detail.view.*
import kotlin.properties.Delegates

class OrderDetailProductItemAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<OrderProduct>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_order_detail, parent, false)
        return OrderDetailProductItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as OrderDetailProductItemViewHolder).bind(items[position])

    }

    inner class OrderDetailProductItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(product: OrderProduct) {
            product.title?.let { setProductName(it) }
            product.quantity?.let { setQuantity(it) }
            product.finalPrice?.let { setPrice(it) }
        }

        @SuppressLint("SetTextI18n")
        private fun setQuantity(value: Int) {
            itemView.promoNoTextView.text = "${value}x"
        }

        private fun setProductName(name: String) {
            itemView.promoNameTextView.text = name
        }

        private fun setPrice(value: Double) {
            itemView.priceTextView.text = value.formatCurrency().toThaiCurrency()
        }
    }
}