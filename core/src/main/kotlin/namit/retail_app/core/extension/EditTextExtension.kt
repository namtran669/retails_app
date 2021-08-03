package namit.retail_app.core.extension

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.afterTextChanged(listener: (text: String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            listener.invoke(editable.toString())
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
}

abstract class AfterTextChangedWatcher: TextWatcher {
    abstract fun afterTextChangedListener(s: String)

    override fun afterTextChanged(s: Editable?) {
        afterTextChangedListener(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //Do nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //Do nothing
    }
}