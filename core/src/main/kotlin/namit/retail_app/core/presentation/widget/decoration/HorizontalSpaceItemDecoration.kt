package namit.retail_app.core.presentation.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(
    private val startEndSpace: Int = 0,
    private val betweenSpace: Int = 0,
    private val topBottomSpace: Int = 0
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
                left = startEndSpace
            } else if (itemCount > 0 && itemPosition == itemCount - 1) {
                left = betweenSpace
                right = startEndSpace
            } else {
                left = betweenSpace
            }
            top = topBottomSpace
            bottom = topBottomSpace
        }
    }
}