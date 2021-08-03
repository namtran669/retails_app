package namit.retail_app.core.presentation.widget

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import namit.retail_app.core.R
import namit.retail_app.core.extension.loadImage
import namit.retail_app.core.extension.toThaiCurrency
import kotlinx.android.synthetic.main.item_horizontal_product.view.*

class ItemProductHorizontalView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.item_horizontal_product, this)
    }

    companion object {
        const val TAG = "DeliveryToolbar"
    }

    fun setProductImage(url: String) {
        productImageView.loadImage(imageUrl = url)
    }

    fun setProductName(name: String) {
        productNameTextView.text = name
    }

    fun setDiscountPrice(price: Float) {
        discountPriceTextView.apply {
            //todo validate price

            text = price.toString().toThaiCurrency()
        }
    }

    fun setActutalPrice(price: Float) {
        actualPriceTextView.apply {
            //todo validate price
            text = price.toString().toThaiCurrency()
            paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    fun setDiscountPercent(number: Int) {
        discountPercentTextView.apply {
            //todo validate percent number

            text = number.toString().plus("%")
        }
    }

}