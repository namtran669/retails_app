package namit.retail_app.coupon.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.coupon.R
import namit.retail_app.coupon.data.entity.CouponFilterModel
import namit.retail_app.coupon.enums.CouponFilterType
import kotlinx.android.synthetic.main.item_coupon_filter.view.*
import kotlin.properties.Delegates

class CouponFilterAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val FILTER_ALL = -1
        private const val FILTER_FLASH_DEAL = -2
    }

    var items by Delegates.observable(listOf<CouponFilterModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onFilterClick: (index: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_coupon_filter, parent, false)
        return CouponTypeViewHolder(view = view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CouponTypeViewHolder).bind(couponFilterModel = items[position])
    }

    inner class CouponTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onFilterClick.invoke(adapterPosition)
                }
            }
        }

        fun bind(couponFilterModel: CouponFilterModel) {
            when (couponFilterModel.id) {
                FILTER_ALL -> {
                    itemView.titleTextView.text = itemView.context.getString(R.string.all)
                }
                FILTER_FLASH_DEAL -> {
                    itemView.titleTextView.text = itemView.context.getString(R.string.flash_deal)
                }
                else -> {
                    itemView.titleTextView.text = couponFilterModel.name
                }
            }

            if (couponFilterModel.isSelected) {
                itemView.titleTextView.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.dodgerBlue)
                )
                itemView.iconImageView.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.bg_circle_dodgerblue)
                itemView.iconImageView.setColorFilter(Color.WHITE)
            } else {
                itemView.titleTextView.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.trout)
                )
                itemView.iconImageView.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.bg_circle_dodgerblue_20)
                itemView.iconImageView.setColorFilter(
                    ContextCompat.getColor(itemView.context, R.color.dodgerBlue)
                )
            }

            itemView.iconImageView.setImageResource(
                when (couponFilterModel.filterType) {
                    CouponFilterType.ALL -> {
                        R.drawable.ic_coupon_all
                    }
                    CouponFilterType.FLASH -> {
                        R.drawable.ic_coupon_flash_deals
                    }
                    CouponFilterType.SUPERMARKET -> {
                        R.drawable.ic_coupon_grocery
                    }
                    CouponFilterType.RESTAURANTS -> {
                        R.drawable.ic_coupon_food
                    }
                    CouponFilterType.CONVENIENCE_STORE -> {
                        R.drawable.ic_coupon_convenience
                    }
                    else -> {
                        R.drawable.ic_coupon_cafe
                    }
                }
            )
        }
    }
}