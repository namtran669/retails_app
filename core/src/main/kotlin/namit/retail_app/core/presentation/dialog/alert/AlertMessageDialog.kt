package namit.retail_app.core.presentation.dialog.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import namit.retail_app.core.R
import namit.retail_app.core.presentation.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_alert_message.*


class AlertMessageDialog : BaseDialog() {

    companion object {
        const val TAG = "AlertMessageDialog"
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_MESSAGE = "ARG_MESSAGE"
        private const val ARG_BUTTON = "ARG_BUTTON"

        fun newInstance(
            title: String,
            message: String,
            buttonText: String
        ): AlertMessageDialog {
            val fragment = AlertMessageDialog()
            fragment.arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
                putString(ARG_BUTTON, buttonText)
            }
            return fragment
        }
    }

    private var title: String = ""
    private var message: String = ""
    private var buttonText: String = ""

    var onDismiss:() -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = arguments?.getString(ARG_TITLE, "") ?: ""
        message = arguments?.getString(ARG_MESSAGE, "") ?: ""
        buttonText = arguments?.getString(ARG_BUTTON, "") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_alert_message, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTextView.text = title
        messageTextView.text = message
        dismissButton.text = buttonText
        dismissButton.setOnClickListener {
            onDismiss.invoke()
            dismiss()
        }
    }
}

