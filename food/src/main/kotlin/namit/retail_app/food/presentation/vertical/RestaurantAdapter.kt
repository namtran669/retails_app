package namit.retail_app.food.presentation.vertical

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.extension.loadImage
import namit.retail_app.food.R
import kotlinx.android.synthetic.main.item_restaurant.view.*
import java.text.DecimalFormat
import kotlin.properties.Delegates

class RestaurantAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items by Delegates.observable(listOf<MerchantInfoItem>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    enum class ViewType(val value: Int) {
        SKELETON(0),
        DATA(1),
    }

    private val DISTANCE_FORMAT = "0.#"
    var onSelectItem: (data: MerchantInfoItem) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == ViewType.SKELETON.value) {
            val view = layoutInflater.inflate(R.layout.item_restaurant_skeleton, parent, false)
            RestaurantSkeletonViewHolder(view)
        } else {
            val view = layoutInflater.inflate(R.layout.item_restaurant, parent, false)
            RestaurantViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSkeletonData(items[position])) {
            ViewType.SKELETON.value
        } else {
            ViewType.DATA.value
        }
    }

    private fun isSkeletonData(data: MerchantInfoItem): Boolean {
        return data.id.isBlank()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].let {
            if (!isSkeletonData(it)) {
                (holder as RestaurantViewHolder).bind(data = it)
            }
        }
    }

    inner class RestaurantSkeletonViewHolder(private val view: View) : RecyclerView.ViewHolder(view)

    inner class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onSelectItem(items[adapterPosition])
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(data: MerchantInfoItem) {
            itemView.apply {
                restaurantImageView.loadImage(imageUrl = data.cover, placeHolder = R.color.altoGray)
                restaurantTitleTextView.text = data.title

                if (!data.description.isNullOrBlank()) {
                    restaurantDescriptionTextView.text = data.description
                }

                if (data.distance > 0) {
                    distanceTextView.visibility = View.VISIBLE
                    distanceTextView.text =
                        "${DecimalFormat(DISTANCE_FORMAT).format(data.distance)} ${resources.getString(
                            R.string.km_symbol
                        )}"
                }

            }

        }

    }
}