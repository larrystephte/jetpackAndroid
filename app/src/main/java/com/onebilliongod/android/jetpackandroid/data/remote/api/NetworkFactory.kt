package com.onebilliongod.android.jetpackandroid.data.remote.api

import com.onebilliongod.android.jetpackandroid.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * NetworkFactory is a singleton object responsible for creating network-related instances.
 * It provides factory methods to create and configure OkHttpClient and Retrofit instances.
 */
object NetworkFactory {
    private const val REQUEST_CONNECT_TIME: Long = 3
    private const val REQUEST_WRITE_TIME: Long = 5
    private const val REQUEST_READ_TIME: Long = 5


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Creates an OkHttpClient instance with provided interceptors.
     *
     * @param authInterceptor An interceptor for handling authentication (e.g., adding tokens).
     * @param headersInterceptor An interceptor for adding common headers to each request.
     * @return Configured OkHttpClient instance.
     */
    private fun createOkHttpClient(authInterceptor: AuthInterceptor,
                           headersInterceptor: HeadersInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(headersInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(REQUEST_CONNECT_TIME, TimeUnit.SECONDS)
            .readTimeout(REQUEST_READ_TIME, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_WRITE_TIME, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Creates a Retrofit instance with a provided headers interceptor.
     *
     * @param headersInterceptor An interceptor for adding common headers to each request.
     * @return Configured Retrofit instance with the OkHttpClient.
     */
    fun createRetrofit(headersInterceptor: HeadersInterceptor): Retrofit {
        val okHttpClient = createOkHttpClient(AuthInterceptor(), headersInterceptor)
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}