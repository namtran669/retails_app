package namit.retail_app.grocery.presentation.category_sub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.grocery.R
import kotlinx.android.synthetic.main.item_sub_category.view.*
import kotlin.properties.Delegates

class GrocerySubCategoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<CategoryItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onItemSelect: (category: CategoryItem) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_sub_category, parent, false)
        return SubCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SubCategoryViewHolder).bind(items[position])
    }

    inner class SubCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemSelect(items[adapterPosition])
                }
            }
        }

        fun bind(data: CategoryItem) {
            itemView.subCategoryTextView.text = data.name

            itemView.amountProductTextView.text =
                itemView.resources.getString(R.string.amount_content, data.productCount)
        }

    }
}