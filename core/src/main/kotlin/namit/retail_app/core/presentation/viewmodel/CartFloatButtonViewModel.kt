package namit.retail_app.core.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.CartModel
import namit.retail_app.core.domain.GetCartInfoUseCase
import namit.retail_app.core.domain.GetUuidUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.core.utils.eventpipe.EventPipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ObsoleteCoroutinesApi
class CartFloatButtonViewModel(
    private val getCartInfoUseCase: GetCartInfoUseCase,
    private val getUuidUseCase: GetUuidUseCase
) : BaseViewModel() {

    companion object {
        val TAG: String = CartFloatButtonViewModel::class.java.simpleName
    }

    val cartInfo = MutableLiveData<CartModel>()
    val isCartLoading = MutableLiveData<Boolean>()

    private val eventContextName = "${TAG}_${System.currentTimeMillis()}"

    init {
        EventPipe.registerEvent(
            eventContextName,
            Dispatchers.Main,
            CartModel::class.java
        ) { cartInfo ->
            updateCartInfoFromNotification(cartInfo)
        }
    }

    override fun onCleared() {
        super.onCleared()
        EventPipe.unregisterByContext(eventContextName)
    }


    fun loadCartInfoFirstTime() {
        launch {
            isCartLoading.value = true

            val uuidResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (uuidResult is UseCaseResult.Success) {
                val cartsResult =
                    getCartInfoUseCase.execute(uuidResult.data!!, 0)
                if (cartsResult is UseCaseResult.Success) {
                    notifyCartInfoChange(cartsResult.data!!)
                }
            }
            isCartLoading.value = false
        }
    }

    private fun updateCartInfoFromNotification(cartModel: CartModel) {
        cartInfo.value = cartModel
    }

    fun refreshCartInfo() {
        launch {
            val uuidResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (uuidResult is UseCaseResult.Success) {
                val cartsResult =
                    getCartInfoUseCase.execute(uuidResult.data!!, 0)
                if (cartsResult is UseCaseResult.Success) {
                    notifyCartInfoChange(cartsResult.data!!)
                } else {
                    notifyCartInfoChange(CartModel())
                }
            }
        }
    }

    fun notifyCartInfoChange(cartInfo: CartModel) {
        EventPipe.send(cartInfo)
    }

}