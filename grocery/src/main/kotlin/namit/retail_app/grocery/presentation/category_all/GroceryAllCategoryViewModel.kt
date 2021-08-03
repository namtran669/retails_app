package namit.retail_app.grocery.presentation.category_all

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.domain.GetMerchantCategoryUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.grocery.data.domain.GetMerchantSubCategoryUseCase
import namit.retail_app.grocery.data.domain.GetMerchantSubCategoryUseCaseImpl.Companion.ERROR_EMPTY_SUB_CATEGORY_CASE
import kotlinx.coroutines.launch

class GroceryAllCategoryViewModel(
    private val merchantInfoItem: MerchantInfoItem,
    private val getCategoryListUseCase: GetMerchantCategoryUseCase,
    private val getSubCategoryListUseCase: GetMerchantSubCategoryUseCase
) : BaseViewModel() {

    val categoryList = MutableLiveData<List<CategoryItem>>()
    var openAllCategory = SingleLiveEvent<Pair<MerchantInfoItem, List<CategoryItem>>>()
    val openCategoryDetail = SingleLiveEvent<Pair<CategoryItem, CategoryItem>>()
    val openSubCategory = SingleLiveEvent<Pair<CategoryItem, List<CategoryItem>>>()
    val openSubCategoryDetail = SingleLiveEvent<CategoryItem>()

    private val allCategoryList = mutableListOf<CategoryItem>()

    fun presentAllCategory() {
        categoryList.value?.let {
            openAllCategory.value = Pair(first = merchantInfoItem, second = it)
        }
    }

    fun loadAllCategoryList() {
        launch {
            val categoryListResult = getCategoryListUseCase.execute(merchantInfoItem.id)
            if (categoryListResult is UseCaseResult.Success) {
                allCategoryList.addAll(categoryListResult.data!!)
                categoryList.value = allCategoryList
            }
        }
    }

    fun presentCategoryDetail(selectCategory: CategoryItem) {
        openCategoryDetail.value = Pair(first = selectCategory, second = selectCategory)
    }

    //This case selected category is the root so we just init breadcrumb list and don't add it to breadcrumb list
    fun checkSubCategory(selectedCategoryData: CategoryItem) {
        launch {
            val result = getSubCategoryListUseCase.execute(merchantInfoItem.id, selectedCategoryData.id)
            if (result is UseCaseResult.Success) {
                result.data?.let {
                    selectedCategoryData.breadcrumbChildList = mutableListOf()
                    openSubCategory.value = Pair(first = selectedCategoryData, second = it)
                }
            } else if (result is UseCaseResult.Error && result.exception.message == ERROR_EMPTY_SUB_CATEGORY_CASE) {
                openSubCategoryDetail.value = selectedCategoryData
            }
        }
    }
}