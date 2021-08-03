package namit.retail_app.coupon.presentation.dialog

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.presentation.base.BaseViewModel

class CouponMerchantListViewModel(
    private val merchantList: List<MerchantInfoItem>,
    private val cartId: Int = -1
): BaseViewModel() {

    val renderCouponWithMerchants = MutableLiveData<List<String>>()
    val renderCouponWithCart = MutableLiveData<Int>()

    fun render() {
        if (cartId > 0) {
            renderCouponWithCart.value = cartId
        } else {
            val merchantIdList = mutableListOf<String>()
            merchantList.forEach {
                merchantIdList.add(it.id)
            }
            renderCouponWithMerchants.value = merchantIdList
        }
    }
}