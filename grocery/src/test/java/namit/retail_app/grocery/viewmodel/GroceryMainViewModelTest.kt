package namit.retail_app.grocery.viewmodel

import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.coupon.data.entity.CouponShelfModel
import namit.retail_app.grocery.data.domain.GetFeatureProductCategoryUseCase
import namit.retail_app.grocery.data.domain.GetGroceryMerchantUseCase
import namit.retail_app.grocery.presentation.main.GroceryMainViewModel
import namit.retail_app.story.data.entity.StoryShelf
import namit.retail_app.story.domain.GetFoodStoryUseCase
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
class GroceryMainViewModelTest : BaseViewModelTest() {
    private lateinit var merchantNameList: TestObserver<List<MerchantInfoItem>>
    private lateinit var storyShelfList: TestObserver<List<StoryShelf>>
    private lateinit var couponListModel: TestObserver<List<CouponShelfModel>>
    private lateinit var featuredProductCategoryList: TestObserver<List<CategoryItem>>

    private lateinit var isMerchantListLoading: TestObserver<Boolean>
    private lateinit var isFeaturedProductListLoading: TestObserver<Boolean>
    private lateinit var isCouponListLoading: TestObserver<Boolean>
    private lateinit var isStoryListLoading: TestObserver<Boolean>

    private val getMerchantListUseCase: GetGroceryMerchantUseCase = mock()
    private val getFoodStoryUseCase: GetFoodStoryUseCase = mock()
    private val getFeatureProductCategoryUseCase: GetFeatureProductCategoryUseCase = mock()
    private val eventTrackingManager: EventTrackingManager = mock()

    private lateinit var viewModel: GroceryMainViewModel

    private val latitude = 1.0
    private val longitude = 1.0

    override fun setup() {
        super.setup()
        viewModel = GroceryMainViewModel(
            getMerchantListUseCase = getMerchantListUseCase,
            getFoodStoryUseCase = getFoodStoryUseCase,
            getFeatureProductCategoryUseCase = getFeatureProductCategoryUseCase,
            eventTrackingManager = eventTrackingManager
        )

        initTestObserver()
    }

    private fun initTestObserver() {
        merchantNameList = viewModel.merchantNameList.testObserver()
        storyShelfList = viewModel.storyShelfList.testObserver()
        couponListModel = viewModel.couponList.testObserver()
        featuredProductCategoryList = viewModel.featuredProductCategoryList.testObserver()
        isMerchantListLoading = viewModel.isMerchantListLoading.testObserver()
        isFeaturedProductListLoading = viewModel.isFeaturedProductListLoading.testObserver()
        isCouponListLoading = viewModel.isCouponListLoading.testObserver()
        isStoryListLoading = viewModel.isStoryListLoading.testObserver()
    }

    @Test
    fun loadMerchantList_success() = runBlocking {
        val merchantResponseList = mutableListOf(
            MerchantInfoItem(id = "1"),
            MerchantInfoItem(id = "2"),
            MerchantInfoItem(id = "3"),
            MerchantInfoItem(id = "4")
        )
        val categoryResponseList = mutableListOf(
            CategoryItem(),
            CategoryItem(),
            CategoryItem(),
            CategoryItem()
        )
        val merchantIds = listOf("1", "2", "3", "4")
        whenever(getMerchantListUseCase.execute(latitude, longitude)).thenReturn(
            UseCaseResult.Success(
                merchantResponseList
            )
        )
        whenever(getFeatureProductCategoryUseCase.execute(merchantIds)).thenReturn(
            UseCaseResult.Success(categoryResponseList)
        )
        viewModel.loadMerchantList(latitude, longitude)

        assert(isMerchantListLoading.observedValues.isNotEmpty())
        assert(merchantNameList.observedValues.isNotEmpty())
        assertEquals(4, merchantNameList.observedValues[0]!!.size)
        assertEquals(true, isFeaturedProductListLoading.observedValues[0])

        assert(featuredProductCategoryList.observedValues.isNotEmpty())
        assertEquals(4, featuredProductCategoryList.observedValues[0]!!.size)
        assertEquals(false, isFeaturedProductListLoading.observedValues[2])

        //other
        assert(storyShelfList.observedValues.isEmpty())
        assert(couponListModel.observedValues.isEmpty())
        assert(isCouponListLoading.observedValues.isEmpty())
        assert(isStoryListLoading.observedValues.isEmpty())
    }
}
