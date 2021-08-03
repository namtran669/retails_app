package namit.retail_app.grocery.viewmodel

import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.domain.*
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.grocery.presentation.merchant.GroceryMerchantDetailViewModel
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
class MerchantDetailViewModelTest : BaseViewModelTest() {
    private lateinit var productList: TestObserver<MutableList<ProductItem>>
    private lateinit var showHaveNoMore: TestObserver<Boolean>
    private lateinit var merchantInfo: TestObserver<MerchantInfoItem>

    private lateinit var viewModel: GroceryMerchantDetailViewModel
    private val getProductListUseCase: GetMerchantProductUseCase = mock()
    private val getCategoryListUseCase: GetMerchantCategoryUseCase = mock()
    private val getCurrentDeliveryTimeSlotDataUseCase: GetCurrentDeliveryTimeSlotDataUseCase = mock()
    private val eventTrackingManager: EventTrackingManager = mock()

    private val getSelectedDeliveryTimeUseCase: GetSelectedDeliveryTimeUseCase = mock()
    private val saveDeliveryTimeUseCase: SaveDeliveryTimeUseCase = mock()

    private val merchantData = MerchantInfoItem(id = "2")
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


        viewModel = GroceryMerchantDetailViewModel(
            merchantInfoData = merchantData,
            getProductListUseCase = getProductListUseCase,
            getCurrentDeliveryTimeSlotDataUseCase = getCurrentDeliveryTimeSlotDataUseCase,
            saveDeliveryTimeUseCase = saveDeliveryTimeUseCase,
            eventTrackingManager = eventTrackingManager,
            getSelectedDeliveryTimeUseCase = getSelectedDeliveryTimeUseCase
        )

        initTestObserver()
    }

    private fun initTestObserver() {
        productList = viewModel.productList.testObserver()
        showHaveNoMore = viewModel.showHaveNoMoreProduct.testObserver()
        merchantInfo = viewModel.merchantInfo.testObserver()
    }

    @Test
    fun initData_test() {
        assert(merchantInfo.observedValues.isNotEmpty())
    }

    @Test
    fun loadProductList_success_page2_isEmpty() = runBlocking {
        whenever(getProductListUseCase.execute(merchantData.id, 0))
            .thenReturn(UseCaseResult.Success(dummyProductPage1))

        whenever(getProductListUseCase.execute(merchantData.id, 1))
            .thenReturn(UseCaseResult.Success(mutableListOf()))

        viewModel.loadProductList()

        Assert.assertEquals(2, productList.observedValues.size)
        // 6 skeleton item
        Assert.assertEquals(6, productList.observedValues[0]?.size)
        // 10 product + 2 skeleton item
        Assert.assertEquals(12, productList.observedValues[1]?.size)
        assert(showHaveNoMore.observedValues.isEmpty())


        viewModel.loadProductList()
        Assert.assertEquals(10, productList.observedValues[2]?.size)
        assert(showHaveNoMore.observedValues.isNotEmpty())
    }

    @Test
    fun loadProductList_success_page2_has_1_item() = runBlocking {
        whenever(getProductListUseCase.execute(merchantData.id, 0))
            .thenReturn(UseCaseResult.Success(dummyProductPage1))

        whenever(getProductListUseCase.execute(merchantData.id, 1))
            .thenReturn(UseCaseResult.Success(dummyProductPage2))

        viewModel.loadProductList()

        Assert.assertEquals(2, productList.observedValues.size)
        // 6 skeleton item
        Assert.assertEquals(6, productList.observedValues[0]?.size)
        // 10 product + 2 skeleton item
        Assert.assertEquals(12, productList.observedValues[1]?.size)
        assert(showHaveNoMore.observedValues.isEmpty())


        viewModel.loadProductList()
        Assert.assertEquals(11, productList.observedValues[2]?.size)
        assert(showHaveNoMore.observedValues.isNotEmpty())
    }
}
