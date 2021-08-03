package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.repository.AccessTokenRepository
import namit.retail_app.core.utils.UseCaseResult

interface RemoveAccessTokenUseCase {
    fun execute(): UseCaseResult<Boolean>
}

class RemoveAccessTokenUseCaseImpl(private val accessTokenRepository: AccessTokenRepository) :
    RemoveAccessTokenUseCase {

    companion object {
        private val TAG = RemoveAccessTokenUseCase::class.java.simpleName
        private const val ERROR_CANNOT_DELETE_CURRENT_ACCESS_TOKEN =
            "ERROR_CANNOT_DELETE_CURRENT_ACCESS_TOKEN"
    }

    override fun execute(): UseCaseResult<Boolean> {
        return try {
            accessTokenRepository.removeAccessToken()
            val hasAccessToken = accessTokenRepository.hasAccessToken()
            if (!hasAccessToken) {
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_DELETE_CURRENT_ACCESS_TOKEN))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}
