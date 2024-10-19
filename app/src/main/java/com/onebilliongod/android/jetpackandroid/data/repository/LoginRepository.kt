package com.onebilliongod.android.jetpackandroid.data.repository

import com.onebilliongod.android.jetpackandroid.data.remote.api.ApiService
import com.onebilliongod.android.jetpackandroid.data.remote.models.request.LoginReq
import com.onebilliongod.android.jetpackandroid.data.remote.models.response.Response
import com.onebilliongod.android.jetpackandroid.data.remote.models.response.UserInfoResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * repository for login
 * inject ApiService
 */
@Singleton
class LoginRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun login(request : LoginReq): Response<UserInfoResponse> {
        return apiService.login(request)
    }
}