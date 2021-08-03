package namit.retail_app.food.presentation.restaurant.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.extension.formatCurrency
import namit.retail_app.core.extension.loadImage
import namit.retail_app.core.extension.toThaiCurrency
import namit.retail_app.food.R
import kotlinx.android.synthetic.main.item_food.view.*
import kotlin.properties.Delegates

class FoodAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<ProductItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onSelectItem: (data: ProductItem) -> Unit = {}

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
            val view = layoutInflater.inflate(R.layout.item_food, parent, false)
            FoodCategoryViewHolder(view)
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
                (holder as? FoodCategoryViewHolder)?.bind(it)
            }
        }
    }

    inner class ProductSkeletonViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    inner class FoodCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSelectItem(items[adapterPosition])
                }
            }
        }

        fun bind(data: ProductItem) {
            itemView.apply {
                foodTitleTextView.text = data.name
                foodDescriptionTextView.text = data.description

                data.retailPriceWithTax?.let {
                    priceTextView.text = it.formatCurrency().toThaiCurrency()
                }

                foodImageView.loadImage(
                    imageUrl = data.thumbnailUrl,
                    placeHolder = R.color.altoGray
                )
            }
        }
    }

}