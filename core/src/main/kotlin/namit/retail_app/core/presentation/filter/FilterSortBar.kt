package namit.retail_app.core.presentation.filter

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import namit.retail_app.core.R
import namit.retail_app.core.data.entity.FilterModel
import namit.retail_app.core.data.entity.SortModel
import namit.retail_app.core.extension.getStyledAttributes
import namit.retail_app.core.presentation.adapter.FilterProductAdapter
import namit.retail_app.core.presentation.adapter.SortProductAdapter
import kotlinx.android.synthetic.main.view_filter_sort.view.*

class FilterSortBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    View.OnClickListener {

    companion object {
        const val TAG = "FilterSortBar"
    }

    private var barBackground = Color.TRANSPARENT
    private var isFilterFocused: Boolean = false
    private var isSortFocused: Boolean = false

    private var filterAdapter: FilterProductAdapter
    private var sortAdapter: SortProductAdapter

    var onFilterChecked: (index: Int) -> Unit = {}
    var onFilterApply: () -> Unit = {}
    var onSortSelected: (index: Int) -> Unit = {}

    init {
        View.inflate(context, R.layout.view_filter_sort, this)

        context.getStyledAttributes(attrs, R.styleable.FilterSortBar, defStyleAttr, 0) {
            val backgroundDrawable = getResourceId(R.styleable.FilterSortBar_barBackground, 0)
            if (backgroundDrawable != 0) {
                barBackground = backgroundDrawable
            }

            val backgroundColor = getColor(R.styleable.FilterSortBar_barBackground, 0)
            if (backgroundColor != 0) {
                barBackground = backgroundColor
            }
        }

        headerLayout.setBackgroundColor(barBackground)
        filterImageView.setOnClickListener(this)
        filterTextView.setOnClickListener(this)
        sortImageView.setOnClickListener(this)
        sortTextView.setOnClickListener(this)
        outSideFilterSortContentView.setOnClickListener(this)
        filterApplyButton.setOnClickListener(this)

        //Filter
        filterAdapter = FilterProductAdapter()
        filterOptionRecyclerView.adapter = filterAdapter
        filterAdapter.onItemChecked = {
            onFilterChecked.invoke(it)
        }

        //Sort
        sortAdapter = SortProductAdapter()
        sortOptionRecyclerView.adapter = sortAdapter
        sortAdapter.onItemSelected = {
            toggleSortButton()
            onSortSelected.invoke(it)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.filterImageView -> {
                toggleFilterButton()
            }

            R.id.filterTextView -> {
                toggleFilterButton()
            }

            R.id.sortTextView -> {
                toggleSortButton()
            }

            R.id.sortImageView -> {
                toggleSortButton()
            }

            R.id.outSideFilterSortContentView -> {
                hideFilterSortOption()
            }

            R.id.filterApplyButton -> {
                toggleFilterButton()
                onFilterApply()
            }

            else -> return
        }
    }

    fun isHasFocus(): Boolean {
        return isFilterFocused || isSortFocused
    }

    fun hideFilterSortOption() {
        isFilterFocused = false
        isSortFocused = false
        handleFilterButton(isFilterFocused)
        handleSortButton(isSortFocused)
    }

    fun setFilterList(filterList: List<FilterModel>) {
        filterAdapter.items = filterList
    }

    fun setSortList(sortList: List<SortModel>) {
        sortAdapter.items = sortList
    }

    fun setSortTitle(title: String) {
        sortTextView.text = title
    }

    private fun toggleFilterButton() {
        isFilterFocused = !isFilterFocused
        if (isFilterFocused) {
            isSortFocused = false
            handleSortButton(isSortFocused)
        }
        handleFilterButton(isFilterFocused)
    }

    private fun toggleSortButton() {
        isSortFocused = !isSortFocused
        if (isSortFocused) {
            isFilterFocused = false
            handleFilterButton(isFilterFocused)
        }
        handleSortButton(isSortFocused)
    }

    private fun handleFilterButton(isFocus: Boolean) {
        if (isFocus) {
            filterImageView.setImageResource(R.drawable.ic_details_selected)
            filterTextView.setTextColor(ContextCompat.getColor(this.context, R.color.dodgerBlue))
            outSideFilterSortContentView.visibility = View.VISIBLE
            filterLayout.visibility = View.VISIBLE
            sortLayout.visibility = View.GONE

        } else {
            filterImageView.setImageResource(R.drawable.ic_details)
            filterTextView.setTextColor(ContextCompat.getColor(this.context, R.color.trout))
            outSideFilterSortContentView.visibility = View.GONE
            filterLayout.visibility = View.GONE
        }
    }

    private fun handleSortButton(isFocus: Boolean) {
        if (isFocus) {
            sortImageView.setImageResource(R.drawable.ic_more_selected)
            sortTextView.setTextColor(ContextCompat.getColor(this.context, R.color.dodgerBlue))
            outSideFilterSortContentView.visibility = View.VISIBLE
            sortLayout.visibility = View.VISIBLE
            filterLayout.visibility = View.GONE
        } else {
            sortImageView.setImageResource(R.drawable.ic_more)
            sortTextView.setTextColor(ContextCompat.getColor(this.context, R.color.trout))
            outSideFilterSortContentView.visibility = View.GONE
            sortLayout.visibility = View.GONE
        }
    }
}