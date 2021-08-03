package namit.retail_app.story.data.entity

import android.os.Parcelable
import namit.retail_app.core.data.entity.BaseStoryContent
import kotlinx.android.parcel.Parcelize

@Parcelize
class StoryContent(override var id: String? = null,
                   override var title: String? = null,
                   override var imageUrl: String? = null,
                   override var createDate: String? = null,
                   override var updateDate: String? = null,
                   var article: String? = null,
                   var description: String? = null,
                   var category: String? = null,
                   var position: Int = 0) : BaseStoryContent(), Parcelable