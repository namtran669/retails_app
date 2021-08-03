package namit.retail_app.home.data.repository

import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.home.data.entity.WeatherModel
import namit.retail_app.home.enums.DaySessionType
import namit.retail_app.home.enums.WeatherType
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.GetWeatherQuery
import java.util.*

interface WeatherRepository {
    suspend fun getCurrentWeather(
        lat: Double, lng: Double
    ): RepositoryResult<WeatherModel>
}

class WeatherRepositoryImpl(private val apolloUnAuth: ApolloClient) : WeatherRepository {

    companion object {
        //darksky weather key
        const val CLEAR_DAY_WEATHER = "clear-day"
        const val CLEAR_NIGHT_WEATHER = "clear-night"
        const val PARTLY_CLOUDY_DAY_WEATHER = "partly-cloudy-day"
        const val PARTLY_CLOUDY_NIGHT_WEATHER = "partly-cloudy-night"
        const val CLOUDY_WEATHER = "cloudy"
        const val RAIN_WEATHER = "rain"
        const val THUNDER_STORM_WEATHER = "thunderstorm"
        const val TORNADO_WEATHER = "tornado"
        const val HAIL_WEATHER = "hail"
    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lng: Double
    ): RepositoryResult<WeatherModel> {

        val query = GetWeatherQuery.builder()
            .lat(lat)
            .lng(lng).build()
        val deferred = apolloUnAuth.query(query).toDeferred()
        val response = deferred.await()
        return if (response.hasErrors()) {
            RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            response.data()?.weather()?.currently()?.let { currentWeather ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = currentWeather.time().toString().toLong() * 1000
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                var daySession = if (currentHour in 6..11) {
                    DaySessionType.MORNING
                } else if (currentHour in 12..17) {
                    DaySessionType.AFTERNOON
                } else if (currentHour in 18..23) {
                    DaySessionType.EVENING
                } else {
                    DaySessionType.NIGHT
                }

                val weatherType = when (currentWeather.icon()) {
                    CLEAR_DAY_WEATHER -> WeatherType.CLEAR_SUN
                    CLEAR_NIGHT_WEATHER -> WeatherType.CLEAR_MOON
                    PARTLY_CLOUDY_DAY_WEATHER -> WeatherType.PARTLY_CLOUDY_SUN
                    PARTLY_CLOUDY_NIGHT_WEATHER -> WeatherType.PARTLY_CLOUDY_MOON
                    CLOUDY_WEATHER -> WeatherType.CLOUDY
                    RAIN_WEATHER -> WeatherType.RAIN
                    THUNDER_STORM_WEATHER -> WeatherType.STORM
                    TORNADO_WEATHER -> WeatherType.STORM
                    HAIL_WEATHER -> WeatherType.STORM
                    else -> {
                        daySession = DaySessionType.MORNING
                        WeatherType.UNKNOWN
                    }
                }

                RepositoryResult.Success(WeatherModel(type = weatherType, session = daySession))
            } ?: kotlin.run {
                RepositoryResult.Success(WeatherModel())
            }
        }
    }

}