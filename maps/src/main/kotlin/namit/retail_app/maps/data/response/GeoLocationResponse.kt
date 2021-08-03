package namit.retail_app.maps.data.response

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class GeoLocationResponse(
    @SerializedName("results")
    var locationList: List<GeoLocation>
)

data class PlaceDetailResponse(
    @SerializedName("result")
    var detail: PlaceLatLng
)

data class PlaceLatLng(
    @SerializedName("geometry")
    var place: JsonObject
)

data class GeoLocation(
    @SerializedName("address_components")
    var addressList: List<GeoAddress>
)

data class GeoAddress(
    @SerializedName("long_name")
    var name: String,
    @SerializedName("types")
    var types: List<String>
)

data class SearchLocationResponse(
    @SerializedName("predictions")
    var locationList: JsonArray
)
