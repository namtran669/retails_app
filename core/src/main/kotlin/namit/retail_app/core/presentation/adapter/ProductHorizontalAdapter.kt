package namit.retail_app.core.presentation.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.extension.*
import kotlinx.android.synthetic.main.item_horizontal_product.view.*
import kotlinx.android.synthetic.main.item_horizontal_product.view.actualPriceTextView
import kotlinx.android.synthetic.main.item_horizontal_product.view.discountPercentTextView
import kotlinx.android.synthetic.main.item_horizontal_product.view.discountPriceTextView
import kotlinx.android.synthetic.main.item_horizontal_product.view.productImageView
import kotlinx.android.synthetic.main.item_product.view.*
import kotlin.properties.Delegates


class ProductHorizontalAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<ProductItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    enum class ViewType(val value: Int) {
        SKELETON(0),
        DATA(1),
    }

    var onSelectItem: (chooseModel: ProductItem) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == ViewType.SKELETON.value) {
            val view =
                layoutInflater.inflate(R.layout.item_horizontal_product_skeleton, parent, false)
            ProductSkeletonViewHolder(view = view)
        } else {
            val view = layoutInflater.inflate(R.layout.item_horizontal_product, parent, false)
            ProductViewHolder(view = view)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return if (isSkeletonData(items[position])) {
            ViewType.SKELETON.value
        } else {
            ViewType.DATA.value
        }
    }

    private fun isSkeletonData(data: ProductItem): Boolean {
        return data.id < 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].let {
            if (!isSkeletonData(it)) {
                (holder as ProductViewHolder).bind(it)
            }
        }
    }

    inner class ProductSkeletonViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSelectItem.invoke(items[adapterPosition])
                }
            }
        }

        fun bind(product: ProductItem) {
            itemView.apply {
                setProductName(product.name)
                if (product.showDiscount) {
                    product.discountPriceWithTax?.let { discountPrice ->
                        setDiscountPrice(discountPrice)
                    }
                    product.retailPriceWithTax?.let { retailPrice ->
                        setActualPrice(retailPrice)
                    }
                    product.discountPercentage?.let { discountPercent ->
                        setDiscountPercentage(discountPercent)
                    }
                } else {
                    product.retailPriceWithTax?.let {
                        setDiscountPrice(it)
                    }
                    itemView.actualPriceTextView.gone()
                    itemView.discountPercentTextView.gone()
                }
                product.thumbnailUrl?.let {
                    productImageView.loadImage(it)
                }

                if (product.quantityInStock <= 1000) {
                    showOutOfStockState()
                } else {
                    showAvailableState()
                    if (product.quantityOrder > 0) {
                        reduceOneItemImageView?.enable()
                    } else {
                        reduceOneItemImageView?.disable()
                    }
                }
            }
        }

        private fun setActualPrice(price: Double?) {
            itemView.actualPriceTextView.apply {
                visibility = View.VISIBLE
                text = price?.formatCurrency()?.toThaiCurrency()
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        private fun setDiscountPrice(price: Double?) {
            itemView.discountPriceTextView.apply {
                visibility = View.VISIBLE
                text = price?.formatCurrency()?.toThaiCurrency()

            }
        }

        private fun setProductName(name: String?) {
            itemView.productNameTextView.apply {
                visibility = View.VISIBLE
                text = name ?: ""
            }
        }

        private fun setDiscountPercentage(percent: Float) {
            itemView.discountPercentTextView.apply {
                visibility = View.VISIBLE
                text = (percent.times(-1)).run { toString().plus("%") }
            }
        }

        private fun showOutOfStockState() {
            itemView.apply {
                context?.let { context ->
                    sizeOrderProductTextView?.text = context.getText(R.string.out_of_stock)
                    sizeOrderProductTextView?.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.frenchGray
                        )
                    )
                    discountPercentTextView?.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.bg_text_discount_percent_disable
                        )
                }
                reduceOneItemImageView?.gone()
                addOneItemImageView?.gone()
            }
        }

        private fun showAvailableState() {
            itemView.apply {
                context?.let { context ->
                    sizeOrderProductTextView?.text = context.getText(0)
                    sizeOrderProductTextView?.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.trout
                        )
                    )
                    discountPercentTextView?.background =
                        ContextCompat.getDrawable(context, R.drawable.bg_text_discount_percent)
                }
                reduceOneItemImageView?.visible()
                addOneItemImageView?.visible()
            }
        }
    }
}