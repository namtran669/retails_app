package namit.retail_app.core.presentation.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ProductGridSpaceItemDecoration (
    private val startEndSpace: Int,
    private val betweenSpace: Int,
    private val topSpace: Int,
    private val bottomSpace: Int
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
            top = topSpace
            if (itemPosition % 2 == 0) {
                left = startEndSpace
                right = betweenSpace / 2
            } else {
                left = betweenSpace / 2
                right = startEndSpace
            }

            if (itemCount % 2 == 0) {
                if (itemPosition == itemCount - 1 || itemPosition == itemCount - 2) {
                    bottom = bottomSpace
                }
            } else {
                if (itemPosition == itemCount - 1) {
                    bottom = bottomSpace
                }
            }
        }
    }
}