package namit.retail_app.coupon.presentation.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.enums.CouponType
import namit.retail_app.core.enums.MerchantType
import namit.retail_app.core.extension.loadImage
import namit.retail_app.core.extension.toDate
import namit.retail_app.core.extension.toGrayScale
import namit.retail_app.coupon.R
import namit.retail_app.coupon.presentation.detail.CouponDetailViewModel
import kotlinx.android.synthetic.main.item_vertical_coupon_discount_amout.view.*
import kotlin.properties.Delegates

class CouponVerticalListAdapter : BaseCouponAdapter() {

    var items by Delegates.observable(listOf<CouponModel>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var itemWidth = 0
    var itemHeight = 0
    var onCouponClick: (couponModel: CouponModel) -> Unit = {}
    var onUseNowClick: (couponModel: CouponModel) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SKELETON_LOADING -> {
                VerticalCouponSkeleton(
                    view = layoutInflater.inflate(
                        R.layout.item_vertical_coupon_skeleton,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_FIXED -> {
                VerticalCoupon(
                    view = layoutInflater.inflate(
                        R.layout.item_vertical_coupon_discount_amout,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_PERCENTAGE -> {
                VerticalCoupon(
                    view = layoutInflater.inflate(
                        R.layout.item_vertical_coupon_discount_percentage,
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_DELIVERY_FEE -> {
                VerticalCoupon(
                    view = layoutInflater.inflate(
                        R.layout.item_vertical_coupon_free_delivery,
                        parent,
                        false
                    )
                )
            }
            else -> {
                CouponHorizontalListAdapter.UnknownCouponViewHolder(
                    view = layoutInflater.inflate(
                        R.layout.item_unknown_content,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return getCouponItemViewType(items[position].couponType)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (items[position].couponType != CouponType.SKELETON_LOADING) {
            (holder as VerticalCoupon).bind(items[position])
        }
    }

    inner class VerticalCouponSkeleton(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.couponConstraintLayout.apply {
                layoutParams.width = itemWidth
                layoutParams.height = itemHeight
            }
        }
    }

    inner class VerticalCoupon(view: View) : BaseCouponViewHolder(view) {
        init {
            itemView.couponConstraintLayout.apply {
                layoutParams.width = itemWidth
                layoutParams.height = itemHeight
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onCouponClick.invoke(items[adapterPosition])
                }
            }

            itemView.collectNowButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onUseNowClick.invoke(items[adapterPosition])
                }
            }
        }

        fun bind(couponModel: CouponModel) {
            if (couponModel.couponMerchantType == MerchantType.MERCHANT) {
                itemView.apply {
                    couponIconImageView.visibility = View.GONE
                    couponMerchantIconImageView.visibility = View.VISIBLE
                    couponModel.merchantInfoItem?.imageUrl?.let {
                        couponMerchantIconImageView.loadImage(it)
                    }
                    couponConstraintLayout.background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.img_coupon_merchant_bg)
                    couponDesc1TextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.trout))
                    couponDesc2TextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.trout))
                    couponValueTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.trout))
                    lineImageView.setImageResource(R.drawable.ic_coupon_line_black)
                    if (couponModel.isRanOut) {
                        couponConstraintLayout.toGrayScale()
                        ranOutImageView.visibility = View.VISIBLE
                    }
                }
            } else {
                itemView.couponIconImageView.visibility = View.VISIBLE
                itemView.couponMerchantIconImageView.visibility = View.GONE
                setCouponIcon(couponModel.couponMerchantType)
                setCouponBackground(couponModel.couponMerchantType)
                setRanOut(couponModel.isRanOut)
            }

            setFlashDeals(couponModel)
            couponModel.couponValue?.let {
                setCouponValue(couponValue = it, couponType = couponModel.couponType)
            }
        }

        private fun setFlashDeals(couponModel: CouponModel) {
            if (couponModel.isFlashDeals) {
                itemView.apply {
                    countDownImageView.visibility = View.VISIBLE
                    countDownTextView.visibility = View.VISIBLE
                    expireDateTitleTextView.visibility = View.GONE
                    expireDateTextView.visibility = View.GONE
                    expireDateImageView.visibility = View.GONE
                }

                couponModel.endDate?.let {
                    startFlashDealTimer(it, itemView.countDownTextView)
                }
            } else {
                itemView.apply {
                    countDownImageView.visibility = View.GONE
                    countDownTextView.visibility = View.GONE
                    expireDateTitleTextView.visibility = View.VISIBLE
                    expireDateTextView.visibility = View.VISIBLE
                    expireDateImageView.visibility = View.VISIBLE
                    expireDateTextView.text = couponModel.endDate?.toDate(dateFormat =
                        CouponDetailViewModel.COUPON_DURATION_DATE_FORMAT)
                }
            }
        }

        private fun setCouponIcon(merchantType: MerchantType) {
            when (merchantType) {
                MerchantType.GROCERY -> {
                    itemView.couponIconImageView.setImageResource(R.drawable.ic_coupon_grocery)
                }
                MerchantType.RESTAURANT -> {
                    itemView.couponIconImageView.setImageResource(R.drawable.ic_coupon_food)
                }
                MerchantType.CONVENIENCE -> {
                    itemView.couponIconImageView.setImageResource(R.drawable.ic_coupon_convenience)
                }
                else -> {
                    itemView.couponIconImageView.setImageResource(R.drawable.ic_coupon_cafe)
                }
            }
        }

        private fun setRanOut(isRanOut: Boolean) {
            itemView.apply {
                if (isRanOut) {
                    ranOutImageView.visibility = View.VISIBLE
                    couponIconImageView.setColorFilter(
                        ContextCompat.getColor(itemView.context, R.color.altoGray),
                        PorterDuff.Mode.SRC_ATOP)
                    couponDesc1TextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white50))
                    couponDesc2TextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white50))
                    couponValueTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white50))
                    couponConstraintLayout.background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.img_coupon_convenience_bg)
                    couponConstraintLayout.toGrayScale()
                } else {
                    ranOutImageView.visibility = View.GONE
                    couponIconImageView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                    couponDesc1TextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                    couponDesc2TextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                    couponValueTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                }
            }
        }

        private fun setCouponBackground(merchantType: MerchantType) {
            itemView.couponConstraintLayout.background = when (merchantType) {
                MerchantType.GROCERY -> {
                    ContextCompat.getDrawable(itemView.context, R.drawable.img_coupon_grocery_bg)
                }
                MerchantType.RESTAURANT -> {
                    ContextCompat.getDrawable(itemView.context, R.drawable.img_coupon_food_bg)
                }
                MerchantType.CONVENIENCE -> {
                    ContextCompat.getDrawable(itemView.context, R.drawable.img_coupon_convenience_bg)
                }
                else -> {
                    ContextCompat.getDrawable(itemView.context, R.drawable.img_coupon_cafe_bg)
                }
            }
        }

        private fun setCouponValue(couponValue: String, couponType: CouponType) {
            if (couponType == CouponType.FIXED || couponType == CouponType.PERCENTAGE) {
                itemView.couponValueTextView.text = couponValue
            }
        }
    }
}