package namit.retail_app.core.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import namit.retail_app.core.R
import kotlinx.android.synthetic.main.layout_empty_state.view.*

class EmptyLayoutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.layout_empty_state, this)
        emptyButton.setOnClickListener {
            onClickAction.invoke()
        }
    }

    companion object {
        const val TAG = "EmptyLayoutView"
    }

    var onClickAction: () -> Unit = {}

    fun hideActionButton() {
        emptyButton.visibility = View.GONE
    }

    fun showActionButton() {
        emptyButton.visibility = View.VISIBLE
    }

    fun setEmptyTitle(title: String) {
        emptyTitleTextView.text = title
    }

    fun setEmptyDetails(details: String) {
        emptyDetailTextView.text = details
    }

    fun setEmptyButtonText(text: String) {
        emptyButton.text = text
    }

    fun setEmptyImage(imageId: Int) {
        emptyImageView.setImageResource(imageId)
    }
}