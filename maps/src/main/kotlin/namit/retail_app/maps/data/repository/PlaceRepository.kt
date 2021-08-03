package namit.retail_app.maps.data.repository

import namit.retail_app.maps.BuildConfig
import namit.retail_app.maps.data.api.MapsApi
import namit.retail_app.maps.data.response.GeoLocationResponse
import namit.retail_app.maps.data.response.PlaceDetailResponse

interface PlaceRepository {
    suspend fun loadPlaceDescription(latitude: Double, longitude: Double): GeoLocationResponse

    suspend fun loadLatLngPlace(placeId: String): PlaceDetailResponse
}

class PlaceRepositoryImpl(private val api: MapsApi):
    PlaceRepository {

    override suspend fun loadPlaceDescription(
        latitude: Double,
        longitude: Double
    ): GeoLocationResponse {
        return api.loadPlaceDescription(
            latlng = "$latitude,$longitude",
            key = BuildConfig.GOOGLE_CLOUD_API_KEY)

    }

    override suspend fun loadLatLngPlace(placeId: String): PlaceDetailResponse {
        return api.loadLatLngPlace(id = placeId, key = BuildConfig.GOOGLE_CLOUD_API_KEY)
    }

}