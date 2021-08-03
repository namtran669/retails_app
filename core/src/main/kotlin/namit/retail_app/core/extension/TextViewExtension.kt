package namit.retail_app.core.extension

import android.view.View
import android.widget.TextView

fun TextView.setContentGoneWhenBlank(content: String?) {
    if (content.isNullOrBlank()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        text = content
    }
}