package namit.retail_app.grocery.presentation.merchant

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.data.entity.TimeSlot
import namit.retail_app.core.data.repository.ProductRepositoryImpl
import namit.retail_app.core.domain.*
import namit.retail_app.core.domain.GetMerchantProductUseCaseImpl.Companion.ERROR_EMPTY_PRODUCT_CASE
import namit.retail_app.core.extension.DATE_TIME_FORMAT_YYYY_MM_DD
import namit.retail_app.core.extension.DAY_OF_WEEK_FULL
import namit.retail_app.core.extension.convertToDate
import namit.retail_app.core.extension.convertTodayTomorrowTime
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class GroceryMerchantDetailViewModel(
    private val merchantInfoData: MerchantInfoItem,
    private val getProductListUseCase: GetMerchantProductUseCase,
    private val getCurrentDeliveryTimeSlotDataUseCase: GetCurrentDeliveryTimeSlotDataUseCase,
    private val saveDeliveryTimeUseCase: SaveDeliveryTimeUseCase,
    private val getSelectedDeliveryTimeUseCase: GetSelectedDeliveryTimeUseCase,
    eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    init {
        eventTrackingManager.trackMerchantView(
            merchantId = merchantInfoData.id,
            merchantName = merchantInfoData.title
        )
    }

    val merchantInfo = MutableLiveData<MerchantInfoItem>()
    val productList = MutableLiveData<MutableList<ProductItem>>()
    val showHaveNoMoreProduct = MutableLiveData<Boolean>()
    val openProductDetail = SingleLiveEvent<Pair<ProductItem, MerchantInfoItem>>()
    val deliveryDateTime = MutableLiveData<Pair<Boolean, String>>()
    val openTimeSlotDialog = SingleLiveEvent<MerchantInfoItem>()
    val renderMerchantCoupon = MutableLiveData<List<String>>()
    val openCouponMerchantDialog = SingleLiveEvent<List<MerchantInfoItem>>()

    private var selectedTimeSlot: TimeSlot? = null
    private var currentProductPage: Int = ProductRepositoryImpl.FIRST_PAGE
    private var currentProductList = mutableListOf<ProductItem>()
    private var isNoMoreProductData = false
    private var isProductListLoading = false

    init {
        merchantInfo.value = merchantInfoData
        renderMerchantCoupon.value = listOf(merchantInfoData.id)
    }

    fun loadProductList() {
        if (isNoMoreProductData || isProductListLoading) {
            return
        }

        if (currentProductPage == ProductRepositoryImpl.FIRST_PAGE) {
            //Add 6 empty data to show Skeleton for more loading
            val skeletonDataList = mutableListOf<ProductItem>()
            skeletonDataList.add(ProductItem())
            skeletonDataList.add(ProductItem())
            skeletonDataList.add(ProductItem())
            skeletonDataList.add(ProductItem())
            skeletonDataList.add(ProductItem())
            skeletonDataList.add(ProductItem())
            productList.value = skeletonDataList
        }

        isProductListLoading = true

        launch {
            val productListResult =
                getProductListUseCase.execute(
                    merchantId = merchantInfoData.id,
                    page = currentProductPage
                )

            if (productListResult is UseCaseResult.Success) {
                val newProductList = productListResult.data!!
                currentProductList.addAll(newProductList)
                newProductList.forEach {
                    it.merchant = merchantInfoData

                }

                val productListForRender = mutableListOf<ProductItem>()
                productListForRender.addAll(currentProductList)

                if (newProductList.size < ProductRepositoryImpl.SIZE_PRODUCT_EACH_REQUEST) {
                    isNoMoreProductData = true
                    showHaveNoMoreProduct.value = true
                } else {
                    //Add two skeleton item on bottom
                    productListForRender.add(ProductItem())
                    productListForRender.add(ProductItem())
                }

                productList.value = productListForRender
                currentProductPage++

            } else if (productListResult is UseCaseResult.Error && productListResult.exception.message == ERROR_EMPTY_PRODUCT_CASE) {
                isNoMoreProductData = true
                showHaveNoMoreProduct.value = true
                productList.value = currentProductList
            }
            isProductListLoading = false
        }
    }

    fun openProductDetailDialog(productItem: ProductItem) {
        openProductDetail.value = Pair(first = productItem, second = merchantInfoData)
    }

    fun loadDefaultDeliveryTimeSlot() {
        val storedDeliveryTime = getSelectedDeliveryTimeUseCase.execute(merchantInfoData.id)
        if (storedDeliveryTime is UseCaseResult.Success) {
            setSelectedTimeSlot(storedDeliveryTime.data!!)
        } else {
            launch {
                val getDeliveryTime = getCurrentDeliveryTimeSlotDataUseCase.execute(
                    merchantId = merchantInfoData.id
                )
                if (getDeliveryTime is UseCaseResult.Success) {
                    val timeSlotList = getDeliveryTime.data!!.deliveryDates
                    day_loop@ for (day in timeSlotList) {
                        if (day.isDeliveryNow && day.isSelected) {
                            setSelectedTimeSlot(timeSlot = day)
                            break
                        } else {
                            for (time in day.slots) {
                                if (day.isSelected && time.isSelected) {
                                    setSelectedTimeSlot(timeSlot = day)
                                    break@day_loop
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setSelectedTimeSlot(timeSlot: TimeSlot) {
        saveDeliveryTimeUseCase.execute(merchantInfoData.id, timeSlot)
        if (timeSlot.isDeliveryNow.not()) {
            selectedTimeSlot = timeSlot
            val currentDate = timeSlot.date.convertToDate(format = DATE_TIME_FORMAT_YYYY_MM_DD)
            timeSlot.slots.firstOrNull { it.isSelected }?.let {
                deliveryDateTime.value = Pair(
                    first = false,
                    second = "${currentDate.convertTodayTomorrowTime(DAY_OF_WEEK_FULL)}, ${it.hour.replace(
                        " ",
                        ""
                    )}"
                )
            }
        } else {
            deliveryDateTime.value = Pair(first = true, second = "")
        }
    }

    fun checkDeliveryAddress() {
        val storedDeliveryTime = getSelectedDeliveryTimeUseCase.execute(merchantInfoData.id)
        if (storedDeliveryTime is UseCaseResult.Success) {
            setSelectedTimeSlot(storedDeliveryTime.data!!)
        } else {
            presentTimeSlotDialog()
        }
    }

    fun presentTimeSlotDialog() {
        openTimeSlotDialog.value = merchantInfoData
    }

    fun addProduct(position: Int) {
        val listUpdate = currentProductList
        listUpdate[position].quantityOrder += 1
        productList.value = listUpdate
    }

    fun reduceProductInCart(position: Int) {
        val listUpdate = productList.value!!
        listUpdate[position].quantityOrder -= 1
        productList.value = listUpdate
    }

    fun removeProductInCart(position: Int) {
        val listUpdate = productList.value!!
        listUpdate[position].quantityOrder = 0
        productList.value = listUpdate
    }

    fun presentCouponMerchantDialog() {
        openCouponMerchantDialog.value = listOf(merchantInfoData)
    }
}