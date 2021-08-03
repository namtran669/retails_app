package namit.retail_app.core.presentation.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import namit.retail_app.core.R
import namit.retail_app.core.extension.getStyledAttributes
import kotlinx.android.synthetic.main.view_shelf_header_skeleton.view.*

class ShelfHeaderSkeletonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_shelf_header_skeleton, this)

        context.getStyledAttributes(attrs, R.styleable.ShelfHeaderSkeletonView, defStyleAttr, 0) {
            val hasSubTitle = getBoolean(R.styleable.ShelfHeaderSkeletonView_hasSubTitle, false)
            if (hasSubTitle) {
                subTitleView.visibility = View.VISIBLE
            }

            val hasSeeAll = getBoolean(R.styleable.ShelfHeaderSkeletonView_hasSeeAll, false)
            if (hasSeeAll) {
                seeAllView.visibility = View.VISIBLE
            }
        }

        setBackgroundColor(Color.WHITE)
    }

    companion object {
        const val TAG = "ShelfHeaderSkeletonView"
    }

}