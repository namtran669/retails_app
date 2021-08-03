package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.repository.AccessTokenRepository
import namit.retail_app.core.utils.UseCaseResult

interface SaveAccessTokenUseCase {
    fun execute(token: String): UseCaseResult<Boolean>
}

class SaveAccessTokenUseCaseImpl(private val accessTokenRepository: AccessTokenRepository) :
    SaveAccessTokenUseCase {

    companion object {
        private val TAG = SaveCurrentUserUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_SAVE_ACCESS_TOKEN =
            "ERROR_CANNOT_SAVE_ACCESS_TOKEN"
    }

    override fun execute(token: String): UseCaseResult<Boolean> {
        return try {
            accessTokenRepository.saveAccessTokenToLocal(token)
            val saveResult = accessTokenRepository.hasAccessToken()
            if (saveResult) {
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_SAVE_ACCESS_TOKEN))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }
}