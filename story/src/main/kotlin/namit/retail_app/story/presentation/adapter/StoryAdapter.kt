package namit.retail_app.story.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.BaseStoryContent
import namit.retail_app.story.R
import namit.retail_app.story.data.entity.StoryShelf
import namit.retail_app.story.presentation.adapter.holder.ShelfStoryViewHolder
import kotlin.properties.Delegates

class StoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<StoryShelf>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var onItemClick: (story: BaseStoryContent) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_shelf_story, parent, false)
        return ShelfStoryViewHolder(view = view, onItemClick = onItemClick)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ShelfStoryViewHolder).bind(storyShelf = items[position])
    }
}