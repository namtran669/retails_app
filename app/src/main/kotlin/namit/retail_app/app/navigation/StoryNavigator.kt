package namit.retail_app.app.navigation

import androidx.fragment.app.DialogFragment
import namit.retail_app.core.data.entity.BaseStoryContent
import namit.retail_app.core.navigation.StoryNavigator
import namit.retail_app.story.presentation.details.StoryDetailDialogFragment

class StoryNavigatorImpl : StoryNavigator {

    override fun getStoryDetail(baseStoryContent: BaseStoryContent): DialogFragment {
        return StoryDetailDialogFragment.newInstance(baseStoryContent = baseStoryContent)
    }

}