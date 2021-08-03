package namit.retail_app.core.presentation.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.SortModel
import kotlinx.android.synthetic.main.item_sort_product.view.*
import kotlin.properties.Delegates

class SortProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<SortModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onItemSelected: (selectedIndex: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_sort_product, parent, false)
        return SortViewHolder(view = view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as SortViewHolder).bind(sortItem = items[position])

    inner class SortViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemSelected.invoke(adapterPosition)
                }
            }
        }

        @Suppress("DEPRECATION")
        fun bind(sortItem: SortModel) {
            itemView.sortTextView.apply {
                text = sortItem.title

                if (sortItem.isSelected) {
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.WHITE)
                    setBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.dodgerBlue
                        )
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setTextAppearance(R.style.H3_White)
                    } else {
                        setTextAppearance(itemView.context, R.style.H3_White)
                    }
                } else {
                    setTypeface(null, Typeface.NORMAL)
                    setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.trout
                        )
                    )
                    setBackgroundColor(Color.TRANSPARENT)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setTextAppearance(R.style.H4_Trout)
                    } else {
                        setTextAppearance(itemView.context, R.style.H4_Trout)
                    }
                }
            }
        }
    }
}