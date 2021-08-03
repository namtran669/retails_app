package namit.retail_app.core.utils

import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


object KeyboardUtil {

    fun show(context: Context?) {
        context?.apply {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
        }
    }

    fun show(context: Context?, editText: EditText) {
        context?.apply {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hide(context: Context?, windowToken: IBinder?) {
        context?.apply {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            windowToken?.let {
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
        }
    }

}

