package namit.retail_app.core.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText

class PhoneNumberFormatter(
    private var editText: EditText,
    private val onTextChanged: (textLength: Int) -> Unit = {}
) : TextWatcher {

    companion object {
        private const val TAG = "PhoneNumberFormatter"
    }

    private val DASH: String = " - "

    override fun afterTextChanged(s: Editable?) {
        onTextChanged.invoke(s?.length ?: 0)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        try {
            val text = editText.text.toString()

            if (text.isBlank() || (text.isNotBlank() && text.length == 16)) {
                return
            }

            var resultText: String

            val sb = StringBuilder(text)

            if (!text[text.length - 1].isDigit()
                && text[text.length - 1] != ' '
                && text[text.length - 1] != '-'
            ) {
                sb.deleteCharAt(sb.length - 1)
                resultText = sb.toString().trim()
            } else {
                if (text.length >= 4 && text[3] != ' ') {
                    sb.insert(3, DASH)
                }

                if (text.length >= 10 && text[9] != ' ') {
                    sb.insert(9, DASH)
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
        } catch (e: IndexOutOfBoundsException) {
            Log.e(TAG, "IndexOutOfBoundsException", e)
        }
    }
}