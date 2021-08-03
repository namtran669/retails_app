package namit.retail_app.maps.domain

import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.maps.data.model.GeoLocationResult
import namit.retail_app.maps.data.repository.PlaceRepository
import namit.retail_app.maps.data.response.GeoLocation

interface GetPlaceDescriptionUseCase {
    suspend fun execute(latitude: Double, longitude: Double): UseCaseResult<GeoLocationResult>
}

class GetPlaceDescriptionUseCaseImpl(
    private val placeRepository: PlaceRepository
): GetPlaceDescriptionUseCase {

    companion object {
        private const val KEY_COUNTRY = "country"
        private const val KEY_PROVINCE = "administrative_area_level_1"
        private const val KEY_DISTRICT = "sublocality_level_1"
        private const val KEY_SUB_DISTRICT = "sublocality_level_2"
        private const val KEY_ROUTE = "route"
        private const val KEY_STREET_NUMBER = "street_number"
        private const val KEY_POSTAL_CODE = "postal_code"
        private const val ESTABLISHMENT = "establishment"
        private const val POINT_OF_INTEREST = "point_of_interest"
    }

    override suspend fun execute(latitude: Double, longitude: Double): UseCaseResult<GeoLocationResult> =
        try {
            val result = placeRepository.loadPlaceDescription(latitude = latitude, longitude = longitude)

            val address = result.locationList.first()
            val geoLocationResult = GeoLocationResult()
            address.addressList.forEach { geoAddress ->
                when {
                    geoAddress.types.contains(KEY_COUNTRY) -> {
                        geoLocationResult.country = geoAddress.name
                    }
                    geoAddress.types.contains(KEY_PROVINCE) -> {
                        geoLocationResult.province = geoAddress.name
                    }
                    geoAddress.types.contains(KEY_DISTRICT) -> {
                        geoLocationResult.district =
                            geoAddress.name.replace("Khet ", "")
                    }
                    geoAddress.types.contains(KEY_SUB_DISTRICT) -> {
                        geoLocationResult.supDistrict =
                            geoAddress.name.replace("Khwaeng ", "")
                    }
                    geoAddress.types.contains(KEY_ROUTE) -> {
                        geoLocationResult.route = geoAddress.name
                    }
                    geoAddress.types.contains(KEY_STREET_NUMBER) -> {
                        geoLocationResult.streetNumber = geoAddress.name
                    }
                    geoAddress.types.contains(KEY_POSTAL_CODE) -> {
                        geoLocationResult.postalCode = geoAddress.name
                    }
                }

                geoLocationResult.landMark = getLandmark(result.locationList)
            }
            UseCaseResult.Success(geoLocationResult)
        } catch (e: Exception) {
            UseCaseResult.Error(e)
        }

    private fun getLandmark(result: List<GeoLocation>): String {
        var landMark = ""
        result.forEach {
            it.addressList.forEach {
                if(it.types.contains(POINT_OF_INTEREST) || it.types.contains(ESTABLISHMENT)) {
                    landMark = it.name
                    return@forEach
        } } }
        return landMark
    }
}