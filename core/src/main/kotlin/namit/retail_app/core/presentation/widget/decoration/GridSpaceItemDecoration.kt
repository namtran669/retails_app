package namit.retail_app.core.presentation.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpaceItemDecoration(
    private val gridSpacingPx: Int = 0,
    private val gridSize: Int = 2,
    private val topOffset: Int = 0,
    private val bottomOffset: Int = 0
) : RecyclerView.ItemDecoration() {

    private var needLeftSpacing = false

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val frameWidth =
            ((parent.width - gridSpacingPx.toFloat() * (gridSize - 1)) / gridSize).toInt()
        val padding = parent.width / gridSize - frameWidth
        val itemPosition =
            (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        val totalItem = state.itemCount
        if (itemPosition < gridSize) {
            outRect.top = topOffset
        } else {
            outRect.top = gridSpacingPx
        }
        if (itemPosition % gridSize == 0) {
            outRect.left = 0
            outRect.right = padding
            needLeftSpacing = true
        } else if ((itemPosition + 1) % gridSize == 0) {
            needLeftSpacing = false
            outRect.right = 0
            outRect.left = padding
        } else if (needLeftSpacing) {
            needLeftSpacing = false
            outRect.left = gridSpacingPx - padding
            if ((itemPosition + 2) % gridSize == 0) {
                outRect.right = gridSpacingPx - padding
            } else {
                outRect.right = gridSpacingPx / 2
            }
        } else if ((itemPosition + 2) % gridSize == 0) {
            needLeftSpacing = false
            outRect.left = gridSpacingPx / 2
            outRect.right = gridSpacingPx - padding
        } else {
            needLeftSpacing = false
            outRect.left = gridSpacingPx / 2
            outRect.right = gridSpacingPx / 2
        }
        var countItemLastRow = gridSize
        if (totalItem % gridSize != 0) {
            countItemLastRow = totalItem % gridSize
        }
        if (itemPosition < totalItem && itemPosition >= totalItem - countItemLastRow) {
            outRect.bottom = bottomOffset
        } else {
            outRect.bottom = 0
        }
    }
}