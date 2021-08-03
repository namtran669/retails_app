package namit.retail_app.auth.domain

import namit.retail_app.auth.data.repository.LoginRepository
import namit.retail_app.core.data.entity.UserModel
import namit.retail_app.core.utils.UseCaseResult

interface GetUserProfileUserCase {
    suspend fun execute(): UseCaseResult<UserModel>
}

class GetUserProfileUserCaseImpl(private val loginRepository: LoginRepository) :
    GetUserProfileUserCase {

    companion object {
        const val ERROR_GET_USER_INFO = "ERROR_GET_USER_INFO"
    }

    override suspend fun execute(): UseCaseResult<UserModel> {
        return try {

            val result = loginRepository.getUserProfile()
            if (result.id != -1) {
                UseCaseResult.Success(result)
            } else {
                UseCaseResult.Error(Throwable(ERROR_GET_USER_INFO))
            }

        } catch (e: Exception) {
            UseCaseResult.Error(e)
        }
    }
}