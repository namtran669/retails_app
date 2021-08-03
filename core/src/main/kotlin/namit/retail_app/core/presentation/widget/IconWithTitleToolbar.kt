package namit.retail_app.core.presentation.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import namit.retail_app.core.R
import namit.retail_app.core.extension.getStyledAttributes
import namit.retail_app.core.extension.loadCircleImage
import kotlinx.android.synthetic.main.toolbar_icon.view.*

class IconWithTitleToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    View.OnClickListener {

    private var toolbarBackground = Color.WHITE
    private var onOnActionListener: OnActionListener? = null

    init {
        View.inflate(context, R.layout.toolbar_icon, this)

        context.getStyledAttributes(attrs, R.styleable.IconWithTitleToolbar, defStyleAttr, 0) {
            val backgroundDrawable =
                getResourceId(R.styleable.IconWithTitleToolbar_toolbarBackground, 0)
            if (backgroundDrawable != 0) {
                toolbarBackground = backgroundDrawable
            }

            val backgroundColor = getColor(R.styleable.IconWithTitleToolbar_toolbarBackground, 0)
            if (backgroundColor != 0) {
                toolbarBackground = backgroundColor
            }
        }

        setBackgroundColor(toolbarBackground)
        backImageView.setOnClickListener(this)
    }

    fun setScreenTitle(title: String) {
        titleTextView.text = title
    }

    fun setToolbarIcon(url: String) {
        iconImageView.loadCircleImage(imageUrl = url)
    }

    fun setToolbarIcon(resourceId: Int) {
        iconImageView.setImageResource(resourceId)
    }

    fun setToolbarIcon(drawable: Drawable) {
        iconImageView.setImageDrawable(drawable)
    }

    fun setToolbarBackImage(resourceId: Int) {
        backImageView.setImageResource(resourceId)
    }

    fun setIconSize(width: Int, height: Int) {
        iconImageView.layoutParams.width = width
        iconImageView.layoutParams.height = height
    }

    fun hideIcon() {
        iconImageView.visibility = View.GONE
    }

    fun setActionListener(onAction: OnActionListener) {
        this.onOnActionListener = onAction
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backImageView -> {
                onOnActionListener?.onBackPress()
            }

            else -> return
        }
    }

    interface OnActionListener {
        fun onBackPress()
    }
}