package namit.retail_app.auth.domain

import namit.retail_app.auth.data.repository.LoginRepository
import namit.retail_app.core.utils.UseCaseResult

interface SendOtpUseCase {
    suspend fun execute(phoneNumber: String): UseCaseResult<Boolean>
}

class SendOtpUseCaseImpl(private val loginRepository: LoginRepository) : SendOtpUseCase {

    companion object {
        const val ERROR_SEND_OTP_CASE = "ERROR_SEND_OTP_CASE"
    }

    override suspend fun execute(phoneNumber: String): UseCaseResult<Boolean> {
        return try {

            val result = loginRepository.loginOtp(phoneNumber)
            if (result) {
                UseCaseResult.Success(true)
            } else {
                UseCaseResult.Error(Throwable(ERROR_SEND_OTP_CASE))
            }

        } catch (e: Exception) {
            UseCaseResult.Error(e)
        }
    }
}