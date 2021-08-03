package namit.retail_app.grocery.presentation.category_sub_detail

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.data.repository.ProductRepositoryImpl
import namit.retail_app.core.domain.GetProductByCategoryUseCase
import namit.retail_app.core.domain.GetProductByCategoryUseCaseImpl.Companion.ERROR_EMPTY_PRODUCT_CASE
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class GrocerySubCategoryDetailViewModel(
    private val rootCategoryData: CategoryItem,
    private val selectedCategoryData: CategoryItem,
    private val getProductByCategoryUseCase: GetProductByCategoryUseCase
) : BaseViewModel() {

    val productList = MutableLiveData<MutableList<ProductItem>>()
    val showHaveNoMoreProduct = MutableLiveData<Boolean>()
    val rootCategory = MutableLiveData<CategoryItem>()

    private var categoryIds = mutableListOf(selectedCategoryData.id)
    private var currentProductPage: Int = ProductRepositoryImpl.FIRST_PAGE
    private var currentProductList = mutableListOf<ProductItem>()
    private var isNoMoreProductData = false
    private var isProductListLoading = false

    init {
        rootCategory.value = rootCategoryData
    }

    fun loadProductList() {
        if (isNoMoreProductData || isProductListLoading) {
            return
        }

        if (currentProductPage == ProductRepositoryImpl.FIRST_PAGE) {
            //todo handle skeleton when the skeleton UI for this part is ready
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
                merchantId = rootCategoryData.merchantId,
                page = currentProductPage,
                categoryIds = categoryIds
            )

            if (productListResult is UseCaseResult.Success) {
                val newProductList = productListResult.data!!
                newProductList.forEach {
                    it.quantityOrder = 0
                    currentProductList.add(it)
                }

                val productListForRender = mutableListOf<ProductItem>()
                productListForRender.addAll(currentProductList)

                if (newProductList.size < ProductRepositoryImpl.SIZE_PRODUCT_EACH_REQUEST) {
                    isNoMoreProductData = true
                    showHaveNoMoreProduct.value = true
                } else {
                    //todo handle skeleton when the skeleton UI for this part is ready
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

    private fun removeLastItemInBreadcrumb() {
        rootCategoryData.breadcrumbChildList?.let {
            if (it.size > 0) {
                it.removeAt(it.size - 1)
            }
        }
    }

    override fun onCleared() {
        removeLastItemInBreadcrumb()
        super.onCleared()
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