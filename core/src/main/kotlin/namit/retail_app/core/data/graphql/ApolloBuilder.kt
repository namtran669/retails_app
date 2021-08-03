package namit.retail_app.core.data.graphql

import namit.retail_app.core.data.api.BaseUrl
import com.apollographql.apollo.ApolloClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class ApolloBuilder(
    private val baseOkHttpClientBuilder: OkHttpClient.Builder,
    private val defaultBaseUrl: BaseUrl
) {
    fun build(baseUrl: BaseUrl = defaultBaseUrl, vararg interceptors: Interceptor): ApolloClient {
        return ApolloClient.builder()
            .okHttpClient(baseOkHttpClientBuilder.apply {
                interceptors.forEach { interceptor ->
                    this.addInterceptor(interceptor)
                }
            }.build())
            .serverUrl(baseUrl.url)
            .build()
    }
}