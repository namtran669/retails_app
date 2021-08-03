package namit.retail_app.coupon.presentation.coupon

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.CouponModel
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.domain.GetMerchantByStoreIdUseCase
import namit.retail_app.core.enums.MerchantType
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.coupon.data.entity.CouponFilterModel
import namit.retail_app.coupon.domain.*
import kotlinx.coroutines.launch

class CouponViewModel(
    private val getAllCouponListUseCase: GetAllCouponListUseCase,
    private val getCouponListByVerticalUseCase: GetCouponListByVerticalUseCase,
    private val getCouponListByMerchantUseCase: GetCouponListByMerchantUseCase,
    private val getCouponFilterListUseCase: GetCouponFilterListUseCase,
    private val saveSelectedCouponUseCase: SaveSelectedCouponUseCase,
    private val getMerchantByStoreIdUseCase: GetMerchantByStoreIdUseCase,
    private val getCouponListWithCartUseCase: GetCouponListWithCartUseCase
) : BaseViewModel() {

    companion object {
        private const val FILTER_ALL = 0
        private const val FILTER_FLASH_DEAL = 1
    }

    val couponFilterList = MutableLiveData<List<CouponFilterModel>>()
    val couponList = MutableLiveData<List<CouponModel>>()
    val totalOfCoupon = MutableLiveData<Int>()
    val isCouponListLoading = MutableLiveData<Boolean>()
    val showEmpty = MutableLiveData<Boolean>()

    val openMainGrocery = SingleLiveEvent<Unit>()
    val openMerchantDetails = SingleLiveEvent<MerchantInfoItem>()

    private val currentCouponFilterList = mutableListOf<CouponFilterModel>()

    fun loadCouponTypeList() {
        launch {
            val couponFilterResult = getCouponFilterListUseCase.execute()
            if (couponFilterResult is UseCaseResult.Success) {
                currentCouponFilterList.clear()
                currentCouponFilterList.addAll(couponFilterResult.data!!)
                couponFilterList.value = currentCouponFilterList
            }
        }
    }

    fun changeSelectedFilter(filterIndex: Int) {
        currentCouponFilterList.forEachIndexed { index, couponFilterModel ->
            couponFilterModel.isSelected = filterIndex == index
        }
        couponFilterList.value = currentCouponFilterList
        if (filterIndex == FILTER_ALL) {
            loadAllCoupon()
        } else {
            loadCouponListByVertical(vertical = currentCouponFilterList[filterIndex].slug)
        }

        //TODO Implement flash deal next phase
        //else if (filterIndex == FILTER_FLASH_DEAL) {}
    }

    fun loadAllCoupon() {
        showLoading()
        launch {
            renderCoupon(getAllCouponListUseCase.execute(page = 0))
        }
    }

    fun loadCouponListByVertical(vertical: String) {
        showLoading()
        launch {
            renderCoupon(
                getCouponListByVerticalUseCase.execute(
                    page = 0,
                    vertical = vertical
                )
            )
        }
    }

    fun loadCouponListByCart(cartId: Int) {
        showLoading()
        launch {
            renderCoupon(
                getCouponListWithCartUseCase.execute(cartId)
            )
        }
    }

    fun loadCouponListByMerchantId(merchantIds: List<String>) {
        showLoading()
        launch {
            renderCoupon(
                getCouponListByMerchantUseCase.execute(
                    page = 0,
                    merchantIds = merchantIds
                )
            )
        }
    }

    fun saveSelectedCoupon(couponModel: CouponModel) {
        saveSelectedCouponUseCase.execute(couponModel = couponModel)
        if (couponModel.couponMerchantType == MerchantType.MERCHANT) {
            couponModel.merchantInfoItem?.id?.let { merchantId ->
                launch {
                    val getMerchantResult = getMerchantByStoreIdUseCase.execute(merchantId)
                    if (getMerchantResult is UseCaseResult.Success) {
                        openMerchantDetails.value = getMerchantResult.data!!
                    }
                }
            }
        } else {
            openMainGrocery.call()
        }
    }

    private fun showLoading() {
        isCouponListLoading.value = true
        couponList.value = listOf(
            CouponModel(),
            CouponModel(),
            CouponModel(),
            CouponModel(),
            CouponModel(),
            CouponModel()
        )
    }

    private fun renderCoupon(useCaseResult: UseCaseResult<List<CouponModel>>) {
        if (useCaseResult is UseCaseResult.Success) {
            totalOfCoupon.value = useCaseResult.data!!.size
            couponList.value = useCaseResult.data!!
            isCouponListLoading.value = false
            showEmpty.value = false
        } else {
            isCouponListLoading.value = false
            showEmpty.value = true
        }
    }
}