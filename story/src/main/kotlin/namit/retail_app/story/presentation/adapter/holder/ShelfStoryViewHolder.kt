package namit.retail_app.story.presentation.adapter.holder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.BaseStoryContent
import namit.retail_app.story.data.entity.StoryShelf
import namit.retail_app.story.presentation.adapter.StoryContentAdapter
import kotlinx.android.synthetic.main.item_shelf_story.view.*

class ShelfStoryViewHolder(view: View, onItemClick: (story: BaseStoryContent) -> Unit) : RecyclerView.ViewHolder(view) {
    private var contentAdapter: StoryContentAdapter = StoryContentAdapter(onItemClick = onItemClick)

    init {
        itemView.apply {
            shelfContentRecyclerView.isNestedScrollingEnabled = false
            shelfContentRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            shelfContentRecyclerView.adapter = contentAdapter
        }
    }

    fun bind(storyShelf: StoryShelf) {
        contentAdapter.items = storyShelf.contentList
        itemView.shelfTitleTextView.text = storyShelf.title
    }
}