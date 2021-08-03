package namit.retail_app.core.presentation.widget
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import namit.retail_app.core.R
import namit.retail_app.core.extension.gone
import namit.retail_app.core.extension.visible
import namit.retail_app.core.presentation.base.BaseFullScreenDialog
import kotlinx.android.synthetic.main.dialog_loading.*

class LoadingDialog : BaseFullScreenDialog() {
    companion object {
        val TAG: String = LoadingDialog::class.java.simpleName
        private const val ARG_HAVE_BLUR_BACKGROUND = "ARG_IS_BLUR_BACKGROUND"
        fun newInstance(haveBlurBackground: Boolean): LoadingDialog {
            val fragment = LoadingDialog()
            fragment.arguments = Bundle().apply {
                putBoolean(ARG_HAVE_BLUR_BACKGROUND, haveBlurBackground)
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
    }

    override fun onStart() {
        super.onStart()
        
        val backgroundColorRes: Int
        if (arguments?.getBoolean(ARG_HAVE_BLUR_BACKGROUND) == true) {
            backgroundColorRes = R.color.white70
            loadingTextView.gone()
        } else {
            backgroundColorRes = android.R.color.white
            loadingTextView.visible()
        }

        dialog?.window?.apply {
            setWindowAnimations(R.style.TranslateFadeInFadeOutAnimation)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setBackgroundDrawableResource(backgroundColorRes)
        }
    }
}