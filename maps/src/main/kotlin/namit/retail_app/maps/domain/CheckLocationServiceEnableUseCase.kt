package namit.retail_app.maps.domain

import android.location.LocationManager
import namit.retail_app.core.utils.UseCaseResult

interface CheckLocationServiceEnableUseCase {
    fun execute(): UseCaseResult<Boolean>
}

class CheckLocationServiceEnableUseCaseImpl(
    private val locationManager: LocationManager
): CheckLocationServiceEnableUseCase {

    companion object {
        private const val ERROR_MESSAGE_CANNOT_GET_USER_LOCATION = "ERROR_MESSAGE_CANNOT_GET_USER_LOCATION"
    }

    override fun execute(): UseCaseResult<Boolean> {
        return try {
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (isGpsEnabled && isNetworkEnabled){
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_MESSAGE_CANNOT_GET_USER_LOCATION))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UseCaseResult.Error(e)
        }
    }

}