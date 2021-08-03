package namit.retail_app.food.presentation.vertical

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.food.domain.GetRestaurantListUseCase
import kotlinx.coroutines.launch

class FoodVerticalViewModel(
    private val getRestaurantListUseCase: GetRestaurantListUseCase
) : BaseViewModel() {

    val restaurantList = MutableLiveData<List<MerchantInfoItem>>()

    private var currentProductList = mutableListOf<MerchantInfoItem>()

    fun loadRestaurantList(lat: Double, lng: Double) {
        //Add empty data to show Skeleton for loading
        val skeletonDataList = mutableListOf<MerchantInfoItem>()
        skeletonDataList.add(MerchantInfoItem())
        restaurantList.value = skeletonDataList

        launch {
            val productListResult =
                getRestaurantListUseCase.execute(
                    lat = lat,
                    lng = lng
                )
            if (productListResult is UseCaseResult.Success) {
                val responseList = productListResult.data!!
                currentProductList.addAll(responseList)

                restaurantList.value = productListResult.data!!
            } else {
                restaurantList.value = currentProductList
            }
        }
    }
}