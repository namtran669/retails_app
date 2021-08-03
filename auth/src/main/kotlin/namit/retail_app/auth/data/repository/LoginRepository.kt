package namit.retail_app.auth.data.repository

import namit.retail_app.core.data.entity.UserModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.AuthenticationGetUserProfileQuery
import hasura.AuthenticationLoginOtpMutation
import hasura.AuthenticationVerifyOTPMutation

interface LoginRepository {
    suspend fun loginOtp(phoneNumber: String): Boolean
    suspend fun verifyOtp(phoneNumber: String, otp: String, secureId: String): String?
    suspend fun getUserProfile(): UserModel
}

class LoginRepositoryImpl(private val apollo: ApolloClient) : LoginRepository {

    companion object {
        const val SUCCESS_CODE = 202
    }

    override suspend fun loginOtp(phoneNumber: String): Boolean {
        val mutate =
            AuthenticationLoginOtpMutation
                .builder()
                .phoneNumber(phoneNumber)
                .build()
        val deferred = apollo.mutate(mutate).toDeferred()
        val response = deferred.await()

        val sendOtpResult = response.data()?.loginOtp()

        sendOtpResult?.code()?.let {
            if (it == SUCCESS_CODE) {
                return true
            }
        }

        return false
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String, secureId: String): String? {
        val mutate =
            AuthenticationVerifyOTPMutation
                .builder()
                .phoneNumber(phoneNumber)
                .otp(otp)
                .secureId(secureId)
                .build()
        val deferred = apollo.mutate(mutate).toDeferred()
        val response = deferred.await()

        val verifyOtpResult = response.data()?.verifyOtp()

        verifyOtpResult?.access_token()?.let {
            return it
        }

        return null
    }

    override suspend fun getUserProfile(): UserModel {
        val query = AuthenticationGetUserProfileQuery.builder().build()
        val deferred = apollo.query(query).toDeferred()
        val response = deferred.await()

        val result = response.data()?.profile
        return UserModel(result?.id() ?: -1, result?.phone_number() ?: "")

    }

}