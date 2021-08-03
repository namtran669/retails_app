package namit.retail_app.core.presentation.dialog.delivery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.TimeSlot
import namit.retail_app.core.extension.*
import kotlinx.android.synthetic.main.item_delivery_day.view.*
import kotlin.properties.Delegates

class DeliveryDayAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<TimeSlot>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onSelectDay: (index: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_delivery_day, parent, false)
        return DeliveryDayViewHolder(view = view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as DeliveryDayViewHolder).bind(deliveryDay = items[position])

    inner class DeliveryDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSelectDay(adapterPosition)
                }
            }
        }

        fun bind(deliveryDay: TimeSlot) {
            itemView.apply {

                //TextView
                if (deliveryDay.isFull) {
                    fullBookTitleTextView.visibility = View.VISIBLE
                    deliveryDayTitleTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.trout30
                        )
                    )
                    deliveryDayTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.trout20
                        )
                    )
                } else {
                    fullBookTitleTextView.visibility = View.GONE
                    deliveryDayTitleTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.trout
                        )
                    )
                    deliveryDayTextView.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.trout60
                        )
                    )
                }

                if (deliveryDay.isDeliveryNow) {
                    deliveryDayTitleTextView.text = resources.getString(R.string.delivery_now)
                    deliveryDayTextView.visibility = View.GONE
                    fullBookTitleTextView.visibility = View.GONE
                } else {
                    deliveryDayTextView.visibility = View.VISIBLE
                    deliveryDayTitleTextView.visibility = View.VISIBLE

                    val dateText = deliveryDay.date.convertToDateAndApplyFormat(
                        DATE_TIME_FORMAT_YYYY_MM_DD,
                        DATE_TIME_FORMAT_DD_MMM_YYYY
                    )

                    val dayOfWeekText = deliveryDay.date.convertToDate(DATE_TIME_FORMAT_YYYY_MM_DD)
                        .convertTodayTomorrowTime(DAY_OF_WEEK_FULL)

                    //Change dayOfWeek in obj to don't need re-check at grocery merchant
                    deliveryDay.dayOfWeek = dayOfWeekText

                    deliveryDayTitleTextView.text = dayOfWeekText
                    deliveryDayTextView.text = dateText
                }

                //Background
                if (deliveryDay.isSelected) {
                    backgroundImageView.setImageResource(R.drawable.bg_time_slot_selected)
                } else {
                    if (deliveryDay.isFull) {
                        backgroundImageView.setImageResource(R.drawable.bg_catskillwhite30_10_radius)
                    } else {
                        backgroundImageView.setImageResource(R.drawable.bg_catskillwhite60_10_radius)
                    }
                }

            }
        }

    }

}