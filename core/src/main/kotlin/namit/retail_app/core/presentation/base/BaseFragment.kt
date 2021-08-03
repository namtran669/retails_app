package namit.retail_app.core.presentation.base

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment: Fragment() {

    open val TAG: String? = null

    protected fun showSnackBar(message: String, duration: Int) {
        view?.let {
            Snackbar.make(it, message, duration).show()
        }
    }
}