package namit.retail_app.order.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import namit.retail_app.order.R
import kotlinx.android.synthetic.main.view_order_tracking_status.view.*


class OrderTrackingStatusView constructor(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_order_tracking_status, this, true)
    }

    companion object {
        val TAG = OrderTrackingStatusView::class.java.simpleName
    }

    enum class State() {
        INACTIVE,
        ACTIVE,
        CURRENT
    }

    fun toggleConfirmStatus(state: State) {
        handleOrderIcon(state, orderConfirmImageView)
    }

    fun togglePrepareStatus(state: State) {
        handleDividerLine(state, orderPrepareDividerView)
        handleOrderIcon(state, orderPrepareImageView)
    }

    fun toggleDeliveryStatus(state: State) {
        handleDividerLine(state, orderDeliverDividerView)
        handleOrderIcon(state, orderDeliverImageView)
    }

    fun toggleSuccessStatus(state: State) {
        handleDividerLine(state, orderSuccessDividerView)
        handleOrderIcon(state, orderSuccessImageView)
    }

    private fun handleOrderIcon(state: State, imageView: ImageView) {
        when (state) {
            State.ACTIVE -> {
                imageView.setBackgroundResource(R.drawable.bg_circle_dodgerblue)
                imageView.layoutParams.apply {
                    width = resources.getDimensionPixelSize(R.dimen.orderIconNormalWidth)
                    height = width
                }
                imageView.requestLayout()
            }
            State.CURRENT -> {
                imageView.setBackgroundResource(R.drawable.bg_circle_dodgerblue)
                imageView.layoutParams.apply {
                    width = resources.getDimensionPixelSize(R.dimen.orderIconCurrentWidth)
                    height = width
                }
                imageView.requestLayout()
            }
            State.INACTIVE -> {
                imageView.setBackgroundResource(R.drawable.bg_circle_gray)
                imageView.layoutParams.apply {
                    width = resources.getDimensionPixelSize(R.dimen.orderIconNormalWidth)
                    height = width
                }
                imageView.requestLayout()
            }
        }
    }

    private fun handleDividerLine(state: State, view: View) {
        when (state) {
            State.ACTIVE, State.CURRENT -> {
                view.setBackgroundResource(R.color.dodgerBlue)
            }
            State.INACTIVE -> {
                view.setBackgroundResource(R.color.catskillWhite)
            }
        }
    }

}

