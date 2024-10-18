package com.onebilliongod.android.jetpackandroid.di

import com.onebilliongod.android.jetpackandroid.data.local.PreferencesManager
import com.onebilliongod.android.jetpackandroid.data.remote.api.AuthInterceptor
import com.onebilliongod.android.jetpackandroid.data.remote.api.HeadersInterceptor
import com.onebilliongod.android.jetpackandroid.data.remote.api.NetworkFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor()
    }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(preferencesManager: PreferencesManager): HeadersInterceptor {
        return HeadersInterceptor(preferencesManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor, headersInterceptor: HeadersInterceptor): OkHttpClient {
        return NetworkFactory.createOkHttpClient(authInterceptor, headersInterceptor)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return NetworkFactory.createRetrofit(okHttpClient)
    }


}