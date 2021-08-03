package namit.retail_app.order.presentation.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import namit.retail_app.order.R
import kotlinx.android.synthetic.main.view_order_status_floating.view.*

class OrderStatusFloatingView constructor(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_order_status_floating, this, true)

        floatingContainer.outlineProvider = ViewOutlineProvider.BACKGROUND
        floatingContainer.clipToOutline = true

        setBackgroundColor(Color.TRANSPARENT)
    }

    companion object {
        val TAG = OrderTrackingStatusView::class.java.simpleName
    }

    fun setMerchantName(name: String) {
        merchantNameTextView.text = name
    }

    @SuppressLint("SetTextI18n")
    fun setArriveTime(time: Int) {
        arriveTitleTextView.visibility = View.VISIBLE
        arriveTimeTextView.visibility = View.VISIBLE

        arriveTitleTextView.text = resources.getString(R.string.arrive_in)
        arriveTimeTextView.text = "$time min"
    }

    fun changeToConfirmState() {
        handleDotViewState(confirmDotView, true)
        handleDotViewState(prepareDotView, false)
        handleDotViewState(deliverDotView, false)
        handleDotViewState(successDotView, false)

        handleDividerViewState(prepareDividerView, false)
        handleDividerViewState(deliverDividerView, false)
        handleDividerViewState(successDividerView, false)

        statusLogoImageView.setImageResource(R.drawable.ic_order_store)
        statusContentTextView.text = resources.getString(R.string.order_confirmed)
    }

    fun changeToPrepareState() {
        handleDotViewState(confirmDotView, true)
        handleDotViewState(prepareDotView, true)
        handleDotViewState(deliverDotView, false)
        handleDotViewState(successDotView, false)

        handleDividerViewState(prepareDividerView, true)
        handleDividerViewState(deliverDividerView, false)
        handleDividerViewState(successDividerView, false)

        statusLogoImageView.setImageResource(R.drawable.ic_order_food)
        statusContentTextView.text = resources.getString(R.string.food_is_prepared)
    }

    fun changeToDeliveryState() {
        handleDotViewState(confirmDotView, true)
        handleDotViewState(prepareDotView, true)
        handleDotViewState(deliverDotView, true)
        handleDotViewState(successDotView, false)

        handleDividerViewState(prepareDividerView, true)
        handleDividerViewState(deliverDividerView, true)
        handleDividerViewState(successDividerView, false)

        statusLogoImageView.setImageResource(R.drawable.ic_order_motobike)
        statusContentTextView.text = resources.getString(R.string.order_is_being_deliverd)
    }

    fun changeToSuccessState() {
        handleDotViewState(confirmDotView, true)
        handleDotViewState(prepareDotView, true)
        handleDotViewState(deliverDotView, true)
        handleDotViewState(successDotView, true)

        handleDividerViewState(prepareDividerView, true)
        handleDividerViewState(deliverDividerView, true)
        handleDividerViewState(successDividerView, true)

        statusLogoImageView.setImageResource(R.drawable.ic_order_complete)
        statusContentTextView.text = resources.getString(R.string.order_is_completed)

        arriveTitleTextView.visibility = View.VISIBLE
        arriveTitleTextView.text = resources.getString(R.string.enjoy_you_meal)
        arriveTimeTextView.visibility = View.GONE

    }

    private fun handleDotViewState(view: View, isActive: Boolean) {
        if (isActive) {
            view.setBackgroundResource(R.drawable.bg_circle_dodgerblue)
        } else {
            view.setBackgroundResource(R.drawable.bg_circle_botticelli)
        }
    }

    private fun handleDividerViewState(view: View, isActive: Boolean) {
        if (isActive) {
            view.setBackgroundResource(R.color.dodgerBlue)
        } else {
            view.setBackgroundResource(R.color.botticelli)
        }
    }

}