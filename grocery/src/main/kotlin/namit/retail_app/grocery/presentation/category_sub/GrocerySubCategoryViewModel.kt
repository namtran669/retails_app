package namit.retail_app.grocery.presentation.category_sub

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.grocery.data.domain.GetMerchantSubCategoryUseCase
import namit.retail_app.grocery.data.domain.GetMerchantSubCategoryUseCaseImpl.Companion.ERROR_EMPTY_SUB_CATEGORY_CASE
import kotlinx.coroutines.launch

class GrocerySubCategoryViewModel(
    private val rootCategoryData: CategoryItem,
    private val childCategoryData: MutableList<CategoryItem>,
    private val getSubCategoryListUseCase: GetMerchantSubCategoryUseCase,
    eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    val rootCategory = MutableLiveData<CategoryItem>()
    val childCategoryList = MutableLiveData<List<CategoryItem>>()
    val openOtherSubCategory = SingleLiveEvent<Pair<CategoryItem, List<CategoryItem>>>()
    val openSubCategoryDetail = SingleLiveEvent<Pair<CategoryItem, CategoryItem>>()

    private var currentCategoryNote = CategoryItem()

    init {
        rootCategory.value = rootCategoryData
        eventTrackingManager.trackCategory(
            merchantId = rootCategoryData.merchantId,
            merchantName = rootCategoryData.merchantName,
            categoryId = rootCategoryData.id,
            categoryName = rootCategoryData.nameEn
        )
    }

    fun renderSubcategoryList() {
        currentCategoryNote = if (!rootCategoryData.breadcrumbChildList!!.isNullOrEmpty()) {
            rootCategoryData.breadcrumbChildList!!.last()
        } else {
            rootCategoryData
        }
        
        childCategoryList.value = childCategoryData
    }

    //add selected category to breadcrumb of root to show on next screen
    fun checkSubCategory(selectedCategoryData: CategoryItem) {
        launch {
            if (selectedCategoryData.id == currentCategoryNote.id) {
                rootCategoryData.breadcrumbChildList?.add(selectedCategoryData)
                openSubCategoryDetail.value =
                    Pair(first = selectedCategoryData, second = rootCategoryData)
            } else {
                val result =
                    getSubCategoryListUseCase.execute(
                        rootCategoryData.merchantId,
                        selectedCategoryData.id
                    )
                if (result is UseCaseResult.Success) {
                    result.data?.let {
                        rootCategoryData.breadcrumbChildList?.add(selectedCategoryData)
                        openOtherSubCategory.value = Pair(first = rootCategoryData, second = it)
                    }
                } else if (result is UseCaseResult.Error && result.exception.message == ERROR_EMPTY_SUB_CATEGORY_CASE) {
                    rootCategoryData.breadcrumbChildList?.add(selectedCategoryData)
                    openSubCategoryDetail.value =
                        Pair(first = selectedCategoryData, second = rootCategoryData)
                }
            }
        }
    }

    private fun removeLastItemInBreadcrumb() {
        rootCategoryData.breadcrumbChildList?.let {
            if (it.size > 0) {
                it.removeAt(it.size - 1)
            }
        }
    }

    override fun onCleared() {
        removeLastItemInBreadcrumb()
        super.onCleared()
    }

}