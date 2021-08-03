package namit.retail_app.grocery.presentation.category_detail

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.data.repository.ProductRepositoryImpl
import namit.retail_app.core.domain.GetProductByCategoryUseCase
import namit.retail_app.core.domain.GetProductByCategoryUseCaseImpl.Companion.ERROR_EMPTY_PRODUCT_CASE
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.tracking.EventTrackingManager
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class GroceryCategoryDetailViewModel(
    private val selectedCategoryData: CategoryItem,
    private val merchantData: MerchantInfoItem,
    private val getProductByCategoryUseCase: GetProductByCategoryUseCase,
    private val eventTrackingManager: EventTrackingManager
) : BaseViewModel() {

    val productList = MutableLiveData<MutableList<ProductItem>>()
    val showHaveNoMoreProduct = MutableLiveData<Boolean>()
    val currentCategory = MutableLiveData<CategoryItem>()
    val openProductDetail = SingleLiveEvent<Pair<ProductItem, MerchantInfoItem>>()

    var currentProductPage: Int = ProductRepositoryImpl.FIRST_PAGE
    private var currentProductList = mutableListOf<ProductItem>()
    private var isNoMoreProductData = false
    private var isProductListLoading = false

    init {
        currentCategory.value = selectedCategoryData
        eventTrackingManager.trackCategory(
            merchantId = selectedCategoryData.merchantId,
            merchantName = selectedCategoryData.merchantName,
            categoryId = selectedCategoryData.id,
            categoryName = selectedCategoryData.nameEn
        )
    }

    fun loadProductList() {
        if (isNoMoreProductData || isProductListLoading) {
            return
        } else {
            isProductListLoading = true
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

        launch {
            isProductListLoading = true
            val productListResult = getProductByCategoryUseCase.execute(
                selectedCategoryData.merchantId,
                listOf(selectedCategoryData.id),
                page = currentProductPage
            )

            if (productListResult is UseCaseResult.Success) {
                val newProductList = productListResult.data!!
                currentProductList.addAll(newProductList)

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
        openProductDetail.value = Pair(first = productItem, second = merchantData)
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
}