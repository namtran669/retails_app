package namit.retail_app.grocery.presentation.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import namit.retail_app.core.extension.getStyledAttributes
import namit.retail_app.core.extension.loadCircleImage
import namit.retail_app.grocery.R
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.item_breadcrumb_sub_category.view.*
import kotlinx.android.synthetic.main.toolbar_grocery_category.view.*


class GroceryCategoryToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    View.OnClickListener {

    private var toolbarBackground = Color.WHITE
    var onBackPress: () -> Unit = {}

    init {
        View.inflate(context, R.layout.toolbar_grocery_category, this)

        context.getStyledAttributes(attrs, R.styleable.GroceryCategoryToolbar, defStyleAttr, 0) {
            val backgroundDrawable =
                getResourceId(R.styleable.GroceryCategoryToolbar_toolbarBackground, 0)
            if (backgroundDrawable != 0) {
                toolbarBackground = backgroundDrawable
            }

            val backgroundColor = getColor(R.styleable.GroceryCategoryToolbar_toolbarBackground, 0)
            if (backgroundColor != 0) {
                toolbarBackground = backgroundColor
            }
        }

        setBackgroundColor(toolbarBackground)
        backClickableView.setOnClickListener(this)
    }

    companion object {
        const val TAG = "GroceryCategoryToolbar"
    }

    fun setScreenTitle(title: String) {
        screenTitleTextView.text = title
    }

    fun setToolbarIcon(url: String) {
        categoryImageView.loadCircleImage(imageUrl = url)
    }

    fun setToolbarIcon(drawable: Drawable) {
        categoryImageView.setImageDrawable(drawable)
    }


    fun setFirstChildCategory(name: String) {
        firstChildCategoryTextView.visibility = View.VISIBLE
        firstChildCategoryTextView.text = name
    }

    fun setChildCategory(name: String) {
        breadcrumbFlexboxLayout.addView(createSubCategoryView(name))
    }

    fun handleBreadcrumbCategory(dataList: List<String>) {
        for ((index, data) in dataList.withIndex()) {
            if (index == 0) {
                setFirstChildCategory(data)
            } else {
                setChildCategory(data)
            }
        }
    }

    private fun createSubCategoryView(name: String): View {
        val layoutParams: FlexboxLayout.LayoutParams =
            FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.MATCH_PARENT
            )
        val inflater =
            this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val subCategoryView = inflater.inflate(R.layout.item_breadcrumb_sub_category, null)
        layoutParams.setMargins(
            0,
            resources.getDimensionPixelSize(R.dimen.categoryBreadcrumbMarginTop),
            0,
            0
        )
        subCategoryView.layoutParams = layoutParams
        subCategoryView.subCategoryTextView.text = name
        return subCategoryView
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backClickableView -> {
                onBackPress()
            }

            else -> return
        }
    }


}