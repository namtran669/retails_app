package namit.retail_app.food.viewmodel

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.domain.GetProductByCategoryUseCase
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.food.presentation.restaurant.menu.MenuPageViewModel
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
class MenuPageViewModelTest : BaseViewModelTest() {

    private lateinit var foodList: TestObserver<List<ProductItem>>
    private lateinit var showHaveNoMoreProduct: TestObserver<Boolean>
    private lateinit var openFoodDetailDialog: TestObserver<Pair<MerchantInfoItem, ProductItem>>

    private val getProductByCategoryUseCase: GetProductByCategoryUseCase = mock()

    private lateinit var viewModel: MenuPageViewModel

    private val categoryData = CategoryItem(id = 1)
    private val restaurantData = MerchantInfoItem(id = "1")

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

        viewModel = MenuPageViewModel(
            restaurantData = restaurantData,
            categoryData = categoryData,
            getProductByCategoryUseCase = getProductByCategoryUseCase
        )
        initTestObserver()
    }

    private fun initTestObserver() {
        foodList = viewModel.foodList.testObserver()
        showHaveNoMoreProduct = viewModel.showHaveNoMoreProduct.testObserver()
        openFoodDetailDialog = viewModel.openFoodDetailDialog.testObserver()
    }

    @Test
    fun loadProductList_success_page2_isEmpty() = runBlocking {
        whenever(
            getProductByCategoryUseCase.execute(
                merchantId = restaurantData.id,
                categoryIds = listOf(categoryData.id),
                page = 0
            )
        )
            .thenReturn(UseCaseResult.Success(dummyProductPage1))

        whenever(
            getProductByCategoryUseCase.execute(
                merchantId = restaurantData.id,
                categoryIds = listOf(categoryData.id),
                page = 1
            )
        )
            .thenReturn(UseCaseResult.Success(mutableListOf()))

        viewModel.loadFoodList()

        Assert.assertEquals(2, foodList.observedValues.size)
        // 6 skeleton item
        Assert.assertEquals(6, foodList.observedValues[0]?.size)
        // 10 product + 1 skeleton item
        Assert.assertEquals(11, foodList.observedValues[1]?.size)
        assert(showHaveNoMoreProduct.observedValues.isEmpty())


        viewModel.loadFoodList()
        Assert.assertEquals(10, foodList.observedValues[2]?.size)
        assert(showHaveNoMoreProduct.observedValues.isNotEmpty())

        //other
        assert(openFoodDetailDialog.observedValues.isEmpty())
    }

    @Test
    fun loadProductList_success_page2_has_1_item() = runBlocking {
        whenever(
            getProductByCategoryUseCase.execute(
                merchantId = restaurantData.id,
                categoryIds = listOf(categoryData.id),
                page = 0
            )
        )
            .thenReturn(UseCaseResult.Success(dummyProductPage1))

        whenever(
            getProductByCategoryUseCase.execute(
                merchantId = restaurantData.id,
                categoryIds = listOf(categoryData.id),
                page = 1
            )
        )
            .thenReturn(UseCaseResult.Success(dummyProductPage2))


        viewModel.loadFoodList()

        Assert.assertEquals(2, foodList.observedValues.size)
        // 6 skeleton item
        Assert.assertEquals(6, foodList.observedValues[0]?.size)
        // 10 product + 1 skeleton item
        Assert.assertEquals(11, foodList.observedValues[1]?.size)
        assert(showHaveNoMoreProduct.observedValues.isEmpty())


        viewModel.loadFoodList()
        Assert.assertEquals(11, foodList.observedValues[2]?.size)
        assert(showHaveNoMoreProduct.observedValues.isNotEmpty())

        //other
        assert(openFoodDetailDialog.observedValues.isEmpty())
    }

    @Test
    fun loadFoodListFirstTime_first() {
        viewModel.loadFoodListFirstTime()

        Assert.assertEquals(1, foodList.observedValues.size)
        //Skeleton
        Assert.assertEquals(6, foodList.observedValues[0]?.size)

        //other
        assert(openFoodDetailDialog.observedValues.isEmpty())
    }

    @Test
    fun loadFoodListFirstTime_second() = runBlocking {
        loadProductList_success_page2_isEmpty()

        viewModel.loadFoodListFirstTime()

        Assert.assertEquals(4, foodList.observedValues.size)
        Assert.assertEquals(10, foodList.observedValues[3]?.size)

        //other
        assert(openFoodDetailDialog.observedValues.isEmpty())
    }

    @Test
    fun presentFoodDetailDialog_success() {
        viewModel.presentFoodDetailDialog(ProductItem(id = 1))

        assert(openFoodDetailDialog.observedValues.isNotEmpty())
        Assert.assertEquals("1", openFoodDetailDialog.observedValues[0]!!.first.id)
        Assert.assertEquals(1, openFoodDetailDialog.observedValues[0]!!.second.id)

        //other
        assert(foodList.observedValues.isEmpty())
        assert(showHaveNoMoreProduct.observedValues.isEmpty())
    }
}