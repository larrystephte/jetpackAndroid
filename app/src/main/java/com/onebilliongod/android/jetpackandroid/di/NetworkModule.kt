package com.onebilliongod.android.jetpackandroid.di

import com.onebilliongod.android.jetpackandroid.data.local.PreferencesManager
import com.onebilliongod.android.jetpackandroid.data.remote.api.ApiService
import com.onebilliongod.android.jetpackandroid.data.remote.api.HeadersInterceptor
import com.onebilliongod.android.jetpackandroid.data.remote.api.NetworkFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * inject HeadersInterceptor
     */
    @Provides
    @Singleton
    fun provideHeaderInterceptor(preferencesManager: PreferencesManager): HeadersInterceptor {
        return HeadersInterceptor(preferencesManager)
    }

    /**
     * inject Retrofit
     */
    @Provides
    @Singleton
    fun provideRetrofit(headersInterceptor: HeadersInterceptor): Retrofit {
        return NetworkFactory.createRetrofit(headersInterceptor)
    }

    /**
     * inject ApiService
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

}