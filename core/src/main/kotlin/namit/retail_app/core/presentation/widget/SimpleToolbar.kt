package namit.retail_app.core.presentation.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import namit.retail_app.core.R
import namit.retail_app.core.extension.getStyledAttributes
import kotlinx.android.synthetic.main.toolbar_simple.view.*

class SimpleToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    View.OnClickListener {

    private var toolbarBackground = Color.WHITE
    var onBackPressed: () -> Unit = {}

    init {
        View.inflate(context, R.layout.toolbar_simple, this)

        context.getStyledAttributes(attrs, R.styleable.SimpleToolbar, defStyleAttr, 0) {
            val backgroundDrawable = getResourceId(R.styleable.SimpleToolbar_toolbarBackground, 0)
            if (backgroundDrawable != 0) {
                toolbarBackground = backgroundDrawable
            }

            val backgroundColor = getColor(R.styleable.SimpleToolbar_toolbarBackground, 0)
            if (backgroundColor != 0) {
                toolbarBackground = backgroundColor
            }

            val titleText = getString(R.styleable.SimpleToolbar_titleContent)
            if (titleText != null) {
                screenTitleTextView.text = titleText
            }

            val isHasBack = getBoolean(R.styleable.SimpleToolbar_hasBack, true)
            if (isHasBack) {
                backImageView.visibility = View.VISIBLE
            } else {
                backImageView.visibility = View.GONE
            }
        }

        setBackgroundColor(toolbarBackground)
        backImageView.setOnClickListener(this)
    }

    companion object {
        const val TAG = "SimpleToolbar"
    }

    fun setScreenTitle(title: String) {
        screenTitleTextView.text = title
    }

    fun setLeftIconImageView(resourceId: Int) {
        backImageView.setImageResource(resourceId)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.backImageView -> {
                onBackPressed()
            }

            else -> return
        }
    }
}