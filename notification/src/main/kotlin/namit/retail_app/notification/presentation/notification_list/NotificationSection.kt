package namit.retail_app.notification.presentation.notification_list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.notification.R
import namit.retail_app.notification.data.entity.NotificationContent
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters

class NotificationSection(
    val title: String,
    val notiList: List<NotificationContent>
) : Section(
    SectionParameters.builder()
        .itemResourceId(R.layout.item_notification_content)
        .headerResourceId(R.layout.item_notification_header)
        .build()
) {

    var onItemSelected: () -> Unit = {}
    var onReorderClicked: () -> Unit = {}

    override fun getItemViewHolder(view: View?): RecyclerView.ViewHolder {
        return NotificationItemViewHolder(view!!, onItemSelected, onReorderClicked)
    }

    override fun getHeaderViewHolder(view: View?): RecyclerView.ViewHolder {
        return NotificationHeaderViewHolder(view!!)
    }

    override fun getContentItemsTotal(): Int {
        return notiList.size
    }

    override fun onBindItemViewHolder(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        (viewHolder as? NotificationItemViewHolder)?.bind(notiList[position])
    }

    override fun onBindHeaderViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        (viewHolder as? NotificationHeaderViewHolder)?.bind(title)
    }

}