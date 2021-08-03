package namit.retail_app.core.domain

import android.util.Log
import namit.retail_app.core.data.repository.UserProfileLocalRepository
import namit.retail_app.core.utils.UseCaseResult

interface DeleteCurrentUserUseCase {
    fun execute(): UseCaseResult<Boolean>
}

class DeleteCurrentUserUseCaseImpl(private val currentUserLocalRepository: UserProfileLocalRepository) :
    DeleteCurrentUserUseCase {

    companion object {
        private val TAG = DeleteCurrentUserUseCaseImpl::class.java.simpleName
        private const val ERROR_CANNOT_DELETE_CURRENT_USER =
            "ERROR_CANNOT_DELETE_CURRENT_USER"
    }

    override fun execute(): UseCaseResult<Boolean> {
        return try {
            currentUserLocalRepository.removeCurrentUser()
            val haveCurrentUser = currentUserLocalRepository.haveCurrentUser()
            if (!haveCurrentUser) {
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_CANNOT_DELETE_CURRENT_USER))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception", e)
            UseCaseResult.Error(e)
        }
    }

}
