package namit.retail_app.core.data.interceptor

import namit.retail_app.core.domain.GetAccessTokenUseCase
import namit.retail_app.core.extension.base64ToPlain
import namit.retail_app.core.utils.UseCaseResult
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthHeaderInterceptor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase
) : Interceptor {

    companion object {
        const val AUTHORIZATION_HEADER = "QXV0aG9yaXphdGlvbg=="
    }

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val mapHeader = mutableMapOf<String, String>()

        val getAccessTokenResult = getAccessTokenUseCase.execute()
        if (getAccessTokenResult is UseCaseResult.Success && !getAccessTokenResult.data.isNullOrBlank()) {
            mapHeader[AUTHORIZATION_HEADER.base64ToPlain()] = getAccessTokenResult.data
        }
        val request = createRequestHeader(chain = chain, mapToken = mapHeader)
        return@runBlocking chain.proceed(request)
    }

    private fun createRequestHeader(
        chain: Interceptor.Chain,
        mapToken: Map<String, String>
    ): Request {
        val request = chain.request().newBuilder()
        for (key in mapToken.keys) {
            request.addHeader(key, mapToken[key] ?: error(""))
        }
        return request.build()
    }
}