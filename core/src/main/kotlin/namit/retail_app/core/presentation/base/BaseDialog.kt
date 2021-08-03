package namit.retail_app.core.presentation.base

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment

open class BaseDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val displayRectangle = Rect()
        val window = activity?.window
        window?.decorView?.getWindowVisibleDisplayFrame(displayRectangle)
        dialog?.window?.apply {
            val windowLayoutParams = WindowManager.LayoutParams()
            windowLayoutParams.copyFrom(attributes)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}