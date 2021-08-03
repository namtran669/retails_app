package namit.retail_app.core.presentation.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(
    private val startEndSpace: Int = 0,
    private val betweenSpace: Int = 0,
    private val topSpace: Int = 0,
    private val bottomSpace: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        with(outRect) {
            if (itemPosition == 0) {
                top = topSpace
            } else if (itemCount > 0 && itemPosition == itemCount - 1) {
                top = betweenSpace
                bottom = bottomSpace
            } else {
                top = betweenSpace
            }
            left = startEndSpace
            right = startEndSpace
        }
    }
}