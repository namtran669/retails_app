package namit.retail_app.core.presentation.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.Toolbar
import namit.retail_app.core.R
import namit.retail_app.core.extension.getStyledAttributes
import kotlinx.android.synthetic.main.toolbar_delivery.view.*

class DeliveryToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr),
    View.OnClickListener {

    private var toolbarBackground = Color.WHITE
    private var onActionListener: ActionListener? = null

    init {
        View.inflate(context, R.layout.toolbar_delivery, this)

        context.getStyledAttributes(attrs, R.styleable.DeliveryToolbar, defStyleAttr, 0) {
            val backgroundDrawable = getResourceId(R.styleable.DeliveryToolbar_toolbarBackground, 0)
            if (backgroundDrawable != 0) {
                toolbarBackground = backgroundDrawable
            }

            val backgroundColor = getColor(R.styleable.DeliveryToolbar_toolbarBackground, 0)
            if (backgroundColor != 0) {
                toolbarBackground = backgroundColor
            }

            val isShowAddress = getBoolean(R.styleable.DeliveryToolbar_isShowAddress, true)
            toggleAddressBar(isShowAddress)
        }

        setBackgroundColor(toolbarBackground)
        backClickableView.setOnClickListener(this)
        locationClickableView.setOnClickListener(this)
        addressToolbarTextView.setOnClickListener(this)
        deliveryTitleTextView.setOnClickListener(this)
        pinLocationImageView.setOnClickListener(this)

        pinLocationImageView.visibility = View.GONE
        deliveryTitleTextView.visibility = View.GONE
        addressToolbarTextView.visibility = View.GONE
    }

    companion object {
        const val TAG = "DeliveryToolbar"
    }

    fun setScreenTitle(title: String) {
        screenTitleTextView.text = title
    }

    fun setScreenTitleTextSize(titleTextSize: Float) {
        screenTitleTextView.textSize = titleTextSize
    }

    fun setDeliveryAddress(address: String) {
        addressToolbarTextView.text = address
    }

    fun setActionListener(action: ActionListener) {
        this.onActionListener = action
    }

    fun toggleAddressBar(isShow: Boolean) {
        if (isShow) {
            pinLocationImageView.visibility = View.VISIBLE
            deliveryTitleTextView.visibility = View.VISIBLE
            addressToolbarTextView.visibility = View.VISIBLE
        } else {
            pinLocationImageView.visibility = View.GONE
            deliveryTitleTextView.visibility = View.GONE
            addressToolbarTextView.visibility = View.GONE
        }
    }

    fun toggleLocationIcon(isShow: Boolean) {
        if (isShow) {
            locationImageView.visibility = View.GONE
        } else {
            locationImageView.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backClickableView -> {
                onActionListener?.onBackPress()
            }

            R.id.locationClickableView -> {
                onActionListener?.onLocationIconPress()
            }

            R.id.addressToolbarTextView, R.id.deliveryTitleTextView, R.id.pinLocationImageView -> {
                onActionListener?.onSetLocationDataPress()
            }

            else -> return
        }
    }

    interface ActionListener {
        fun onBackPress()
        fun onSetLocationDataPress()
        fun onLocationIconPress()
    }

}