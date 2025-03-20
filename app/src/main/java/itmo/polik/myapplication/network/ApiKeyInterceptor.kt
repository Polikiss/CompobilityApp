package itmo.polik.myapplication.network

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(private val apiKey: String, private val contentType: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithApiKey = originalRequest.newBuilder()
            .header("x-api-key", apiKey)
            .header("Content-Type", contentType)
            .build()
        return chain.proceed(requestWithApiKey)
    }
}