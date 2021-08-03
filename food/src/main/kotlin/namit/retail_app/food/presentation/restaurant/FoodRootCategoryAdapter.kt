package namit.retail_app.food.presentation.restaurant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.food.R
import kotlinx.android.synthetic.main.item_food_root_category.view.*
import kotlin.properties.Delegates

class FoodRootCategoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<CategoryItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onSelectItem: (index: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_food_root_category, parent, false)
        return FoodCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FoodCategoryViewHolder).bind(items[position])
    }

    inner class FoodCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSelectItem(adapterPosition)
                }
            }
        }

        fun bind(data: CategoryItem) {
            itemView.apply {
                foodCategoryTextView.text = data.name

                if (data.isSelected) {
                    foodCategoryTextView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.trout
                        )
                    )
                } else {
                    foodCategoryTextView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.trout50
                        )
                    )
                }
            }
        }
    }

}