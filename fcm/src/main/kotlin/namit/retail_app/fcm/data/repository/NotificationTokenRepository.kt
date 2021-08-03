package namit.retail_app.fcm.data.repository

import namit.retail_app.core.utils.RepositoryResult
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.CreateNotificationTokenMutation
import hasura.UpdateNotificationTokenMutation
import hasura.type.Status

interface NotificationTokenRepository {
    suspend fun createFcmNotificationToken(
        uuid: String,
        fcmToken: String
    ): RepositoryResult<Boolean>

    suspend fun updateFcmNotificationToken(
        uuid: String,
        isEnable: Boolean
    ): RepositoryResult<Boolean>
}

class NotificationTokenRepositoryImpl(private val apolloAuth: ApolloClient, private val apolloUnAuth: ApolloClient) :
    NotificationTokenRepository {

    override suspend fun createFcmNotificationToken(
        uuid: String,
        fcmToken: String
    ): RepositoryResult<Boolean> {
        val mutate = CreateNotificationTokenMutation.builder()
            .uuid(uuid)
            .token(fcmToken).build()

        val deferred = apolloUnAuth.mutate(mutate).toDeferred()
        val response = deferred.await()
        if (response.hasErrors()) {
            return RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            val result = response.data()?.createNotificationToken()
            if (result?.token()?.isNotEmpty() == true) {
                return RepositoryResult.Success(true)
            }
            return RepositoryResult.Success(false)
        }
    }

    override suspend fun updateFcmNotificationToken(
        uuid: String,
        isEnable: Boolean
    ): RepositoryResult<Boolean> {
        val apolloStatus = if (isEnable) {
            Status.LOGIN
        } else {
            Status.LOGOUT
        }
        val mutate = UpdateNotificationTokenMutation.builder()
            .uuid(uuid)
            .status(apolloStatus).build()

        val deferred = apolloAuth.mutate(mutate).toDeferred()
        val response = deferred.await()
        if (response.hasErrors()) {
            return RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            val result = response.data()?.updateNotificationToken()
            if (result?.token()?.isNotEmpty() == true) {
                return RepositoryResult.Success(true)
            }
            return RepositoryResult.Success(false)
        }
    }

}
