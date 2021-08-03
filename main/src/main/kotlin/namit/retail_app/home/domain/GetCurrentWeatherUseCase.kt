package namit.retail_app.home.domain

import android.util.Log
import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.home.data.entity.WeatherModel
import namit.retail_app.home.data.repository.WeatherRepository

interface GetCurrentWeatherUseCase {
    suspend fun execute(lat: Double, lng: Double): UseCaseResult<WeatherModel>
}

class GetCurrentWeatherUseCaseImpl(private val weatherRepository: WeatherRepository) :
    GetCurrentWeatherUseCase {

    companion object {
        private val TAG = GetCurrentWeatherUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_GET_CURRENT_WEATHER =
            "ERROR_CANNOT_GET_CURRENT_WEATHER"
    }

    override suspend fun execute(lat: Double, lng: Double): UseCaseResult<WeatherModel> {
        return try {
            val repoResult = weatherRepository.getCurrentWeather(lat = lat, lng = lng)
            if (repoResult is RepositoryResult.Success) {
                if (repoResult.data != null) {
                    UseCaseResult.Success(repoResult.data!!)
                } else {
                    UseCaseResult.Error(Throwable(ERROR_CANNOT_GET_CURRENT_WEATHER))
                }
            } else {
                UseCaseResult.Error(Throwable((repoResult as RepositoryResult.Error).message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}