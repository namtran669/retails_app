package namit.retail_app.fcm.domain

import android.util.Log
import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.fcm.data.repository.NotificationTokenRepository

interface UpdateNotificationTokenUseCase {
    suspend fun execute(uuid: String, isEnable: Boolean): UseCaseResult<Boolean>
}

class UpdateNotificationTokenUseCaseImpl(private val notificationTokenRepository: NotificationTokenRepository) :
    UpdateNotificationTokenUseCase {

    companion object {
        val TAG: String = UpdateNotificationTokenUseCaseImpl::class.java.simpleName
        const val ERROR_CANNOT_CREATE_NOTIFICATION_TOKEN = "ERROR_CANNOT_CREATE_NOTIFICATION_TOKEN"
    }

    override suspend fun execute(
        uuid: String, isEnable: Boolean
    ): UseCaseResult<Boolean> {
        return try {
            val createResult = notificationTokenRepository.updateFcmNotificationToken(
                uuid = uuid,
                isEnable = isEnable
            )
            if (createResult is RepositoryResult.Success) {
                if (createResult.data == true) {
                    UseCaseResult.Success(createResult.data)
                } else {
                    UseCaseResult.Error(Throwable(ERROR_CANNOT_CREATE_NOTIFICATION_TOKEN))
                }
            } else {
                UseCaseResult.Error(Throwable((createResult as RepositoryResult.Error).message))
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            UseCaseResult.Error(e)
        }
    }
}