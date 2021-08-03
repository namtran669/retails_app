package namit.retail_app.fcm.domain

import android.util.Log
import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.fcm.data.repository.NotificationTokenRepository

interface CreateNotificationTokenUseCase {
    suspend fun execute(uuid: String, fcmToken: String): UseCaseResult<Boolean>
}

class CreateNotificationTokenUseCaseImpl(private val notificationTokenRepository: NotificationTokenRepository) :
    CreateNotificationTokenUseCase {

    companion object {
        val TAG: String = CreateNotificationTokenUseCaseImpl::class.java.simpleName
        const val ERROR_CANNOT_CREATE_NOTIFICATION_TOKEN = "ERROR_CANNOT_CREATE_NOTIFICATION_TOKEN"
    }

    override suspend fun execute(
        uuid: String, fcmToken: String
    ): UseCaseResult<Boolean> {
        return try {
            val createResult = notificationTokenRepository.createFcmNotificationToken(
                uuid = uuid,
                fcmToken = fcmToken
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