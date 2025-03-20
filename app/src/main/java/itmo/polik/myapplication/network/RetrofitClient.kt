package itmo.polik.myapplication.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://json.apiastro.com/"
    private const val API_KEY = "ovVUhG0dYi9H7zjdjAfZC3bfSGdZRJau9ISPLLjm"
    private const val CONTENT_TYPE = "application/json"
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor(API_KEY, CONTENT_TYPE))
        .addInterceptor(loggingInterceptor)
        .build()


    val astroApiService: AstroApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AstroApiService::class.java)
    }
}