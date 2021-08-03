package namit.retail_app.food.presentation.restaurant.menu

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.CategoryItem
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.data.entity.ProductItem
import namit.retail_app.core.data.repository.ProductRepositoryImpl
import namit.retail_app.core.domain.GetProductByCategoryUseCase
import namit.retail_app.core.domain.GetProductByCategoryUseCaseImpl.Companion.ERROR_EMPTY_PRODUCT_CASE
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.launch

class MenuPageViewModel(
    private val restaurantData: MerchantInfoItem,
    private val categoryData: CategoryItem,
    private val getProductByCategoryUseCase: GetProductByCategoryUseCase
) : BaseViewModel() {

    val foodList = MutableLiveData<List<ProductItem>>()
    val showHaveNoMoreProduct = MutableLiveData<Boolean>()
    val openFoodDetailDialog = SingleLiveEvent<Pair<MerchantInfoItem, ProductItem>>()

    private val currentFoodListData = mutableListOf<ProductItem>()
    private var currentProductPage: Int = ProductRepositoryImpl.FIRST_PAGE
    private var isNoMoreProductData = false
    private var isProductListLoading = false

    fun loadFoodListFirstTime() {
        if (currentFoodListData.isEmpty()) {
            loadFoodList()
        } else {
            foodList.value = currentFoodListData
        }
    }

    fun loadFoodList() {
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
            foodList.value = skeletonDataList
        }

        launch {
            isProductListLoading = true
            val productListResult = getProductByCategoryUseCase.execute(
                restaurantData.id,
                listOf(categoryData.id),
                currentProductPage
            )

            if (productListResult is UseCaseResult.Success) {
                val newProductList = productListResult.data!!
                currentFoodListData.addAll(newProductList)

                val productListForRender = mutableListOf<ProductItem>()
                productListForRender.addAll(currentFoodListData)

                if (newProductList.size < ProductRepositoryImpl.SIZE_PRODUCT_EACH_REQUEST) {
                    isNoMoreProductData = true
                    showHaveNoMoreProduct.value = true
                } else {
                    //Add a skeleton item on bottom
                    productListForRender.add(ProductItem())
                }

                foodList.value = productListForRender
                currentProductPage++

            } else if (productListResult is UseCaseResult.Error && productListResult.exception.message == ERROR_EMPTY_PRODUCT_CASE) {

                isNoMoreProductData = true
                showHaveNoMoreProduct.value = true
                foodList.value = currentFoodListData
            }
            isProductListLoading = false
        }

    }

    fun presentFoodDetailDialog(foodSelect: ProductItem) {
        openFoodDetailDialog.value = Pair(first = restaurantData, second = foodSelect)
    }

}