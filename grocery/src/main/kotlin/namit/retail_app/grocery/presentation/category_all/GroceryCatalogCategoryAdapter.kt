package namit.retail_app.grocery.presentation.category_all

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.extension.loadCircleImage
import namit.retail_app.grocery.R
import kotlinx.android.synthetic.main.item_catalog_category.view.*
import kotlin.properties.Delegates

class GroceryCatalogCategoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<CategoryItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onItemSelect: (category: CategoryItem) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_catalog_category, parent, false)
        return CatalogCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CatalogCategoryViewHolder).bind(items[position])
    }

    inner class CatalogCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemSelect(items[adapterPosition])
                }
            }
        }

        fun bind(data: CategoryItem) {
            itemView.catalogCategoryNameTextView.text = data.name
            itemView.catalogCategoryCountTextView.text =
                itemView.resources.getString(R.string.amount_content, data.productCount)
            data.iconUrl?.let { itemView.catalogCategoryImageView.loadCircleImage(it) }

        }
    }

}