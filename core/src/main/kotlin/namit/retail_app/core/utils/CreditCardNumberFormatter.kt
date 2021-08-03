package namit.retail_app.core.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class CreditCardNumberFormatter(
    private var editText: EditText,
    private val onTextChanged: (textLength: Int) -> Unit = {}
) : TextWatcher {

    private val SPACE: String = " "

    override fun afterTextChanged(s: Editable?) {
        onTextChanged.invoke(s?.length ?: 0)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val text = editText.text.toString()

        if (text.isBlank() || (text.isNotBlank() && text.length == 19)) {
            return
        }

        var resultText: String

        val sb = StringBuilder(text)

        if (!text[text.length - 1].isDigit()
            && text[text.length - 1] != ' ') {
            sb.deleteCharAt(sb.length - 1)
            resultText = sb.toString().trim()
        } else {
            if (text.length >= 5 && text[4] != ' ') {
                sb.insert(4, SPACE)
            }

            if (text.length >= 10 && text[9] != ' ') {
                sb.insert(9, SPACE)
            }

            if (text.length >= 15 && text[14] != ' ') {
                sb.insert(14, SPACE)
            }

            //Clear trailing space and dash
            resultText = sb.toString().trim()

            if (resultText[resultText.length - 1] == '-') {
                resultText = resultText.substring(0, resultText.length - 2).trim()
            }
        }

        editText.removeTextChangedListener(this)
        editText.setText(resultText)
        editText.setSelection(resultText.length)
        editText.addTextChangedListener(this)
    }

}