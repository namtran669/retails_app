package namit.retail_app.grocery.presentation.main

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.AddressModel
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.enums.MerchantType
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.LocaleUtils
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.coupon.data.entity.CouponShelfModel
import namit.retail_app.grocery.data.domain.GetFeatureProductCategoryUseCase
import namit.retail_app.grocery.data.domain.GetGroceryMerchantUseCase
import namit.retail_app.story.data.entity.StoryShelf
import namit.retail_app.story.domain.GetFoodStoryUseCase
import kotlinx.coroutines.launch

class GroceryMainViewModel(
    private val getMerchantListUseCase: GetGroceryMerchantUseCase,
    private val getFoodStoryUseCase: GetFoodStoryUseCase,
    private val getFeatureProductCategoryUseCase: GetFeatureProductCategoryUseCase,
    eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    init {
        eventTrackingManager.trackVertical(merchantType = MerchantType.GROCERY)
    }

    val merchantNameList = MutableLiveData<List<MerchantInfoItem>>()
    val storyShelfList = MutableLiveData<List<StoryShelf>>()
    val couponList = MutableLiveData<List<CouponShelfModel>>()
    val featuredProductCategoryList = MutableLiveData<List<CategoryItem>>()
    val openMerchantDetail = SingleLiveEvent<MerchantInfoItem>()
    val openProductDetail = SingleLiveEvent<Pair<ProductItem, MerchantInfoItem>>()

    val isMerchantListLoading = MutableLiveData<Boolean>()
    val isFeaturedProductListLoading = MutableLiveData<Boolean>()
    val isCouponListLoading = MutableLiveData<Boolean>()
    val isStoryListLoading = MutableLiveData<Boolean>()
    val showEditAddressDialog = SingleLiveEvent<AddressModel>()
    val shouldShowSetLocationDialog = SingleLiveEvent<Unit>()
    private var merchantDataList = listOf<MerchantInfoItem>()

    fun loadMerchantList(lat: Double, lng: Double) {
        isMerchantListLoading.value = true
        launch {
            isFeaturedProductListLoading.value = true
            val merchantResult = getMerchantListUseCase.execute(lat, lng)
            if (merchantResult is UseCaseResult.Success) {
                merchantDataList = merchantResult.data!!

                //Load feature product categories of each merchant
                loadFeatureProductCategory(merchantDataList)
            }
            merchantNameList.value = merchantDataList
            isMerchantListLoading.value = false
        }
    }

    fun loadStoryList() {
        launch {
            isStoryListLoading.value = true
            val foodStoryResult = getFoodStoryUseCase.execute("Food Stories")
            val mockShelfContent = mutableListOf<StoryShelf>()

            if (foodStoryResult is UseCaseResult.Success) {
                mockShelfContent.add(
                    StoryShelf().apply {
                        id = ""
                        title = getTitleStories()
                        contentList = foodStoryResult.data!!
                    }
                )
            }

            storyShelfList.value = mockShelfContent
            isStoryListLoading.value = false
        }
    }

    private fun getTitleStories(): String {
        if (LocaleUtils.isThai()) {
            return "เกร็ดความรู้"
        }
        return "Stories"
    }

    private fun loadFeatureProductCategory(merchantList: List<MerchantInfoItem>) {
        launch {
            isFeaturedProductListLoading.value = true
            val merchantIds = mutableListOf<String>()
            merchantList.forEach { merchantIds.add(it.id) }

            val productListResult = getFeatureProductCategoryUseCase.execute(merchantIds)

            if (productListResult is UseCaseResult.Success) {
                //Add merchant name to feature category list to render
                merchantList.forEach { merchant ->
                    productListResult.data!!.firstOrNull { category ->
                        category.merchantId == merchant.id
                    }?.apply {
                        merchantName = merchant.title
                    }
                }

                featuredProductCategoryList.value = productListResult.data
            }
            isFeaturedProductListLoading.value = false
        }
    }

    fun showAllMerchantProduct(merchantId: String) {
        //Feature list is loaded by id in merchant list, so always find out one
        merchantDataList.first {
            it.id == merchantId
        }.apply {
            openMerchantDetail.value = this
        }
    }

    fun showProductDetailDialog(productItem: ProductItem) {
        //Feature list is loaded by id in merchant list, so always find out one
        val merchantInfoItem = merchantDataList.firstOrNull {
            it.id == productItem.merchantId
        }

        merchantInfoItem?.let {
            openProductDetail.value = Pair(first = productItem, second = it)
        } ?: kotlin.run {
            openProductDetail.value = Pair(first = productItem, second = MerchantInfoItem().apply {
                id = productItem.merchantId
            })
        }
    }

    fun showEditAddressDialog(address: AddressModel) {
        showEditAddressDialog.value = address
    }

    fun showSetLocationDialog() {
        shouldShowSetLocationDialog.call()
    }

    fun addProduct(categoryPosition: Int, productPosition: Int) {
        val categoryList = featuredProductCategoryList.value!!
        val product = categoryList[categoryPosition].productList!![productPosition]
        product.quantityOrder += 1
        featuredProductCategoryList.value = categoryList
    }

    fun reduceProductInCart(categoryPosition: Int, productPosition: Int) {
        val categoryList = featuredProductCategoryList.value!!
        val product = categoryList[categoryPosition].productList!![productPosition]
        product.quantityOrder -= 1
        featuredProductCategoryList.value = categoryList
    }

    fun deleteProductInCart(categoryPosition: Int, productPosition: Int) {
        val categoryList = featuredProductCategoryList.value!!
        val product = categoryList[categoryPosition].productList!![productPosition]
        product.quantityOrder = 0
        featuredProductCategoryList.value = categoryList
    }
}