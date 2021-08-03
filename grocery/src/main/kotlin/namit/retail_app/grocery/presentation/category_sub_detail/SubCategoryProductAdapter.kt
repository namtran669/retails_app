package namit.retail_app.grocery.presentation.category_sub_detail

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.extension.*
import namit.retail_app.grocery.R
import kotlinx.android.synthetic.main.item_horizontal_product_sub_category.view.*
import kotlin.properties.Delegates

class SubCategoryProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<ProductItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onSelectProduct: ((ProductItem) -> Unit) = { }
    var addOneMore: ((ProductItem, Int) -> Unit) = { _, _ -> }
    var reduceOne: ((ProductItem, Int) -> Unit) = { _, _ -> }

    enum class ViewType(val value: Int) {
        SKELETON(0),
        DATA(1),
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == ViewType.SKELETON.value) {
            val view =
                layoutInflater.inflate(R.layout.item_horizontal_product_skeleton, parent, false)
            ProductSkeletonViewHolder(view)
        } else {
            val view =
                layoutInflater.inflate(R.layout.item_horizontal_product_sub_category, parent, false)
            HorizontalProductItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return items.size
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].let {
            if (!isSkeletonData(it)) {
                (holder as HorizontalProductItemViewHolder).bindData(it)
            }
        }
    }

    inner class HorizontalProductItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
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

        fun bindData(data: ProductItem) {
            itemView.apply {
                setNameProduct(data)

                data.thumbnailUrl?.let {
                    setImageProduct(it)
                }
                data.retailPriceWithTax?.let {
                    setDiscountPrice(it)
                }

                data.quantityOrder.let {
                    sizeOrderProductTextView.text = it.toString()
                    reduceOneItemImageView.enable()
                    if (it <= 0) {
                        reduceOneItemImageView.disable()
                    } else {
                        reduceOneItemImageView.enable()
                    }
                }
            }
        }

        private fun setImageProduct(url: String?) {
            itemView.productImageView.loadImage(imageUrl = url, placeHolder = R.color.altoGray)
        }

        private fun setActualPrice(price: Double?) {
            itemView.actualPriceTextView.apply {
                visibility = View.VISIBLE
                text = price?.formatCurrency()?.toThaiCurrency()
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
            val renderName: String = data.name
            itemView.productNameTextView.apply {
                visibility = View.VISIBLE
                text = renderName
            }
        }

        private fun setDiscountPercentage(percent: Float) {
            itemView.discountPercentTextView.apply {
                visibility = View.VISIBLE
                text = (percent * -1).run { toString().plus("%") }
            }
        }

        private fun setPriceDetail(detail: String) {
            itemView.priceDetailTextView.apply {
                visibility = View.VISIBLE
                text = detail
            }
        }

    }

    inner class ProductSkeletonViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}
