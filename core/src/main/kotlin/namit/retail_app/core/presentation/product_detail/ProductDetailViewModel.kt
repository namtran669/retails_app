package namit.retail_app.core.presentation.product_detail

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.OptionGroup
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.data.repository.ProductRepositoryImpl
import namit.retail_app.core.domain.GetRelatedProductByCategoryUseCase
import namit.retail_app.core.enums.MerchantType
import namit.retail_app.core.extension.formatCurrency
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val getRelatedProductByCategoryUseCase: GetRelatedProductByCategoryUseCase,
    productData: ProductItem,
    private val merchantData: MerchantInfoItem,
    eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    val renderProductImage = MutableLiveData<List<String>>()
    val productDataLiveData = MutableLiveData<ProductItem>()
    val relatedProduct = MutableLiveData<List<ProductItem>>()
    val merchantInfo = MutableLiveData<MerchantInfoItem>()
    val productQuantity = MutableLiveData<Int>()
    val totalPrice = MutableLiveData<String>()
    val toggleMinusButton = MutableLiveData<Boolean>()
    val showTitleBar = SingleLiveEvent<String>()
    val shouldShowProductTitle = MutableLiveData<Boolean>()
    val showCustomizeOptionView = MutableLiveData<Boolean>()
    val showRelatedProduct = MutableLiveData<Boolean>()
    val scrollToTop = MutableLiveData<Unit>()
    val merchantType = MutableLiveData<MerchantType>()
    val isOutOfStockState = SingleLiveEvent<Boolean>()

    private var currentRelatedProductPage: Int = ProductRepositoryImpl.FIRST_PAGE
    private var isShowingCustomizeView = false
    private val relatedProductList = mutableListOf<ProductItem>()
    private var product: ProductItem = productData.copy(quantityOrder = 1)

    init {
        eventTrackingManager.trackProductView(
            merchantId = merchantData.id,
            merchantName = merchantData.title,
            productId = productData.id,
            productName = productData.name
        )
        loadProduct()
    }

    private fun loadProduct() {
        productDataLiveData.value = product
        productQuantity.value = product.quantityOrder
        merchantInfo.value = merchantData
        renderProductImage.value = if (product.images.isNotEmpty()) {
            product.images
        } else {
            listOf(product.thumbnailUrl ?: "")
        }

        if (product.optionGroup.isNotEmpty()) {
            showCustomizeOption()
        }

        relatedProductList.clear()
        resetOption()
    }

    fun reloadProduct(productData: ProductItem) {
        product = productData.copy(quantityOrder = 1)
        loadProduct()
        getRelatedProductList()
        checkMerchantType()
        resetOption()
        scrollToTop.value = Unit
    }

    private fun resetOption() {
        product.optionGroup.forEach { optionGroup ->
            optionGroup.options.forEach { optionPick ->
                optionPick.isSelected = false
            }
        }
        product.optionGroupSelected = null
    }

    fun getRelatedProductList() {
        launch {
            val relatedProductResult = getRelatedProductByCategoryUseCase.execute(
                product = product,
                page = currentRelatedProductPage
            )
            if (relatedProductResult is UseCaseResult.Success) {
                showRelatedProduct.value = true
                relatedProductList.addAll(relatedProductResult.data!!.toList())
                relatedProduct.value = relatedProductList
                currentRelatedProductPage++
            } else {
                if (relatedProductList.isEmpty()) {
                    showRelatedProduct.value = false
                }
            }
        }
    }

    fun presentTitleBar() {
        showTitleBar.value = product.name
    }

    fun showCustomizeOption() {
        isShowingCustomizeView = !isShowingCustomizeView
        showCustomizeOptionView.value = isShowingCustomizeView
    }

    fun checkMerchantType() {
        merchantType.value = merchantData.type
    }

    fun setShowTitleProduct(isShow: Boolean) {
        shouldShowProductTitle.value = isShow
    }

    fun getTitleProduct(): String {
        return product.name
    }

    fun addOneItemProduct() {
        product.quantityOrder += 1
        toggleMinusButton.value = true
        productQuantity.value = product.quantityOrder
    }

    fun reduceOneItemProduct() {
        if (product.quantityOrder > 1) {
            product.quantityOrder -= 1
            productQuantity.value = product.quantityOrder
        }
        toggleMinusButton.value = product.quantityOrder > 1
    }

    fun updateProductOption(listOption: List<OptionGroup>, note: String): ProductItem {
        product.optionGroupSelected = listOption
        product.note = note
        return product
    }

    fun updateTotalPrice() {
        var price: Double = product.retailPriceWithTax ?: 0.0
        product.optionGroup.forEach { group ->
            group.options.forEach { option ->
                if (option.isSelected) {
                    price += option.price ?: 0.0
                }
            }
        }
        totalPrice.value = price.times(product.quantityOrder).formatCurrency()
    }

    fun addOneRelatedProduct(position: Int) {
        val listUpdate = relatedProduct.value!!
        listUpdate[position].quantityOrder += 1
        relatedProduct.value = listUpdate
    }

    fun reduceOneRelatedProduct(position: Int) {
        val listUpdate = relatedProduct.value!!
        listUpdate[position].quantityOrder -= 1
        relatedProduct.value = listUpdate
    }

    fun deleteRelatedProduct(position: Int) {
        val listUpdate = relatedProduct.value!!
        listUpdate[position].quantityOrder = 0
        relatedProduct.value = listUpdate
    }

    fun checkProductState() {
        isOutOfStockState.value = product.quantityInStock <= 0
    }
}