package namit.retail_app.story.domain

import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.story.data.entity.PromotionContent
import namit.retail_app.story.data.repository.WeeklyPromotionRepository

interface GetWeeklyPromotionUseCase {
    suspend fun execute(): UseCaseResult<List<PromotionContent>>
}

class GetWeeklyPromotionUseCaseImpl(
    private val weeklyPromotionRepository: WeeklyPromotionRepository
): GetWeeklyPromotionUseCase {

    companion object {
        const val ERROR_EMPTY_PROMOTION_CASE = "ERROR_EMPTY_PROMOTION_CASE"
    }

    override suspend fun execute(): UseCaseResult<List<PromotionContent>> {
        return try {
            val foodStoryList = weeklyPromotionRepository.loadPromotionList()
            if (foodStoryList.isNotEmpty()) {
                UseCaseResult.Success(foodStoryList)
            } else {
                UseCaseResult.Error(Throwable(ERROR_EMPTY_PROMOTION_CASE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UseCaseResult.Error(Throwable(e.message))
        }
    }

}