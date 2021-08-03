package namit.retail_app.story.presentation.details

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.BaseStoryContent
import namit.retail_app.core.enums.StoryContentType
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.story.data.entity.AnnoucementContent
import namit.retail_app.story.data.entity.PromotionContent
import namit.retail_app.story.data.entity.StoryContent

class StoryDetailDialogViewModel(
    private val baseStoryContent: BaseStoryContent,
    private val eventTrackingManager: EventTrackingManager
): BaseViewModel() {
    val showTitle = MutableLiveData<String>()
    val hideTitle = MutableLiveData<String>()
    val showActionButton = MutableLiveData<Unit>()
    val renderImageInCenter = MutableLiveData<Boolean>()
    val storyImage = MutableLiveData<String>()
    val title = MutableLiveData<String>()
    val details = MutableLiveData<String>()

    fun showTitle() {
        showTitle.value = baseStoryContent.title
    }

    fun hideTitle() {
        showTitle.value = ""
    }

    fun renderStoryDetail() {
        baseStoryContent.imageUrl?.let {
            storyImage.value = it
        }

        baseStoryContent.title?.let {
            title.value = it
        }

        when (baseStoryContent) {
            is PromotionContent -> {
                eventTrackingManager.trackCMSView(StoryContentType.PROMOTION)
                details.value = baseStoryContent.body
            }
            is StoryContent -> {
                eventTrackingManager.trackCMSView(StoryContentType.STORY)
                details.value = baseStoryContent.article
            }
            is AnnoucementContent -> {
                eventTrackingManager.trackCMSView(StoryContentType.ANNOUCEMENT)
                details.value = baseStoryContent.annoucement
                renderImageInCenter.value = true
            }
        }
    }
}