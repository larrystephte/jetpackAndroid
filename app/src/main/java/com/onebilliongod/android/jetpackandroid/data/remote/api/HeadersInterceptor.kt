package com.onebilliongod.android.jetpackandroid.data.remote.api

import com.onebilliongod.android.jetpackandroid.data.local.PreferenceKeys
import com.onebilliongod.android.jetpackandroid.data.local.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * add accessToken to avery http  header
 */
@Singleton
class HeadersInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        val accessToken = runBlocking {
            preferencesManager.getData(PreferenceKeys.ACCESS_TOKEN, "").first()
        }

        if (accessToken.isNotEmpty()) {
            builder.addHeader("Authorization", "Bearer $accessToken")
        }

        val response = chain.proceed(builder.build())

        return response
    }
}