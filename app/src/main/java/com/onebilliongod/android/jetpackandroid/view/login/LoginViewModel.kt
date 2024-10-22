package com.onebilliongod.android.jetpackandroid.view.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onebilliongod.android.jetpackandroid.data.local.PreferenceKeys
import com.onebilliongod.android.jetpackandroid.data.local.PreferencesManager
import com.onebilliongod.android.jetpackandroid.data.remote.models.request.LoginReq
import com.onebilliongod.android.jetpackandroid.data.remote.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  ViewModel responsible for managing the login process.
 *  It provides methods for both mock and actual login, updating the state accordingly.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository,
                                         private val preferencesManager: PreferencesManager) : ViewModel() {
    var loginState by mutableStateOf<Result<Boolean>?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    /**
     * Mock login function for testing purposes.
     * Saves a mock access token in local storage and updates the login state to success.
     */
    fun mockLogin(username: String, password: String) {
        isLoading = true

        viewModelScope.launch {
            preferencesManager.saveData(
                PreferenceKeys.ACCESS_TOKEN,
                "mockAccessToken"
            )

            isLoading = false
            loginState = Result.success(true)
        }
    }

    /**
     *  Performs the actual login process using the provided credentials.
     */
    fun login(username: String, password: String) {
        Log.i("LoginViewModel", "username:$username,password:$password")
        isLoading = true
        viewModelScope.launch {
            try {
                val response = repository.login(LoginReq(username, password))
                Log.i("LoginViewModel", "response:$response")
                if (response.isSuccessful()) {
                    preferencesManager.saveData(
                        PreferenceKeys.ACCESS_TOKEN,
                        response.data.accessToken
                    )

                    isLoading = false
                    loginState = Result.success(true)
                } else {
                    isLoading = false
                    loginState = Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                isLoading = false
                Log.e("LoginViewModel", "login error:$e")
                loginState = Result.failure(e)
            }
        }
    }
}