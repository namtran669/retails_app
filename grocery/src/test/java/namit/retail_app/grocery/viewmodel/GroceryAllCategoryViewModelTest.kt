package namit.retail_app.grocery.viewmodel

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.domain.GetMerchantCategoryUseCase
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.grocery.data.domain.GetMerchantSubCategoryUseCase
import namit.retail_app.grocery.data.domain.GetMerchantSubCategoryUseCaseImpl
import namit.retail_app.grocery.presentation.category_all.GroceryAllCategoryViewModel
import namit.retail_app.testutils.BaseViewModelTest
import namit.retail_app.testutils.TestObserver
import namit.retail_app.testutils.testObserver
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class GroceryAllCategoryViewModelTest : BaseViewModelTest() {
    private lateinit var categoryList: TestObserver<List<CategoryItem>>
    private lateinit var openSubCategory: TestObserver<Pair<CategoryItem, List<CategoryItem>>>
    private lateinit var openSubCategoryDetail: TestObserver<CategoryItem>

    private val getMerchantCategoryUseCase: GetMerchantCategoryUseCase = mock()
    private val getSubCategoryListUseCase: GetMerchantSubCategoryUseCase = mock()

    private lateinit var viewModel: GroceryAllCategoryViewModel

    private val merchantInfoItem = MerchantInfoItem().apply {
        id = "1"
    }
    private val dummyCategoryList = mutableListOf<CategoryItem>()

    override fun setup() {
        super.setup()

        val category1 = CategoryItem(nameEn = "Banana", nameTh = "Apple", id = 1)
        val category2 = CategoryItem(nameEn = "Orange", nameTh = "CP Mart", id = 2)

        dummyCategoryList.apply {
            add(category1)
            add(category2)
        }

        viewModel = GroceryAllCategoryViewModel(
            merchantInfoItem = merchantInfoItem,
            getCategoryListUseCase = getMerchantCategoryUseCase,
            getSubCategoryListUseCase = getSubCategoryListUseCase
        )
        initTestObserver()
    }

    private fun initTestObserver() {
        categoryList = viewModel.categoryList.testObserver()
        openSubCategory = viewModel.openSubCategory.testObserver()
        openSubCategoryDetail = viewModel.openSubCategoryDetail.testObserver()
    }

    @Test
    fun checkSubCategory_has_sub() = runBlocking {
        val responseCategoryList = mutableListOf<CategoryItem>()
        val category1 = CategoryItem(nameEn = "Sub Cate 1", nameTh = "Sub Cate 1", id = 1)
        val category2 = CategoryItem(nameEn = "Sub Cate 2", nameTh = "Sub Cate 2", id = 2)

        responseCategoryList.apply {
            add(category1)
            add(category2)
        }
        whenever(getSubCategoryListUseCase.execute(merchantInfoItem.id, dummyCategoryList[0].id)).thenReturn(
            UseCaseResult.Success(
                responseCategoryList
            )
        )
        viewModel.checkSubCategory(dummyCategoryList[0])

        assert(openSubCategory.observedValues.isNotEmpty())

        //other
        assert(openSubCategoryDetail.observedValues.isEmpty())
    }

    @Test
    fun checkSubCategory_empty_sub() = runBlocking {
        whenever(getSubCategoryListUseCase.execute(any(), any())).thenReturn(
            UseCaseResult.Error(
                Throwable(
                message = GetMerchantSubCategoryUseCaseImpl.ERROR_EMPTY_SUB_CATEGORY_CASE
            ))
        )
        viewModel.checkSubCategory(dummyCategoryList[0])

        assert(openSubCategoryDetail.observedValues.isNotEmpty())

        //other
        assert(openSubCategory.observedValues.isEmpty())
    }

}
