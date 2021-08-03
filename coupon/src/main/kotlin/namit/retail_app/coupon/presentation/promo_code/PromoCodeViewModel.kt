package namit.retail_app.coupon.presentation.promo_code

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.RedeemCart
import namit.retail_app.core.domain.RedeemCartUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class PromoCodeViewModel(
    private val cartId: Int,
    private val redeemCartUseCase: RedeemCartUseCase
) : BaseViewModel() {

    val redeemSuccess = MutableLiveData<RedeemCart>()
    val redeemFail = MutableLiveData<Unit>()
    val isRedeemProcessing = MutableLiveData<Boolean>()
    val showErrorMessage = SingleLiveEvent<String>()

    private var currentPromoCode = ""

    fun redeemCode() {
        if (currentPromoCode.isBlank()) {
            return
        }
        launch {
            isRedeemProcessing.value = true
            val redeemResult =
                redeemCartUseCase.execute(cartId = cartId, couponCode = currentPromoCode)
            when(redeemResult){
                is UseCaseResult.Success -> redeemSuccess.value = redeemResult.data!!.apply {
                    code = currentPromoCode
                }
                is UseCaseResult.Error ->{
                    showErrorMessage.value = redeemResult.exception.message
                    redeemFail.value = Unit
                }
            }
            isRedeemProcessing.value = false
        }
    }

    fun setCurrentPromoCode(newCode: String) {
        currentPromoCode = newCode
    }
}