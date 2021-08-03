package namit.retail_app.maps.data.api

import namit.retail_app.maps.data.response.GeoLocationResponse
import namit.retail_app.maps.data.response.PlaceDetailResponse
import namit.retail_app.maps.data.response.SearchLocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsApi {

    @GET("maps/api/geocode/json")
    suspend fun loadPlaceDescription(@Query("latlng") latlng: String,
                                     @Query("key") key: String,
                                     @Query("language") language: String = "EN"): GeoLocationResponse

    @GET("maps/api/place/autocomplete/json?components=country:TH")
    suspend fun searchLocation(@Query("input") query: String, @Query("key") key: String): SearchLocationResponse

    @GET("maps/api/place/details/json?fields=geometry")
    suspend fun loadLatLngPlace(
        @Query("place_id") id: String,
        @Query("key") key: String
    ): PlaceDetailResponse
}