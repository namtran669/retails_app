package namit.retail_app.core.utils

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

object LocationUtils {

    fun distance(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Double {
        val theta = lng1 - lng2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}