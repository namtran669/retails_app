package namit.retail_app.coupon.presentation.banner

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import namit.retail_app.coupon.R
import kotlinx.android.synthetic.main.layout_coupon_banner.view.*

class CouponBannerView : ConstraintLayout {
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.layout_coupon_banner, this, true)

        setOnClickListener {
            onBannerClick.invoke()
        }
    }

    var onBannerClick:() -> Unit = {}

    fun setTotalCoupons(totalCoupon: Int) {
        couponBannerTextView.text =
            context.resources.getString(
                R.string.you_have_x_coupon, totalCoupon.toString()
            )
    }
}