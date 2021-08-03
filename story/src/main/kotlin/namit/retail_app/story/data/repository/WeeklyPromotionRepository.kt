package namit.retail_app.story.data.repository

import namit.retail_app.story.data.entity.PromotionContent
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import strapi.PromotionsListQuery

interface WeeklyPromotionRepository {
    suspend fun loadPromotionList(): List<PromotionContent>
}

class WeeklyPromotionRepositoryImpl(private val apollo: ApolloClient) : WeeklyPromotionRepository {

    override suspend fun loadPromotionList(): List<PromotionContent> {
        val query = PromotionsListQuery.builder().build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val promotionList = response.data()?.promotions() ?: listOf()
        val result = mutableListOf<PromotionContent>()
        promotionList.forEach { promotion ->
            result.add(
                PromotionContent(
                    id = promotion.id(),
                    title = promotion.Title(),
                    imageUrl = promotion.Banner()?.url(),
                    createDate = promotion.created_at().toString(),
                    updateDate = promotion.updated_at().toString(),
                    body = promotion.Body() ?: ""
                )
            )
        }
        return result
    }
}