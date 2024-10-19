package com.onebilliongod.android.jetpackandroid.data.remote.api

import com.onebilliongod.android.jetpackandroid.data.remote.models.request.LoginReq
import com.onebilliongod.android.jetpackandroid.data.remote.models.response.Response
import com.onebilliongod.android.jetpackandroid.data.remote.models.response.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * login api service
 */
interface ApiService {
    @POST("/**/login")
    suspend fun login(@Body loginReq: LoginReq) : Response<UserInfoResponse>
}