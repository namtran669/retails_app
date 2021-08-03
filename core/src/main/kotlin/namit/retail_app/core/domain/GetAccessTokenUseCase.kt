package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.repository.AccessTokenRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetAccessTokenUseCase {
    fun execute(): UseCaseResult<String>
}

class GetAccessTokenUseCaseImpl(private val accessTokenRepository: AccessTokenRepository) :
    GetAccessTokenUseCase {

    companion object {
        private val TAG = GetAccessTokenUseCaseImpl::class.java.simpleName
        private const val ERROR_NO_ACCESS_TOKEN =
            "ERROR_NO_ACCESS_TOKEN"
    }

    override fun execute(): UseCaseResult<String> {
        return try {
            if (accessTokenRepository.hasAccessToken()) {
                val token = accessTokenRepository.getAccessTokenFromLocal()
                UseCaseResult.Success(token)
            } else {
                UseCaseResult.Error(Throwable(ERROR_NO_ACCESS_TOKEN))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}
