package com.onebilliongod.android.jetpackandroid.data.remote.api

import com.onebilliongod.android.jetpackandroid.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkFactory {
    private const val REQUEST_CONNECT_TIME: Long = 3
    private const val REQUEST_WRITE_TIME: Long = 5
    private const val REQUEST_READ_TIME: Long = 5

    //
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //
    fun createOkHttpClient(authInterceptor: AuthInterceptor,
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

    fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}