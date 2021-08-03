package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.repository.UuidRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetUuidUseCase {
    suspend fun execute(): UseCaseResult<String>
}

class GetUuidUseCaseImpl(private val uuidRepository: UuidRepository) :
    GetUuidUseCase {

    companion object {
        private val TAG = GetUuidUseCaseImpl::class.java.simpleName
        const val ERROR_CANNOT_GET_UUID = "ERROR_CANNOT_GET_UUID"
    }

    override suspend fun execute(): UseCaseResult<String> {
        return try {
            if (uuidRepository.haveUuid()) {
                UseCaseResult.Success(uuidRepository.getUuid())
            } else {
                uuidRepository.generateUuid()
                if (uuidRepository.haveUuid()) {
                    UseCaseResult.Success(uuidRepository.getUuid())
                } else {
                    UseCaseResult.Error(Throwable(ERROR_CANNOT_GET_UUID))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }
}
