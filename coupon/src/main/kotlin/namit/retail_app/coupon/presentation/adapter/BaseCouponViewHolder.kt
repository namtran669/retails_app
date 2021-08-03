package namit.retail_app.coupon.presentation.adapter

import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit

open class BaseCouponViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var couponTimer: CountDownTimer? = null

    protected fun startFlashDealTimer(endTime: Long, textView: TextView) {
        couponTimer?.cancel()
        couponTimer = object : CountDownTimer(endTime, 500) {
            override fun onFinish() {}

            override fun onTick(millisUntilFinished: Long) {
                val timeDiff = millisUntilFinished - System.currentTimeMillis()
                if (timeDiff > 0) {
                    textView.text = String.format(
                        "%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(timeDiff),
                        TimeUnit.MILLISECONDS.toMinutes(timeDiff) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(timeDiff) % TimeUnit.MINUTES.toSeconds(1)
                    )
                }
            }
        }.start()
    }
}