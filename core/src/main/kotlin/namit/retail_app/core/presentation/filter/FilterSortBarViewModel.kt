package namit.retail_app.core.presentation.filter

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.FilterModel
import namit.retail_app.core.data.entity.SortModel
import namit.retail_app.core.presentation.base.BaseViewModel

class FilterSortBarViewModel: BaseViewModel() {

    private val currentFilterList = mutableListOf<FilterModel>()
    private val currentSortList = mutableListOf<SortModel>()

    val filterList = MutableLiveData<List<FilterModel>>()
    val sortList = MutableLiveData<List<SortModel>>()
    val selectedSort = MutableLiveData<SortModel>()

    init {
        //Just mock data
        currentFilterList.addAll(dummyFilterData())
        currentSortList.addAll(dummySortData())
    }

    fun renderFilterList() {
        filterList.value = currentFilterList
    }

    fun renderSortList() {
        sortList.value = currentSortList
    }

    fun updateFilter(index: Int) {
        currentFilterList[index].isSelected = currentFilterList[index].isSelected.not()
        renderFilterList()
    }

    fun updateSort(checkedIndex: Int) {
        currentSortList.forEachIndexed { index, sortModel ->
            sortModel.isSelected = index == checkedIndex
        }
        selectedSort.value = currentSortList[checkedIndex]
        renderSortList()
    }

    private fun dummyFilterData(): List<FilterModel> {
        val filterList = mutableListOf<FilterModel>()
        filterList.add(FilterModel("New"))
        filterList.add(FilterModel("Merchant Offers"))
        filterList.add(FilterModel("Coupon Discount Applicablew"))
        filterList.add(FilterModel("Organic"))
        filterList.add(FilterModel("Frozen"))
        filterList.add(FilterModel("Microwaveable"))
        return filterList
    }

    private fun dummySortData(): List<SortModel> {
        val sortList = mutableListOf<SortModel>()
        sortList.add(SortModel("Price High-Low"))
        sortList.add(SortModel("New Product"))
        sortList.add(SortModel("Discount High-Low"))
        sortList.add(SortModel("Product Name A-Z"))
        sortList.add(SortModel("Product Name Z-A"))
        sortList.add(SortModel("Recently Bought"))
        sortList.add(SortModel("Shelf Life Long-Short"))
        return sortList
    }
}