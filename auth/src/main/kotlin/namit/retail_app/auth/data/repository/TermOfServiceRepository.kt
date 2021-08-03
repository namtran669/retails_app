package namit.retail_app.auth.data.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import strapi.TermOfServicesQuery

interface TermOfServiceRepository {
    suspend fun loadTermOfService(): String
}

class TermOfServiceRepositoryImpl(private val apollo: ApolloClient) : TermOfServiceRepository {

    override suspend fun loadTermOfService(): String {
        val query = TermOfServicesQuery
            .builder()
            .build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()
        return response.data()?.privacies()?.first()?.Body() ?: ""
    }
}