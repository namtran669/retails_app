package namit.retail_app.food.domain

import android.util.Log
import namit.retail_app.core.data.entity.MerchantInfoItem
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.food.data.repository.RestaurantRepository

interface GetRestaurantListUseCase {
    suspend fun execute(lat: Double, lng: Double): UseCaseResult<List<MerchantInfoItem>>
}

class GetRestaurantListUseCaseImpl(
    private val restaurantRepository: RestaurantRepository
) : GetRestaurantListUseCase {

    companion object {
        val TAG = GetRestaurantListUseCase::class.java.simpleName
        const val ERROR_EMPTY_RESTAURANT_CASE = "ERROR_EMPTY_RESTAURANT_CASE"
    }

    override suspend fun execute(lat: Double, lng: Double): UseCaseResult<List<MerchantInfoItem>> {
        return try {
            val restaurantList = restaurantRepository.getRestaurantList(lat, lng)
            if (restaurantList.isNotEmpty()) {
                UseCaseResult.Success(restaurantList)
            } else {
                UseCaseResult.Error(Throwable(ERROR_EMPTY_RESTAURANT_CASE))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }

}
