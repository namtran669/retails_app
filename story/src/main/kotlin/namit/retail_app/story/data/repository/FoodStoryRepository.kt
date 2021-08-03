package namit.retail_app.story.data.repository

import namit.retail_app.story.data.entity.StoryContent
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import strapi.ArticleListQuery

interface FoodStoryRepository {
    suspend fun loadFoodStories(category: String): List<StoryContent>
}

class FoodStoryRepositoryImpl(private val apollo: ApolloClient) : FoodStoryRepository {

    companion object {
        const val KEY_CONTENT_FOOD_STORY = "Food Stories"
    }

    override suspend fun loadFoodStories(category: String): List<StoryContent> {
        val query = ArticleListQuery.builder().build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val articleList = response.data()?.articles() ?: listOf()
        val result = mutableListOf<StoryContent>()
        articleList.forEach { article ->
            result.add(
                StoryContent(
                    id = article.id(),
                    title = article.Title(),
                    imageUrl = article.Banner()?.url(),
                    createDate = article.created_at().toString(),
                    updateDate = article.updated_at().toString(),
                    article = article.Body(),
                    description = "",
                    category = "",
                    position = 0
                )
            )
        }
        return result
    }
}