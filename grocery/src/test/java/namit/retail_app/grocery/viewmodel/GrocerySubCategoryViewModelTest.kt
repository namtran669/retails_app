package namit.retail_app.grocery.viewmodel

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.grocery.data.domain.GetMerchantSubCategoryUseCase
import namit.retail_app.grocery.data.domain.GetMerchantSubCategoryUseCaseImpl
import namit.retail_app.grocery.presentation.category_sub.GrocerySubCategoryViewModel
import namit.retail_app.testutils.BaseViewModelTest
import namit.retail_app.testutils.TestObserver
import namit.retail_app.testutils.testObserver
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class GrocerySubCategoryViewModelTest : BaseViewModelTest() {
    private lateinit var rootCategory: TestObserver<CategoryItem>
    private lateinit var childCategoryList: TestObserver<List<CategoryItem>>
    private lateinit var openOtherSubCategory: TestObserver<Pair<CategoryItem, List<CategoryItem>>>
    private lateinit var openSubCategoryDetail: TestObserver<Pair<CategoryItem, CategoryItem>>

    private val getSubCategoryListUseCase: GetMerchantSubCategoryUseCase = mock()
    private val eventTrackingManager: EventTrackingManager = mock()

    private lateinit var viewModel: GrocerySubCategoryViewModel

    private val rootCategoryData = CategoryItem(nameEn = "Root", nameTh = "Root", id = 1, merchantId = "1")
    private val childCategoryListData = mutableListOf<CategoryItem>()

    override fun setup() {
        super.setup()

        val category1 = CategoryItem(nameEn = "Banana", nameTh = "Apple", id = 2)
        val category2 = CategoryItem(nameEn = "Orange", nameTh = "CP Mart", id = 3)

        childCategoryListData.apply {
            add(category1)
            add(category2)
        }

        viewModel = GrocerySubCategoryViewModel(
            rootCategoryData = rootCategoryData,
            childCategoryData = childCategoryListData,
            getSubCategoryListUseCase = getSubCategoryListUseCase,
            eventTrackingManager = eventTrackingManager
        )
        initTestObserver()
    }

    private fun initTestObserver() {
        rootCategory = viewModel.rootCategory.testObserver()
        childCategoryList = viewModel.childCategoryList.testObserver()
        openOtherSubCategory = viewModel.openOtherSubCategory.testObserver()
        openSubCategoryDetail = viewModel.openSubCategoryDetail.testObserver()
    }

    @Test
    fun checkSubCategory_has_sub() = runBlocking {
        val responseCategoryList = mutableListOf<CategoryItem>()
        val category1 = CategoryItem(nameEn = "Sub Cate 1", nameTh = "Sub Cate 1", id = 4)
        val category2 = CategoryItem(nameEn = "Sub Cate 2", nameTh = "Sub Cate 2", id = 5)

        responseCategoryList.apply {
            add(category1)
            add(category2)
        }
        whenever(getSubCategoryListUseCase.execute(rootCategoryData.merchantId, childCategoryListData[0].id)).thenReturn(
            UseCaseResult.Success(
                responseCategoryList
            )
        )
        viewModel.checkSubCategory(childCategoryListData[0])

        assert(openOtherSubCategory.observedValues.isNotEmpty())

        //other
        assert(openSubCategoryDetail.observedValues.isEmpty())
    }

    @Test
    fun checkSubCategory_empty_sub() = runBlocking {
        whenever(getSubCategoryListUseCase.execute(rootCategoryData.merchantId, childCategoryListData[0].id)).thenReturn(
            UseCaseResult.Error(
                Throwable(
                    message = GetMerchantSubCategoryUseCaseImpl.ERROR_EMPTY_SUB_CATEGORY_CASE
                )
            )
        )
        viewModel.checkSubCategory(childCategoryListData[0])

        assert(openSubCategoryDetail.observedValues.isNotEmpty())

        //other
        assert(openOtherSubCategory.observedValues.isEmpty())
    }

}
