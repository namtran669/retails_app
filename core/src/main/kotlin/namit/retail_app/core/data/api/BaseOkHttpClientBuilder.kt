package namit.retail_app.core.data.api
import namit.retail_app.core.BuildConfig
import namit.retail_app.core.data.interceptor.HasuraInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class BaseOkHttpClientBuilder(private val hasuraInterceptor: HasuraInterceptor? = null) {

    fun init(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .readTimeout(HTTP_TIMEOUT_SECOND, TimeUnit.SECONDS)
            .connectTimeout(HTTP_TIMEOUT_SECOND, TimeUnit.SECONDS)
            .apply {
                hasuraInterceptor?.let {
                    addInterceptor(it)
                }

                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(logging)
                }
            }
    }
}