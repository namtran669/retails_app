package namit.retail_app.notification.presentation.notification_list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import namit.retail_app.notification.R
import kotlinx.android.synthetic.main.item_notification_header.view.*

class NotificationHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    init {

    }

    fun bind(title: String) {
        //todo test UI
        itemView.apply {
            headerTitleTextView.text = title
            findViewById<TextView>(R.id.headerTitleTextView)
        }
    }

}