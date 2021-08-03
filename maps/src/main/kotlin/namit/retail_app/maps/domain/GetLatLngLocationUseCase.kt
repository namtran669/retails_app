package namit.retail_app.maps.domain

import android.util.Log
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.core.utils.UseCaseResult.Success
import namit.retail_app.maps.data.repository.PlaceRepository
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject

interface GetLatLngLocationUseCase {
    suspend fun execute(id: String): UseCaseResult<LatLng>
}

class GetLatLngPlaceUseCaseImpl(private val placeRepository: PlaceRepository) :
    GetLatLngLocationUseCase {

    companion object {
        private val TAG = GetLatLngPlaceUseCaseImpl::class.java.simpleName
        private const val ERROR_USER_ADDRESS_LIST_EMPTY =
            "ERROR_USER_ADDRESS_LIST_EMPTY"
    }

    override suspend fun execute(id: String): UseCaseResult<LatLng> {
        return try {
            val locationJson = placeRepository.loadLatLngPlace(id)
            if (locationJson.detail.place.isJsonObject) {
                val location = locationJson.detail.place.get("location")
                if (location != null && location.isJsonObject) {
                    val lat = (location as JsonObject).get("lat").asDouble
                    val lng = location.get("lng").asDouble
                    Success(LatLng(lat, lng))
                } else {
                    UseCaseResult.Error(Throwable(ERROR_USER_ADDRESS_LIST_EMPTY))
                }
            } else {
                UseCaseResult.Error(Throwable(ERROR_USER_ADDRESS_LIST_EMPTY))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}