package namit.retail_app.core.navigation

import androidx.fragment.app.DialogFragment
import namit.retail_app.core.data.entity.BaseStoryContent

interface StoryNavigator {
    fun getStoryDetail(baseStoryContent: BaseStoryContent): DialogFragment
}