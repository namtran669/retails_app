package namit.retail_app.cart.presentation.detail

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.cart.R
import namit.retail_app.core.data.entity.CartProductModel
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.extension.formatCurrency
import namit.retail_app.core.extension.loadImage
import namit.retail_app.core.extension.setSafeOnClickListener
import namit.retail_app.core.extension.toThaiCurrency
import namit.retail_app.core.utils.CartUtils
import namit.retail_app.core.utils.CartUtils.INTERVAL_TIME_ADD_TO_CART
import io.sulek.ssml.OnSwipeListener
import kotlinx.android.synthetic.main.item_cart_merchant_content.view.*
import kotlin.properties.Delegates

class CartProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(mutableListOf<CartProductModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var parentIndex = 0
    var onAddOneItem: (parentIndex: Int, childIndex: Int) -> Unit = { _: Int, _: Int -> }
    var onReduceOneItem: (parentIndex: Int, childIndex: Int) -> Unit = { _: Int, _: Int -> }
    var onDeleteItem: (parentIndex: Int, childIndex: Int) -> Unit = { _: Int, _: Int -> }
    var onSwipeItem: (parentIndex: Int, childIndex: Int, isExpanded: Boolean) -> Unit =
        { _: Int, _: Int, _: Boolean -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_cart_merchant_content, parent, false)
        return CartMerchantItemViewHolder(view = view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CartMerchantItemViewHolder).bindData(items[position])
    }

    inner class CartMerchantItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.addOneItemImageView.setSafeOnClickListener(intervalTime = INTERVAL_TIME_ADD_TO_CART) {
                onAddOneItem.invoke(parentIndex, adapterPosition)
            }

            itemView.reduceOneItemImageView.setSafeOnClickListener(intervalTime = INTERVAL_TIME_ADD_TO_CART) {
                onReduceOneItem.invoke(parentIndex, adapterPosition)
            }

            itemView.backgroundContainer.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && items[adapterPosition].isSwiped) {
                    deleteItem(adapterPosition)
                }
            }

            itemView.binImageView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && items[adapterPosition].isSwiped) {
                    deleteItem(adapterPosition)
                }
            }

            itemView.cartItemSwipeContainer.setOnSwipeListener(object : OnSwipeListener {
                override fun onSwipe(isExpanded: Boolean) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onSwipeItem(parentIndex, adapterPosition, isExpanded)
                        itemView.backgroundContainer.apply {
                            isClickable = isExpanded
                            isFocusable = isExpanded
                            isFocusableInTouchMode = isExpanded
                        }
                    }
                }
            })
        }

        private fun deleteItem(index: Int) {
            onDeleteItem.invoke(parentIndex, index)
        }

        fun bindData(data: CartProductModel) {
            data.product?.apply {
                setNameProduct(this)
                setImageProduct(thumbnailUrl)
                retailPriceWithTax?.let {
                    setDiscountPrice(CartUtils.getTotalPriceProduct(this) * data.quantity)
                    setPriceDetail(it)
                }
            }
            setQuantity(data.quantity)

            itemView.cartItemSwipeContainer.apply(data.isSwiped)
            itemView.backgroundContainer.apply {
                isClickable = data.isSwiped
                isFocusable = data.isSwiped
                isFocusableInTouchMode = data.isSwiped
            }
        }

        private fun setImageProduct(url: String?) {
            itemView.productImageView.loadImage(url)
        }

        private fun setActualPrice(price: Double) {
            itemView.actualPriceTextView.apply {
                visibility = View.VISIBLE
                text = price.formatCurrency().toThaiCurrency()
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        private fun setDiscountPrice(price: Double) {
            itemView.discountPriceTextView.apply {
                visibility = View.VISIBLE
                text = price.formatCurrency().toThaiCurrency()
            }
        }

        private fun setNameProduct(data: ProductItem) {
            itemView.productNameTextView.apply {
                visibility = View.VISIBLE
                text = data.name
            }
        }

        private fun setDiscountPercentage(percent: Float) {
            itemView.discountPercentTextView.apply {
                visibility = View.VISIBLE
                text = (percent * -1).run { toString().plus("%") }
            }
        }

        private fun setDescription(description: String) {
            itemView.detailsProductTextView.apply {
                visibility = View.VISIBLE
                text = description
            }
        }

        private fun setPriceDetail(price: Double) {
            itemView.priceDetailTextView.apply {
                visibility = View.VISIBLE
                text = "${price.formatCurrency().toThaiCurrency()}/Pack"
            }
        }

        private fun setQuantity(amount: Int) {
            itemView.apply {
                sizeOrderProductTextView.text = amount.toString()
            }
        }
    }

}
