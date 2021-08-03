package namit.retail_app.core.presentation.widget

import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class EndlessProductVerticalListListener : NestedScrollView.OnScrollChangeListener {

    private var layoutManager: RecyclerView.LayoutManager

    constructor(layoutManager: LinearLayoutManager) {
        this.layoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        this.layoutManager = layoutManager
    }

    override fun onScrollChange(
        view: NestedScrollView?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if (view?.getChildAt(view.childCount - 1) != null
            && (scrollY >= view.getChildAt(view.childCount - 1).measuredHeight - view.measuredHeight)
            && scrollY > oldScrollY
        ) {
            var firstItemIndex = 0
            if (layoutManager is GridLayoutManager) {
                firstItemIndex =
                    (layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
            } else if (layoutManager is LinearLayoutManager) {
                firstItemIndex =
                    (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            }

            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            if (visibleItemCount + firstItemIndex >= totalItemCount) {
                onGoToBottomList()
            }
        }
    }

    abstract fun onGoToBottomList()
}