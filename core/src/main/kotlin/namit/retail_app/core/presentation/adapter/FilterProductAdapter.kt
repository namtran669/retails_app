package namit.retail_app.core.presentation.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.FilterModel
import kotlinx.android.synthetic.main.item_filter_product.view.*
import kotlin.properties.Delegates

class FilterProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<FilterModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onItemChecked: (checkedIndex: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_filter_product, parent, false)
        return FilterViewHolder(view = view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as FilterViewHolder).bind(filerItem = items[position])

    inner class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemChecked.invoke(adapterPosition)
                }
            }
        }

        @Suppress("DEPRECATION")
        fun bind(filerItem: FilterModel) {
            itemView.apply {
                filterTextView.text = filerItem.title

                if (filerItem.isSelected) {
                    filterImageView.setImageResource(R.drawable.ic_checkbox_tick_checked)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        filterTextView.setTextAppearance(R.style.H3_Trout)
                    } else {
                        filterTextView.setTextAppearance(itemView.context, R.style.H3_Trout)
                    }

                } else {
                    filterImageView.setImageResource(R.drawable.ic_checkbox_tick_available)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        filterTextView.setTextAppearance(R.style.H4_Trout)
                    } else {
                        filterTextView.setTextAppearance(itemView.context, R.style.H4_Trout)
                    }
                }
            }
        }
    }

}