package namit.retail_app.grocery.presentation.category_all

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.grocery.R
import namit.retail_app.grocery.data.entity.FeatureCategory
import kotlinx.android.synthetic.main.item_feature_category.view.*
import kotlin.properties.Delegates

class GroceryFeatureCategoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<FeatureCategory>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private var actionListener: OnActionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_feature_category, parent, false)
        return FeatureCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FeatureCategoryViewHolder).bind(items[position])
    }

    inner class FeatureCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    actionListener?.onItemSelect(items[adapterPosition])
                    for ((index, value) in items.withIndex()) {
                        if (value.isSelected) {
                            value.isSelected = false
                            notifyItemChanged(index)
                            break
                        }
                    }
                    val selectedCategory = items[adapterPosition]
                    selectedCategory.isSelected = true
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        fun bind(data: FeatureCategory) {
            if (data.title == null) {
                itemView.featureCategoryNameTextView.text =
                    itemView.resources.getString(R.string.all)
                itemView.featureCategoryCountTextView.visibility = View.INVISIBLE
                if (data.isSelected) {
                    itemView.featureCategoryNameTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.dodgerBlue
                        )
                    )
                    itemView.featureCategoryIconBackground.setBackgroundResource(R.drawable.bg_circle_dodgerblue)
                    itemView.featureCategoryImageView.setImageResource(R.drawable.ic_ticket_white)
                } else {
                    itemView.featureCategoryNameTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.trout
                        )
                    )
                    itemView.featureCategoryIconBackground.setBackgroundResource(R.drawable.bg_circle_pattensblue_image)
                    itemView.featureCategoryImageView.setImageResource(R.drawable.ic_ticket_blue)
                }
            } else {
                itemView.featureCategoryNameTextView.text = data.title
                itemView.featureCategoryCountTextView.visibility = View.VISIBLE
                itemView.featureCategoryCountTextView.text =
                    itemView.resources.getString(R.string.amount_content, data.count)

                if (data.isSelected) {
                    itemView.featureCategoryNameTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.dodgerBlue
                        )
                    )
                    val iconResource = when (data.title) {
                        "New" -> R.drawable.ic_new_white
                        "Top Picks" -> R.drawable.ic_like_white
                        else -> R.drawable.ic_star_white
                    }
                    itemView.featureCategoryIconBackground.setBackgroundResource(R.drawable.bg_circle_dodgerblue)
                    itemView.featureCategoryImageView.setImageResource(iconResource)
                } else {
                    itemView.featureCategoryNameTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.trout
                        )
                    )
                    val iconResource = when (data.title) {
                        "New" -> R.drawable.ic_new_blue
                        "Top Picks" -> R.drawable.ic_like_blue
                        else -> R.drawable.ic_star_blue
                    }
                    itemView.featureCategoryIconBackground.setBackgroundResource(R.drawable.bg_circle_pattensblue_image)
                    itemView.featureCategoryImageView.setImageResource(iconResource)
                }
            }

        }
    }

    //todo change to base listener
    interface OnActionListener {
        fun onItemSelect(category: FeatureCategory)
    }
}