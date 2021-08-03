package namit.retail_app.core.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import namit.retail_app.core.R
import namit.retail_app.core.presentation.food_detail.ProductOptionGroupAdapter
import com.eggdigital.trueyouedc.utils.ExpandCollapseAnimationUtils
import kotlinx.android.synthetic.main.view_customize_option_product.view.*

class OptionProductView constructor(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_customize_option_product, this, true)
    }

    companion object {
        val TAG = OptionProductView::class.java.simpleName
    }

    private var optionGroupAdapter: ProductOptionGroupAdapter? = null

    fun setOnClickListener(action: () -> Unit) {
        customizeLayout.setOnClickListener {
            action.invoke()
        }
    }

    fun setAdapterOption(optionAdapter: ProductOptionGroupAdapter) {
        optionGroupAdapter = optionAdapter
        optionGroupRecyclerView.apply {
            adapter = optionGroupAdapter
        }
    }

    fun setExpandDetailView() {
        ExpandCollapseAnimationUtils.expand(optionDetailsLayout)
        customizeTextView.setTextColor(ContextCompat.getColor(context, R.color.dodgerBlue))
        expandImageView.setImageResource(R.drawable.ic_more_selected)
    }

    fun setCollapseDetailView() {
        ExpandCollapseAnimationUtils.collapse(optionDetailsLayout)
        customizeTextView.setTextColor(ContextCompat.getColor(context, R.color.trout70))
        expandImageView.setImageResource(R.drawable.ic_more)
    }

    fun getOptionNote(): String {
        return specialNoteEditText.text.toString()
    }

    fun setOptionNote(note: String) {
        specialNoteEditText.setText(note)
    }
}

