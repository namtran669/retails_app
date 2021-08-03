package namit.retail_app.story.data.entity

import namit.retail_app.core.data.entity.BaseShelf
import namit.retail_app.core.data.entity.BaseStoryContent

class StoryShelf: BaseShelf() {
    var contentList: List<BaseStoryContent> = listOf()
}