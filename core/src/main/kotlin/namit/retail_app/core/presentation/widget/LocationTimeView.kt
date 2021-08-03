package namit.retail_app.core.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import namit.retail_app.core.R
import kotlinx.android.synthetic.main.header_location_time.view.*

class LocationTimeView constructor(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.header_location_time, this, true)

        locationViewLinear.setOnClickListener {
            onLocationClick.invoke()
        }

        timeViewLinear.setOnClickListener {
            onTimeSlotClick.invoke()
        }
    }

    companion object {
        const val TAG = "LocationTimeView"
    }

    var onLocationClick: () -> Unit = {}
    var onTimeSlotClick: () -> Unit = {}

    fun setLocationTitle(title: String) {
        locationPickupTextView.text = title
    }

    fun setTimeSlot(time: String) {
        var timeText = time
        timeDetailTextView.text = timeText
        if (timeDetailTextView.lineCount > 1) {
            timeText = timeText.replace(",", "  ")
        }
        timeDetailTextView.text = timeText
    }
}