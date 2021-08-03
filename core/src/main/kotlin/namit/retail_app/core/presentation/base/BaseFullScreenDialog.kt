package namit.retail_app.core.presentation.base

import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import namit.retail_app.core.R
import namit.retail_app.core.presentation.dialog.alert.AlertMessageDialog
import com.google.android.material.snackbar.Snackbar

open class BaseFullScreenDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog.window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setWindowAnimations(R.style.TranslateUpDownAnimation)
            val windowLayoutParams = WindowManager.LayoutParams()
            windowLayoutParams.copyFrom(attributes)
            setBackgroundDrawableResource(android.R.color.transparent)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val display = activity!!.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            setLayout(width, height)
        }
        val displayRectangle = Rect()
        val window = activity?.window
        window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)
    }

    protected fun alertNetworkConnectionErrorDialog() {
        activity?.supportFragmentManager?.let {
            AlertMessageDialog.newInstance(
                title = getString(R.string.hello),
                message = getString(R.string.alert_message_network_connection),
                buttonText = getString(R.string.dismiss)
            ).show(it, AlertMessageDialog.TAG)
        }
    }

    protected fun showSnackBar(message: String, duration: Int) {
        view?.let {
            Snackbar.make(it, message, duration).show()
        }
    }
}