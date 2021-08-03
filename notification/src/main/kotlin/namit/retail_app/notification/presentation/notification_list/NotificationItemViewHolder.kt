package namit.retail_app.notification.presentation.notification_list

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.notification.R
import namit.retail_app.notification.data.entity.NotificationContent
import namit.retail_app.notification.data.entity.NotificationState
import kotlinx.android.synthetic.main.item_notification_content.view.*

class NotificationItemViewHolder(
    view: View,
    selectAction: () -> Unit,
    reorderAction: () -> Unit
) : RecyclerView.ViewHolder(view) {
    init {
        view.notificationReorderTextView.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                reorderAction.invoke()
            }
        }

        view.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                selectAction.invoke()
            }
        }
    }

    fun bind(data: NotificationContent) {
        //todo test UI
        itemView.apply {

            if (data.type == NotificationState.ORDER) {
                moreIconImageView.visibility = View.GONE
                notificationReorderTextView.visibility = View.VISIBLE
                notificationOrderStatusTextView.visibility = View.VISIBLE

                notificationContentTextView.setSingleLine(false)
                notificationContentTextView.maxLines = 1
                notificationContentTextView.ellipsize = TextUtils.TruncateAt.END
            } else {
                moreIconImageView.visibility = View.VISIBLE
                notificationReorderTextView.visibility = View.GONE
                notificationOrderStatusTextView.visibility = View.GONE

                notificationContentTextView.setSingleLine(false)
                notificationContentTextView.maxLines = 2
                notificationContentTextView.ellipsize = TextUtils.TruncateAt.END
            }

            if (data.isUnSeen) {
                notificationNewIconView.visibility = View.VISIBLE
            } else {
                notificationNewIconView.visibility = View.GONE
            }

            //Fill data

            data.content?.let { notificationContentTextView.text = it }
            notificationTitleTextView.text = data.title
            notificationTimeTextView.text = data.createdAt
            data.orderStatus?.let { notificationOrderStatusTextView.text = it }
            if (data.type == NotificationState.ORDER) {
                notificationIconImageView.setImageResource(R.drawable.ic_order_noti)
            } else {
                notificationIconImageView.setImageResource(R.drawable.ic_announcement)
            }
        }
    }
}