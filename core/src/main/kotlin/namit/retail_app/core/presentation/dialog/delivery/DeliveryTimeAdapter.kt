package namit.retail_app.core.presentation.dialog.delivery

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.SlotDetail
import kotlinx.android.synthetic.main.item_delivery_time.view.*
import kotlin.properties.Delegates

class DeliveryTimeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<SlotDetail>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_delivery_time, parent, false)
        return DeliveryTimeViewHolder(view = view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as DeliveryTimeViewHolder).bind(deliveryTime = items[position])

    inner class DeliveryTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    for ((index, value) in items.withIndex()) {
                        if (value.isSelected) {
                            value.isSelected = false
                            notifyItemChanged(index)
                            break
                        }
                    }
                    val deliveryTime = items[adapterPosition]
                    deliveryTime.isSelected = true
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        fun bind(deliveryTime: SlotDetail) {
            itemView.apply {
                deliveryTimeTextView.text = deliveryTime.hour

                if (!deliveryTime.isFull && deliveryTime.isPick) {
                    isClickable = true
                    deliveryUnavailableTextView.visibility = View.GONE
                    deliveryTimeTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.trout
                        )
                    )
                    if (deliveryTime.isSelected) {
                        deliveryTimeImageView.setImageResource(R.drawable.ic_checkbox_checked)
                        deliveryTimeTextView.setTypeface(null, Typeface.BOLD)
                        setBackgroundResource(R.drawable.bg_item_delivery_time_selected)

                    } else {
                        deliveryTimeImageView.setImageResource(R.drawable.ic_checkbox_available)
                        deliveryTimeTextView.setTypeface(null, Typeface.NORMAL)
                        setBackgroundResource(R.drawable.bg_catskillwhite30_10_radius)
                    }
                } else {
                    isClickable = false
                    deliveryTimeImageView.setImageResource(R.drawable.ic_checkbox_unavailable)
                    deliveryUnavailableTextView.visibility = View.VISIBLE
                    deliveryTimeTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.trout30
                        )
                    )
                    setBackgroundResource(R.drawable.bg_catskillwhite30_10_radius)
                }
            }
        }
    }

}