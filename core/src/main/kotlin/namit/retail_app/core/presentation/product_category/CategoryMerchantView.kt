package namit.retail_app.core.presentation.product_category

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.extension.getStyledAttributes
import kotlinx.android.synthetic.main.view_product_category.view.*

class CategoryMerchantView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    View.OnClickListener {

    companion object {
        const val TAG = "CategoryMerchantView"
    }

    private var toolbarBackground = Color.WHITE
    private var onActionListener: OnActionListener? = null
    var categoryAdapter: CategoryMerchantAdapter? = null

    init {
        View.inflate(context, R.layout.view_product_category, this)

        context.getStyledAttributes(attrs, R.styleable.ProductCategory, defStyleAttr, 0) {
            val backgroundDrawable =
                getResourceId(R.styleable.ProductCategory_categoryBackground, 0)
            if (backgroundDrawable != 0) {
                toolbarBackground = backgroundDrawable
            }

            val backgroundColor = getColor(R.styleable.ProductCategory_categoryBackground, 0)
            if (backgroundColor != 0) {
                toolbarBackground = backgroundColor
            }
        }

        setBackgroundColor(toolbarBackground)
        categorySeeMoreHomeButton.setOnClickListener(this)

        //Init category list
        initCategoriesView()
    }

    private fun initCategoriesView() {

        categoryAdapter = CategoryMerchantAdapter()
        categoryAdapter?.setActionListener(object : CategoryMerchantAdapter.OnActionListener {
            override fun onItemSelect(category: CategoryItem) {
                onActionListener?.onItemSelected(category)
            }

        })
        categoryListRecyclerView.apply {
            adapter = categoryAdapter
            isNestedScrollingEnabled = false
        }
    }

    fun setActionListener(onAction: OnActionListener) {
        this.onActionListener = onAction
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.categorySeeMoreHomeButton -> {
                onActionListener?.onSeeAllPressed()
            }

            else -> return
        }
    }

    interface OnActionListener {
        fun onItemSelected(category: CategoryItem)
        fun onSeeAllPressed()
    }

}