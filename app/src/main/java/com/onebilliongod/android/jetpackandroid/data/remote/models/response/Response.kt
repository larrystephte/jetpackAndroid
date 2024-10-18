package com.onebilliongod.android.jetpackandroid.data.remote.models.response

//the top data struct of return parameters
data class Response<T>(
     val code: String,
     val message: String,
     val success: Boolean,
     val data: T,
) {
     fun isSuccessful() : Boolean {
          return code == "200"
     }
}
