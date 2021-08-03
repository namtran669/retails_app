package namit.retail_app.core.presentation.dialog.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import namit.retail_app.core.R
import namit.retail_app.core.presentation.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_question.*

class QuestionDialog : BaseDialog() {

    companion object {
        const val TAG = "QuestionDialog"
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_MESSAGE = "ARG_MESSAGE"
        private const val ARG_NEGATIVE_BUTTON = "ARG_NEGATIVE_BUTTON"
        private const val ARG_POSITIVE_BUTTON = "ARG_POSITIVE_BUTTON"

        fun newInstance(
            title: String,
            message: String,
            negativeButtonText: String,
            positiveButtonText: String
        ): QuestionDialog {
            val fragment = QuestionDialog()
            fragment.arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
                putString(ARG_NEGATIVE_BUTTON, negativeButtonText)
                putString(ARG_POSITIVE_BUTTON, positiveButtonText)
            }
            return fragment
        }
    }

    private var title: String = ""
    private var message: String = ""
    private var negativeButtonText: String = ""
    private var positiveButtonText: String = ""

    var onPositionClick:() -> Unit = {}
    var onNegativeClick:() -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = arguments?.getString(ARG_TITLE, "") ?: ""
        message = arguments?.getString(ARG_MESSAGE, "") ?: ""
        negativeButtonText = arguments?.getString(ARG_NEGATIVE_BUTTON, "") ?: ""
        positiveButtonText = arguments?.getString(ARG_POSITIVE_BUTTON, "") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_question, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTextView.text = title
        messageTextView.text = message
        negativeButton.text = negativeButtonText
        positiveButton.text = positiveButtonText

        negativeButton.setOnClickListener {
            onNegativeClick.invoke()
            dismiss()
        }

        positiveButton.setOnClickListener {
            onPositionClick.invoke()
            dismiss()
        }
    }
}