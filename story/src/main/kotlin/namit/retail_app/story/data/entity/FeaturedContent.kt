package namit.retail_app.story.data.entity

import android.os.Parcel
import android.os.Parcelable
import namit.retail_app.core.data.entity.BaseStoryContent

class FeaturedContent(override var id: String? = null,
                      override var title: String? = null,
                      override var imageUrl: String? = null,
                      override var createDate: String? = null,
                      override var updateDate: String? = null) : BaseStoryContent(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeaturedContent> {
        override fun createFromParcel(parcel: Parcel): FeaturedContent {
            return FeaturedContent(parcel)
        }

        override fun newArray(size: Int): Array<FeaturedContent?> {
            return arrayOfNulls(size)
        }
    }

}