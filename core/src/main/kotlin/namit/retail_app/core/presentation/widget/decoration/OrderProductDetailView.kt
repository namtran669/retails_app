package namit.retail_app.core.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import namit.retail_app.core.R
import namit.retail_app.core.extension.disable
import namit.retail_app.core.extension.enable
import kotlinx.android.synthetic.main.view_order_product_detail.view.*


class OrderProductDetailView constructor(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs), View.OnClickListener {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_order_product_detail, this, true)
        addOneItemImageView.setOnClickListener(this)
        reduceOneItemImageView.setOnClickListener(this)
        addToCartButton.setOnClickListener(this)
    }

    companion object {
        val TAG = OrderProductDetailView::class.java.simpleName
    }

    var onAddItem: () -> Unit = {}
    var onReduceItem: () -> Unit = {}
    var onAddToCart: (Int) -> Unit = {}

    fun toggleMinusButton(isOn: Boolean) {
        if (isOn) {
            reduceOneItemImageView.enable()
        } else {
            reduceOneItemImageView.disable()
        }
    }

    fun setQuantityNumber(amount: Int) {
        sizeOrderProductTextView.text = amount.toString()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.addOneItemImageView -> {
                onAddItem.invoke()
            }

            R.id.reduceOneItemImageView -> {
                onReduceItem.invoke()
            }

            R.id.addToCartButton -> {
                onAddToCart.invoke(getQuantityOrder())
            }

            else -> return
        }
    }

    fun setAddToCartAction(action: () -> Unit) {
        addToCartButton.setOnClickListener {
            action.invoke()
        }
    }

    private fun getQuantityOrder() : Int {
       return sizeOrderProductTextView.text.toString().toInt()
    }

}
