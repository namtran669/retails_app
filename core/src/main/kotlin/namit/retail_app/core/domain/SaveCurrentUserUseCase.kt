package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.UserModel
import namit.retail_app.core.data.repository.UserProfileLocalRepository
import namit.retail_app.core.utils.UseCaseResult

interface SaveCurrentUserUseCase {
    fun execute(user: UserModel): UseCaseResult<Boolean>
}

class SaveCurrentUserUseCaseImpl(private val currentUserLocalRepository: UserProfileLocalRepository) :
    SaveCurrentUserUseCase {

    companion object {
        private val TAG = SaveCurrentUserUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_SAVE_CURRENT_USER =
            "ERROR_CANNOT_SAVE_CURRENT_USER"
    }

    override fun execute(user: UserModel): UseCaseResult<Boolean> {
        return try {
            currentUserLocalRepository.saveCurrentUser(user)
            val saveResult = currentUserLocalRepository.haveCurrentUser()
            if (saveResult) {
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_SAVE_CURRENT_USER))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }
}
