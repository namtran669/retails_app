package namit.retail_app.food.viewmodel

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.domain.GetMerchantCategoryUseCase
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.food.presentation.restaurant.RestaurantDetailViewModel
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
class RestaurantDetailViewModelTest : BaseViewModelTest() {

    private lateinit var restaurantInfo: TestObserver<MerchantInfoItem>
    private lateinit var selectedCategory: TestObserver<Pair<MerchantInfoItem, CategoryItem>>
    private lateinit var categoryList: TestObserver<List<CategoryItem>>
    private lateinit var checkGoToBottom: TestObserver<CategoryItem>

    private val getCategoryListUseCase: GetMerchantCategoryUseCase = mock()

    private lateinit var viewModel: RestaurantDetailViewModel

    private lateinit var restaurantInfoData: MerchantInfoItem
    private lateinit var categoryResponseList: List<CategoryItem>

    override fun setup() {
        super.setup()

        restaurantInfoData = MerchantInfoItem(id = "1")

        categoryResponseList = mutableListOf(
            CategoryItem(id = 1),
            CategoryItem(id = 2),
            CategoryItem(id = 3),
            CategoryItem(id = 4)
        )

        viewModel = RestaurantDetailViewModel(
            restaurantData = restaurantInfoData,
            getCategoryListUseCase = getCategoryListUseCase
        )

        initTestObserver()
    }

    private fun initTestObserver() {
        restaurantInfo = viewModel.restaurantInfo.testObserver()
        selectedCategory = viewModel.selectedCategory.testObserver()
        categoryList = viewModel.categoryList.testObserver()
        checkGoToBottom = viewModel.checkGoToBottom.testObserver()
    }

    @Test
    fun loadCategoryList_success() = runBlocking {

        whenever(getCategoryListUseCase.execute(restaurantInfoData.id)).thenReturn(
            UseCaseResult.Success(categoryResponseList)
        )

        viewModel.loadCategoryList()

        assert(categoryList.observedValues.isNotEmpty())
        assert(selectedCategory.observedValues.isNotEmpty())
        Assert.assertEquals(4, categoryList.observedValues[0]!!.size)
        Assert.assertEquals(1, selectedCategory.observedValues[0]!!.second.id)
        Assert.assertEquals("1", selectedCategory.observedValues[0]!!.first.id)

        //other
        assert(checkGoToBottom.observedValues.isEmpty())
    }

    @Test
    fun updateCategory_success() {

        loadCategoryList_success()

        viewModel.updateCategory(1)

        Assert.assertEquals(2, selectedCategory.observedValues[1]!!.second.id)
        Assert.assertEquals("1", selectedCategory.observedValues[1]!!.first.id)

    }

    @Test
    fun checkGotoBottomList_success() {
        loadCategoryList_success()

        viewModel.checkGotoBottomList()

        Assert.assertEquals(1, checkGoToBottom.observedValues[0]!!.id)
    }
}