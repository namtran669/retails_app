package namit.retail_app.grocery.viewmodel

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.domain.GetProductByCategoryUseCase
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.grocery.presentation.category_sub_detail.GrocerySubCategoryDetailViewModel
import namit.retail_app.testutils.BaseViewModelTest
import namit.retail_app.testutils.TestObserver
import namit.retail_app.testutils.testObserver
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
class GrocerySubCategoryDetailViewModelTest : BaseViewModelTest() {
    private lateinit var rootCategory: TestObserver<CategoryItem>
    private lateinit var showHaveNoMoreProduct: TestObserver<Boolean>
    private lateinit var productList: TestObserver<MutableList<ProductItem>>

    private val getProductByCategoryUseCase: GetProductByCategoryUseCase = mock()

    private lateinit var viewModel: GrocerySubCategoryDetailViewModel

    private val rootCategoryData = CategoryItem(nameEn = "Root", nameTh = "Root", id = 1, merchantId = "1")
    private val selectedCategoryData = CategoryItem(nameEn = "Selected", nameTh = "Selected", id = 2, merchantId = "1")

    private val dummyProductPage1 = mutableListOf<ProductItem>()
    private val dummyProductPage2 = mutableListOf<ProductItem>()

    override fun setup() {
        super.setup()
        val product1 = ProductItem()

        //each page 10 item
        dummyProductPage1.apply {
            add(product1)
            add(product1)
            add(product1)
            add(product1)
            add(product1)
            add(product1)
            add(product1)
            add(product1)
            add(product1)
            add(product1)
        }

        dummyProductPage2.add(product1)

        viewModel = GrocerySubCategoryDetailViewModel(
            rootCategoryData = rootCategoryData,
            selectedCategoryData = selectedCategoryData,
            getProductByCategoryUseCase = getProductByCategoryUseCase
        )
        initTestObserver()
    }

    private fun initTestObserver() {
        rootCategory = viewModel.rootCategory.testObserver()
        showHaveNoMoreProduct = viewModel.showHaveNoMoreProduct.testObserver()
        productList = viewModel.productList.testObserver()
    }

    @Test
    fun loadProductList_success_page2_isEmpty() = runBlocking {
        whenever(getProductByCategoryUseCase.execute(merchantId = rootCategoryData.merchantId, page = 0, categoryIds = listOf(selectedCategoryData.id)))
            .thenReturn(UseCaseResult.Success(dummyProductPage1))

        whenever(getProductByCategoryUseCase.execute(merchantId = rootCategoryData.merchantId, page = 1, categoryIds = listOf(selectedCategoryData.id)))
            .thenReturn(UseCaseResult.Success(mutableListOf()))

        viewModel.loadProductList()

        Assert.assertEquals(2, productList.observedValues.size)
        // 6 skeleton item
        Assert.assertEquals(6, productList.observedValues[0]?.size)
        // 10 product + 2 skeleton item
        Assert.assertEquals(12, productList.observedValues[1]?.size)
        assert(showHaveNoMoreProduct.observedValues.isEmpty())


        viewModel.loadProductList()
        Assert.assertEquals(10, productList.observedValues[2]?.size)
        assert(showHaveNoMoreProduct.observedValues.isNotEmpty())
    }

    @Test
    fun loadProductList_success_page2_has_1_item() = runBlocking {
        whenever(getProductByCategoryUseCase.execute(merchantId = rootCategoryData.merchantId, page = 0, categoryIds = listOf(selectedCategoryData.id)))
            .thenReturn(UseCaseResult.Success(dummyProductPage1))

        whenever(getProductByCategoryUseCase.execute(merchantId = rootCategoryData.merchantId, page = 1, categoryIds = listOf(selectedCategoryData.id)))
            .thenReturn(UseCaseResult.Success(dummyProductPage2))

        viewModel.loadProductList()

        Assert.assertEquals(2, productList.observedValues.size)
        // 6 skeleton item
        Assert.assertEquals(6, productList.observedValues[0]?.size)
        // 10 product + 2 skeleton item
        Assert.assertEquals(12, productList.observedValues[1]?.size)
        assert(showHaveNoMoreProduct.observedValues.isEmpty())


        viewModel.loadProductList()
        Assert.assertEquals(11, productList.observedValues[2]?.size)
        assert(showHaveNoMoreProduct.observedValues.isNotEmpty())
    }
}
