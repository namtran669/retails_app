package namit.retail_app.core.presentation.cart

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.domain.AddProductToCartUseCase
import namit.retail_app.core.domain.GetUuidUseCase
import namit.retail_app.core.domain.ReduceOneProductUseCase
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel(
    private val addProductToCartUseCase: AddProductToCartUseCase,
    private val reduceOneProductUseCase: ReduceOneProductUseCase,
    private val getUuidUseCase: GetUuidUseCase,
    private val eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    val addProductSuccess = MutableLiveData<Pair<Boolean, Int>>()
    val addMultiProductSuccess = MutableLiveData<Pair<Boolean, Int>>()
    val reduceOneProductSuccess = MutableLiveData<Pair<Boolean, Int>>()
    val deleteProductInCartSuccess = MutableLiveData<Pair<Boolean, Int>>()

    val addProductInCategorySuccess = MutableLiveData<Triple<Boolean, Int, Int>>()
    val reduceOneProductInCategorySuccess = MutableLiveData<Triple<Boolean, Int, Int>>()
    val deleteProductCategoryInCartSuccess = MutableLiveData<Triple<Boolean, Int, Int>>()

    fun addOneProduct(productData: ProductItem, index: Int = -1) {
        launch {
            val secureIdResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (secureIdResult is UseCaseResult.Success) {
                val result = addProductToCartUseCase.execute(productData, secureIdResult.data!!, 1)
                if (result is UseCaseResult.Success) {
                    trackAddProduct(productData = productData)
                    addProductSuccess.value = Pair(first = result.data!!, second = index)
                }
            }
        }
    }

    fun addOneProduct(categoryPosition: Int = -1, productData: ProductItem, index: Int = -1) {
        launch {
            val secureIdResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (secureIdResult is UseCaseResult.Success) {
                val result = addProductToCartUseCase.execute(productData, secureIdResult.data!!, 1)
                if (result is UseCaseResult.Success) {
                    trackAddProduct(productData = productData)
                    addProductInCategorySuccess.value =
                        Triple(first = result.data!!, second = categoryPosition, third = index)
                }
            }
        }
    }

    fun addMultiProduct(productData: ProductItem,
                        index: Int = -1,
                        quantity: Int) {
        launch {
            val secureIdResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (secureIdResult is UseCaseResult.Success) {
                trackAddProduct(productData = productData, quantity = quantity)
                val result = addProductToCartUseCase.execute(
                    productData,
                    secureIdResult.data!!,
                    quantity
                )
                if (result is UseCaseResult.Success) {
                    addMultiProductSuccess.value = Pair(first = result.data!!, second = index)
                }
            }
        }
    }

    fun reduceOneProduct(productData: ProductItem, index: Int = -1) {
        launch {
            val secureIdResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (secureIdResult is UseCaseResult.Success) {
                val result = reduceOneProductUseCase.execute(productData, secureIdResult.data!!)
                if (result is UseCaseResult.Success) {
                    reduceOneProductSuccess.value = Pair(first = result.data!!, second = index)
                }
            }
        }
    }

    fun reduceOneProduct(categoryPosition: Int = -1, productData: ProductItem, index: Int = -1) {
        launch {
            val secureIdResult = withContext(Dispatchers.IO) { getUuidUseCase.execute() }
            if (secureIdResult is UseCaseResult.Success) {
                val result = reduceOneProductUseCase.execute(productData, secureIdResult.data!!)
                if (result is UseCaseResult.Success) {
                    reduceOneProductInCategorySuccess.value =
                        Triple(first = result.data!!, second = categoryPosition, third = index)
                }
            }
        }
    }

    private fun trackAddProduct(productData: ProductItem, quantity: Int = 1) {
        val optionNameList = mutableListOf<String>()
        productData.optionGroupSelected?.forEach {
            optionNameList.add(it.nameEn)
        }
        eventTrackingManager.trackAddToCart(
            productId = productData.id,
            productName = productData.nameEn,
            merchantId = productData.merchant?.id ?: "",
            merchantName = productData.merchant?.title ?: "",
            quantity = quantity,
            categoryIds = productData.categoryIds,
            options = optionNameList
        )
    }
}