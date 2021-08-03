package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.entity.UserModel
import namit.retail_app.core.data.repository.UserProfileLocalRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetUserProfileLocalUseCase {
    fun execute(): UseCaseResult<UserModel>
}

class GetUserProfileLocalUseCaseImpl(private val currentUserLocalRepository: UserProfileLocalRepository) :
    GetUserProfileLocalUseCase {

    companion object {
        private val TAG = GetUserProfileLocalUseCaseImpl::class.java.simpleName
        private const val ERROR_NO_CURRENT_USER =
            "ERROR_NO_CURRENT_USER"
    }

    override fun execute(): UseCaseResult<UserModel> {
        return try {
            if (currentUserLocalRepository.haveCurrentUser()) {
                val user = currentUserLocalRepository.getCurrentUser()
                UseCaseResult.Success(user)
            } else {
                UseCaseResult.Error(Throwable(ERROR_NO_CURRENT_USER))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}
