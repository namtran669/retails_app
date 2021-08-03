package namit.retail_app.food.viewmodel

import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.food.domain.GetRestaurantListUseCase
import namit.retail_app.food.presentation.vertical.FoodVerticalViewModel
import namit.retail_app.testutils.BaseViewModelTest
import namit.retail_app.testutils.TestObserver
import namit.retail_app.testutils.testObserver
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class FoodVerticalViewModelTest : BaseViewModelTest(){
    private lateinit var restaurantList: TestObserver<List<MerchantInfoItem>>

    private val getRestaurantListUseCase: GetRestaurantListUseCase = mock()

    private lateinit var viewModel: FoodVerticalViewModel

    private val latitude = 1.0
    private val longitude = 1.0

    override fun setup() {
        super.setup()

        viewModel = FoodVerticalViewModel(
            getRestaurantListUseCase = getRestaurantListUseCase
        )
        initTestObserver()
    }

    private fun initTestObserver() {
        restaurantList = viewModel.restaurantList.testObserver()
    }

    @Test
    fun loadRestaurantList_success() = runBlocking {
        val responseList = mutableListOf(
            MerchantInfoItem(),
            MerchantInfoItem(),
            MerchantInfoItem(),
            MerchantInfoItem()
        )
        whenever(getRestaurantListUseCase.execute(latitude, longitude)).thenReturn(
            UseCaseResult.Success(
                responseList
            )
        )

        viewModel.loadRestaurantList(latitude, longitude)

        assert(restaurantList.observedValues.isNotEmpty())
        assertEquals(1, restaurantList.observedValues[0]!!.size)
        assertEquals(4, restaurantList.observedValues[1]!!.size)
    }
}