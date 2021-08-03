package namit.retail_app.story.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.core.data.entity.BaseStoryContent
import namit.retail_app.core.enums.StoryContentType
import namit.retail_app.core.extension.DATE_TIME_FORMAT_DD_MMM_YYYY
import namit.retail_app.core.extension.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM_SS
import namit.retail_app.core.extension.convertToDateFromUTCTime
import namit.retail_app.core.extension.loadImage
import namit.retail_app.core.presentation.adapter.holder.UnknownContentViewHolder
import namit.retail_app.story.R
import namit.retail_app.story.data.entity.AnnoucementContent
import namit.retail_app.story.data.entity.FeaturedContent
import namit.retail_app.story.data.entity.PromotionContent
import namit.retail_app.story.data.entity.StoryContent
import kotlinx.android.synthetic.main.item_content_annoucement.view.*
import kotlinx.android.synthetic.main.item_content_featured.view.*
import kotlinx.android.synthetic.main.item_content_promotion.view.*
import kotlinx.android.synthetic.main.item_content_story.view.*
import kotlin.properties.Delegates

class StoryContentAdapter(
    val onItemClick: (story: BaseStoryContent) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items by Delegates.observable(listOf<BaseStoryContent>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            StoryContentType.PROMOTION.value -> {
                val view = layoutInflater.inflate(R.layout.item_content_promotion, parent, false)
                PromotionContentViewHolder(view = view)
            }
            StoryContentType.STORY.value -> {
                val view = layoutInflater.inflate(R.layout.item_content_story, parent, false)
                StoryContentViewHolder(view = view)
            }
            StoryContentType.FEATURED.value -> {
                val view = layoutInflater.inflate(R.layout.item_content_featured, parent, false)
                FeaturedContentViewHolder(view = view)
            }
            StoryContentType.ANNOUCEMENT.value -> {
                val view = layoutInflater.inflate(R.layout.item_content_annoucement, parent, false)
                AnnoucementContentViewHolder(view = view)
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_unknown_content, parent, false)
                UnknownContentViewHolder(view = view)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is PromotionContent -> StoryContentType.PROMOTION.value
            is StoryContent -> StoryContentType.STORY.value
            is FeaturedContent -> StoryContentType.FEATURED.value
            is AnnoucementContent -> StoryContentType.ANNOUCEMENT.value
            else -> StoryContentType.UNKNOWN.value
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val data = items[position]) {
            is PromotionContent -> (holder as PromotionContentViewHolder).bind(promotionContent = data)
            is StoryContent -> (holder as StoryContentViewHolder).bind(storyContent = data)
            is FeaturedContent -> (holder as FeaturedContentViewHolder).bind(featuredContent = data)
            is AnnoucementContent -> (holder as AnnoucementContentViewHolder).bind(
                annoucementContent = data
            )
        }
    }

    inner class StoryContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick.invoke(items[adapterPosition])
                }
            }

            itemView.storyReadMoreButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick.invoke(items[adapterPosition])
                }
            }
        }

        fun bind(storyContent: StoryContent) {
            itemView.apply {
                storyTitleHomeImageView.text = storyContent.title
                storyContent.imageUrl?.let {
                    storyImageHomeImageView.loadImage(imageUrl = it)
                }

                storyContent.updateDate?.let {
                    val startDate =
                        storyContent.updateDate?.convertToDateFromUTCTime(
                            DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM_SS,
                            DATE_TIME_FORMAT_DD_MMM_YYYY
                        ) ?: ""
                    storyDateTimeHomeImageView.text = startDate
                }
            }
        }
    }

    inner class PromotionContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick.invoke(items[adapterPosition])
                }
            }

            itemView.promotionReadMoreButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick.invoke(items[adapterPosition])
                }
            }
        }

        fun bind(promotionContent: PromotionContent) {
            itemView.apply {
                promotionTitleHomeImageView.text = promotionContent.title
                promotionContent.imageUrl?.let {
                    promotionImageHomeImageView.loadImage(imageUrl = it)
                }
                val startDate =
                    promotionContent.updateDate?.convertToDateFromUTCTime(
                        DATE_TIME_FORMAT_YYYY_MM_DD_HH_MM_SS,
                        DATE_TIME_FORMAT_DD_MMM_YYYY
                    )
                promotionDateTimeHomeTextView.text = startDate
            }
        }
    }

    inner class FeaturedContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick.invoke(items[adapterPosition])
                }
            }
        }

        fun bind(featuredContent: FeaturedContent) {
            itemView.apply {
                featuredTitleHomeImageView.text = featuredContent.title
                featuredContent.imageUrl?.let {
                    featuredImageHomeImageView.loadImage(imageUrl = it)
                }
            }
        }
    }

    inner class AnnoucementContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick.invoke(items[adapterPosition])
                }
            }

            itemView.moreDetailsButton.setOnClickListener {
                onItemClick.invoke(items[adapterPosition])
            }
        }

        fun bind(annoucementContent: AnnoucementContent) {
            itemView.apply {
                titleTextView.text = annoucementContent.title
                annoucementContent.imageUrl?.let {
                    iconImageView.loadImage(imageUrl = it)
                }
            }
        }
    }
}