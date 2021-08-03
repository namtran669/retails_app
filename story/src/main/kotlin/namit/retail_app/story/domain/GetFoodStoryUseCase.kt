package namit.retail_app.story.domain

import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.story.data.entity.StoryContent
import namit.retail_app.story.data.repository.FoodStoryRepository

interface GetFoodStoryUseCase {
    suspend fun execute(category: String): UseCaseResult<List<StoryContent>>
}

class GetFoodStoryUseCaseImpl(
    private val foodStoryRepository: FoodStoryRepository
): GetFoodStoryUseCase {

    companion object {
        const val ERROR_EMPTY_FOOD_STORY_CASE = "ERROR_EMPTY_FOOD_STORY_CASE"
    }

    override suspend fun execute(category: String): UseCaseResult<List<StoryContent>> {
        return try {
            val foodStoryList = foodStoryRepository.loadFoodStories(category = category)
            if (foodStoryList.isNotEmpty()) {
                UseCaseResult.Success(foodStoryList)
            } else {
                UseCaseResult.Error(Throwable(ERROR_EMPTY_FOOD_STORY_CASE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            UseCaseResult.Error(Throwable(e.message))
        }
    }
}