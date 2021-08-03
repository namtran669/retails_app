package namit.retail_app.grocery.viewmodel

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.domain.GetProductByCategoryUseCase
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.grocery.presentation.category_detail.GroceryCategoryDetailViewModel
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
class GroceryCategoryDetailViewModelTest : BaseViewModelTest() {
    private lateinit var showHaveNoMoreProduct: TestObserver<Boolean>
    private lateinit var productList: TestObserver<MutableList<ProductItem>>
    private lateinit var currentCategory: TestObserver<CategoryItem>

    private val getProductByCategoryUseCase: GetProductByCategoryUseCase = mock()
    private val eventTrackingManager: EventTrackingManager = mock()

    private lateinit var viewModel: GroceryCategoryDetailViewModel

    private val selectedCategoryData =
        CategoryItem(nameEn = "Selected", nameTh = "Selected", id = 2, merchantId = "1")

    private val merchantInfoItem = MerchantInfoItem().apply {
        id = "1"
    }
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

        viewModel = GroceryCategoryDetailViewModel(
            merchantData = merchantInfoItem,
            selectedCategoryData = selectedCategoryData,
            getProductByCategoryUseCase = getProductByCategoryUseCase,
            eventTrackingManager = eventTrackingManager
        )

        initTestObserver()
    }

    private fun initTestObserver() {
        showHaveNoMoreProduct = viewModel.showHaveNoMoreProduct.testObserver()
        productList = viewModel.productList.testObserver()
        currentCategory = viewModel.currentCategory.testObserver()
    }

    @Test
    fun initData_test() {
        assert(currentCategory.observedValues.isNotEmpty())
    }

    @Test
    fun loadProductList_success_page2_isEmpty() = runBlocking {
        whenever(
            getProductByCategoryUseCase.execute(
                merchantId = "1",
                page = 0,
                categoryIds = listOf(selectedCategoryData.id)
            )
        )
            .thenReturn(UseCaseResult.Success(dummyProductPage1))

        whenever(
            getProductByCategoryUseCase.execute(
                merchantId = "1",
                page = 1,
                categoryIds = listOf(selectedCategoryData.id)
            )
        )
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
        whenever(
            getProductByCategoryUseCase.execute(
                merchantId = "1",
                page = 0,
                categoryIds = listOf(selectedCategoryData.id)
            )
        ).thenReturn(UseCaseResult.Success(dummyProductPage1))

        whenever(
            getProductByCategoryUseCase.execute(
                merchantId = "1",
                page = 1,
                categoryIds = listOf(selectedCategoryData.id)
            )
        ).thenReturn(UseCaseResult.Success(dummyProductPage2))

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
