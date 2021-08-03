package namit.retail_app.core.data.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HasuraInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val mapHeader = mutableMapOf<String, String>()
        
        val request = createRequestHeader(chain = chain, mapToken = mapHeader)
        return@runBlocking chain.proceed(request)
    }

    private fun createRequestHeader(chain: Interceptor.Chain, mapToken: Map<String, String>): Request {
        val request = chain.request().newBuilder()
        for (key in mapToken.keys) {
            request.addHeader(key, mapToken[key]!!)
        }
        return request.build()
    }
}