package namit.retail_app.core.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import namit.retail_app.core.R
import namit.retail_app.core.extension.afterTextChanged
import namit.retail_app.core.utils.KeyboardUtil
import kotlinx.android.synthetic.main.header_search_view.view.*

class HeaderSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.header_search_view, this)
        setBackgroundResource(R.drawable.bg_shape_white_15_radius)
        searchEditText.afterTextChanged {
            onKeyWordsChange.invoke(it)
        }
    }

    companion object {
        const val TAG = "HeaderSearchView"
    }

    var onKeyWordsChange: (keyword: String) -> Unit = {}

    fun enableSearch(enable: Boolean) {
        if (enable) {
            searchEditText.visibility = View.VISIBLE
            searchTextView.visibility = View.GONE
        } else {
            searchEditText.visibility = View.GONE
            searchTextView.visibility = View.VISIBLE
        }
    }

    fun setHintContent(hint: String) {
        searchTextView.text = hint
        searchEditText.hint = hint
    }

    fun currentKeyword(): String {
        return searchEditText.text.toString()
    }

    fun clearText() {
        searchEditText.setText("")
    }

    fun showKeyboard(context: Context) {
        searchEditText.requestFocus()
        KeyboardUtil.show(context, searchEditText)
    }

    fun hideKeyboard(context: Context) {
        searchEditText.clearFocus()
        KeyboardUtil.hide(context, null)
    }
}