package namit.retail_app.cart.presentation.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.cart.R
import namit.retail_app.core.data.entity.CartMerchantModel
import namit.retail_app.core.extension.formatCurrency
import namit.retail_app.core.extension.toThaiCurrency
import kotlinx.android.synthetic.main.item_shelf_cart_merchant.view.*
import kotlin.properties.Delegates

class CartMerchantAdapter :
    RecyclerView.Adapter<CartMerchantAdapter.ShelfCartMerchantViewHolder>() {
    var items by Delegates.observable(listOf<CartMerchantModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onCheckItemChange: (index: Int) -> Unit = {}
    var onClickAddItem: (index: Int) -> Unit = {}
    var onAddOneItemChild: (parentIndex: Int, childIndex: Int) -> Unit = { _: Int, _: Int -> }
    var onReduceOneItemChild: (parentIndex: Int, childIndex: Int) -> Unit = { _: Int, _: Int -> }
    var onDeleteItemChild: (parentIndex: Int, childIndex: Int) -> Unit = { _: Int, _: Int -> }
    var onSwipeItemChild: (parentIndex: Int, childIndex: Int, isExpanded: Boolean) -> Unit = { _: Int, _: Int, _: Boolean -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelfCartMerchantViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_shelf_cart_merchant, parent, false)
        return ShelfCartMerchantViewHolder(view = view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ShelfCartMerchantViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    inner class ShelfCartMerchantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var contentAdapter = CartProductAdapter()

        init {
            itemView.apply {
                cartProductRecyclerView.isNestedScrollingEnabled = false
                cartProductRecyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                cartProductRecyclerView.adapter = contentAdapter.apply {
                    onAddOneItem = { parentIndex: Int, itemIndex: Int ->
                        onAddOneItemChild.invoke(parentIndex, itemIndex)
                    }

                    onReduceOneItem = { parentIndex: Int, itemIndex: Int ->
                        onReduceOneItemChild.invoke(parentIndex, itemIndex)
                    }

                    onDeleteItem = { parentIndex: Int, itemIndex: Int ->
                        onDeleteItemChild.invoke(parentIndex, itemIndex)
                    }
                    onSwipeItem = { parentIndex: Int, childIndex: Int, isExpanded: Boolean ->
                        onSwipeItemChild.invoke(parentIndex, childIndex, isExpanded)
                    }
                }
            }

            itemView.selectMerchantCheckbox.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onCheckItemChange.invoke(adapterPosition)
                }
            }

            itemView.addItemTextView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClickAddItem(adapterPosition)
                }
            }
        }

        fun bindData(data: CartMerchantModel) {
            itemView.apply {
                contentAdapter.apply {
                    items = data.products
                    parentIndex = adapterPosition
                }
                data.merchant?.let {
                    itemView.merchantNameTextView.text = it.title
                }
                setDeliveryFee(data.deliveryFee)
                if (data.isSelected) {
                    selectMerchantCheckbox.setImageResource(R.drawable.ic_checkbox_tick_checked)
                } else {
                    selectMerchantCheckbox.setImageResource(R.drawable.ic_checkbox_tick_available)
                }
            }
        }

        private fun setDeliveryFee(price: Double) {
            itemView.deliveryFeeTextView.apply {
                visibility = View.VISIBLE
                text = price.formatCurrency().toThaiCurrency()
            }
        }

    }

}