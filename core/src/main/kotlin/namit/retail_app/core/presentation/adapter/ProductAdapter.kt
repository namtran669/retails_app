package namit.retail_app.core.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.extension.*
import kotlinx.android.synthetic.main.item_product.view.*
import kotlin.properties.Delegates

class ProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<ProductItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onSelectProduct: ((ProductItem) -> Unit) = { }
    var addOneMore: ((ProductItem, Int) -> Unit) = { _, _ -> }
    var reduceOne: ((ProductItem, Int) -> Unit) = { _, _ -> }

    var itemWidth = 0
    var itemHeight = 0

    enum class ViewType(val value: Int) {
        SKELETON(0),
        DATA(1),
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == ViewType.SKELETON.value) {
            val view = inflater.inflate(R.layout.item_product_skeleton, parent, false)
            ProductSkeletonViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_product, parent, false)
            ProductsViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].let {
            if (!isSkeletonData(it)) {
                (holder as? ProductsViewHolder)?.bind(position)
            }
        }
    }

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

    inner class ProductsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.productCardView.apply {
                layoutParams.width = itemWidth
                layoutParams.height = itemHeight
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSelectProduct.invoke(items[adapterPosition])
                }
            }

            itemView.addOneItemImageView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    addOneMore.invoke(items[adapterPosition], adapterPosition)
                }
            }

            itemView.reduceOneItemImageView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    reduceOne.invoke(items[adapterPosition], adapterPosition)
                }
            }
        }

        fun bind(position: Int) {
            itemView.apply {

                val product = items[position]

                product.thumbnailUrl?.let {
                    productImageView.loadImage(
                        imageUrl = it,
                        placeHolder = R.color.altoGray
                    )
                }
                nameProductTextView.text = product.name
                discountPriceTextView.text =
                    product.retailPriceWithTax?.formatCurrency()?.toThaiCurrency()

                nameProductTextView.text = product.name
                discountPriceTextView.text =
                    product.retailPriceWithTax?.formatCurrency()?.toThaiCurrency()
                sizeOrderProductTextView.text = product.quantityOrder.toString()
                discountPercentTextView.text

                if (product.quantityInStock <= 0) {
                    showOutOfStockState()
                } else {
                    showAvailableState()
                    if (product.quantityOrder > 0) {
                        reduceOneItemImageView.enable()
                    } else {
                        reduceOneItemImageView.disable()
                    }
                }
            }
        }


        private fun showOutOfStockState() {
            itemView.apply {
                sizeOrderProductTextView.apply {
                    text = context.getText(R.string.out_of_stock)
                    setTextColor(ContextCompat.getColor(context, R.color.frenchGray))
                }
                discountPercentTextView.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_text_discount_percent_disable)
                reduceOneItemImageView.gone()
                addOneItemImageView.gone()
            }
        }

        private fun showAvailableState() {
            itemView.apply {
                discountPercentTextView.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_text_discount_percent)
                reduceOneItemImageView.visible()
                addOneItemImageView.visible()
            }
        }
    }

    inner class ProductSkeletonViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.productCardView.apply {
                layoutParams.width = itemWidth
                layoutParams.height = itemHeight
            }
        }
    }
}
