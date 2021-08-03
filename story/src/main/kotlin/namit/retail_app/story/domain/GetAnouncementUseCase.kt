package namit.retail_app.story.domain

import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.story.data.entity.AnnoucementContent
import namit.retail_app.story.data.repository.AnnoucementRepository

interface GetAnouncementUseCase {
    suspend fun execute(): UseCaseResult<List<AnnoucementContent>>
}

class GetAnouncementUseCaseImpl(
    private val annoucementRepository: AnnoucementRepository
): GetAnouncementUseCase {

    companion object {
        const val ERROR_EMPTY_ANNOUCEMENT_CASE = "ERROR_EMPTY_ANNOUCEMENT_CASE"
    }

    override suspend fun execute(): UseCaseResult<List<AnnoucementContent>> {
        return try {
            val foodStoryList = annoucementRepository.loadAnnoucementList()
            if (foodStoryList.isNotEmpty()) {
                UseCaseResult.Success(foodStoryList)
            } else {
                UseCaseResult.Error(Throwable(ERROR_EMPTY_ANNOUCEMENT_CASE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UseCaseResult.Error(Throwable(e.message))
        }
    }

}