package namit.retail_app.auth.domain

import namit.retail_app.auth.data.repository.TermOfServiceRepository
import namit.retail_app.core.utils.UseCaseResult

interface GetTermOfServiceUseCase {
    suspend fun execute(): UseCaseResult<String>
}

class GetTermOfServiceUseCaseImpl(private val termOfServiceRepository: TermOfServiceRepository) :
    GetTermOfServiceUseCase {

    companion object {
        const val ERROR_GET_TERM_OF_SERVICE = "ERROR_GET_TERM_OF_SERVICE"
    }

    override suspend fun execute(): UseCaseResult<String> {
        return try {

            val result = termOfServiceRepository.loadTermOfService()
            if (result.isNotEmpty()) {
                UseCaseResult.Success(result)
            } else {
                UseCaseResult.Error(Throwable(ERROR_GET_TERM_OF_SERVICE))
            }

        } catch (e: Exception) {
            UseCaseResult.Error(e)
        }
    }
}