package namit.retail_app.core.presentation.product_category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.extension.loadCircleImage
import kotlinx.android.synthetic.main.item_product_category.view.*
import kotlin.properties.Delegates

class CategoryMerchantAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<CategoryItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private var actionListener: OnActionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_product_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CategoryViewHolder).bind(data = items[position])
    }

    fun setActionListener(action: OnActionListener) {
        this.actionListener = action
    }

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(data: CategoryItem) {
            itemView.setOnClickListener {
                actionListener?.onItemSelect(data)
            }

            data.iconUrl?.let {
                itemView.productCategoryImageView.loadCircleImage(it)
            }

            itemView.productCategoryNameTextView.text = data.name

        }
    }

    //todo change to base listener
    interface OnActionListener {
        fun onItemSelect(category: CategoryItem)
    }

}