package com.onebilliongod.android.jetpackandroid.data.remote.api

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

//check http status code
class AuthInterceptor()  : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        Log.i("AuthInterceptor","response.code:${response.code()},${response.message()}, ${response.protocol()}")

        //check http status code,if 401 then jump to login page
        if (response.code() == 401) {
            //using global event bus jump to login
            CoroutineScope(Dispatchers.IO).launch  {
                EventBus.sendEvent(AuthEvent.TokenExpired)
            }
        }

        return response
    }
}