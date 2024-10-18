package com.onebilliongod.android.jetpackandroid.data.remote.models.response

//return user info jwt access token  after login
data class UserInfoResponse (
     val userId: Long,
     val userName: String,
     val accessToken: String,
)