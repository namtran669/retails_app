package namit.retail_app.story.data.repository

import namit.retail_app.story.data.entity.AnnoucementContent
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import strapi.AnnoucementListQuery

interface AnnoucementRepository {
    suspend fun loadAnnoucementList(): List<AnnoucementContent>
}

class AnnoucementRepositoryImpl(private val apollo: ApolloClient) : AnnoucementRepository {

    override suspend fun loadAnnoucementList(): List<AnnoucementContent> {
        val query = AnnoucementListQuery.builder().build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        val articleList = response.data()?.annoucements() ?: listOf()
        val result = mutableListOf<AnnoucementContent>()
        articleList.forEach { annoucement ->
            result.add(
                AnnoucementContent(
                    id = annoucement.id(),
                    title = annoucement.Announcements(),
                    imageUrl = annoucement.Image()?.url(),
                    createDate = annoucement.created_at().toString(),
                    updateDate = annoucement.updated_at().toString(),
                    annoucement = annoucement.Body() ?: ""
                )
            )
        }
        return result
    }
}