package namit.retail_app.core.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class CreditCardExpireDateFormatter(
    private var editText: EditText,
    private val onTextChanged: (textLength: Int) -> Unit = {}
) : TextWatcher {

    private val SLASH: String = "/"

    override fun afterTextChanged(s: Editable?) {
        onTextChanged.invoke(s?.length ?: 0)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = editText.text.toString()

        if (text.isBlank() || (text.isNotBlank() && text.length == 5)) {
            return
        }

        val sb = StringBuilder(text)

        if (text.length >= 3 && text[2] != '/') {
            sb.insert(2, SLASH)
        }

        //Clear trailing space and dash
        val resultText = sb.toString().trim()

        editText.removeTextChangedListener(this)
        editText.setText(resultText)
        editText.setSelection(resultText.length)
        editText.addTextChangedListener(this)
    }

}