package namit.retail_app.core.presentation.cart_button

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import namit.retail_app.core.R
import namit.retail_app.core.extension.formatCurrency
import namit.retail_app.core.extension.toThaiCurrency
import kotlinx.android.synthetic.main.float_cart_button_view.view.*

class FloatCartButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.float_cart_button_view, this)
    }

    companion object {
        const val TAG = "FloatCartButton"
    }

    fun setCartValue(total: Double?, amount: Int?) {
        //todo validate value
        hideSkeleton()

        val totalValue = total ?: 0.0
        totalPriceTextView.apply {
            if (totalValue > 0) {
                text = totalValue.formatCurrency().toThaiCurrency()
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }
        productAmountTextView.text = amount?.toString() ?: "0"
    }

    fun hideSkeleton(){
        isEnabled = true
        cartSkeletonView.visibility = View.GONE
        cartContentLayout.visibility = View.VISIBLE
        productAmountTextView.visibility = View.VISIBLE
    }

    fun showSkeleton(){
        isEnabled = false
        cartSkeletonView.visibility = View.VISIBLE
        cartContentLayout.visibility = View.GONE
        productAmountTextView.visibility = View.GONE
    }
}