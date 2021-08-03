package namit.retail_app.auth.domain

import namit.retail_app.auth.data.repository.LoginRepository
import namit.retail_app.core.utils.UseCaseResult

interface VerifyOtpUseCase {
    suspend fun execute(phoneNumber: String, otp:String, secureId: String): UseCaseResult<String>
}

class VerifyOtpUseCaseImpl(private val loginRepository: LoginRepository) : VerifyOtpUseCase {

    companion object {
        const val ERROR_VERIFY_OTP_CASE = "ERROR_VERIFY_OTP_CASE"
    }

    override suspend fun execute(phoneNumber: String, otp:String, secureId: String): UseCaseResult<String> {
        return try {
            val accessToken = loginRepository.verifyOtp(phoneNumber, otp, secureId)
            if (!accessToken.isNullOrBlank()) {
                UseCaseResult.Success(accessToken)
            } else {
                UseCaseResult.Error(Throwable(ERROR_VERIFY_OTP_CASE))
            }

        } catch (e: Exception) {
            UseCaseResult.Error(e)
        }
    }
}