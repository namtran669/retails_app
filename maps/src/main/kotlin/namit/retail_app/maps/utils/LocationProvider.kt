package namit.retail_app.maps.utils

import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import io.reactivex.Observable

private const val DEFAULT_GET_LOCATION_INTERVAL = 5000L

interface LocationProvider {
    fun execute(interval: Long = DEFAULT_GET_LOCATION_INTERVAL): Observable<Location>
}

class LocationProviderImpl(private val rxLocation: RxLocation): LocationProvider {

    override fun execute(interval: Long): Observable<Location> {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(interval)
        return rxLocation
            .location()
            .updates(locationRequest)
    }

}