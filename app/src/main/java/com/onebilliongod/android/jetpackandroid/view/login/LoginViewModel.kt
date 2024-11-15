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
import com.onebilliongod.android.jetpackandroid.data.room.dao.UserDao
import com.onebilliongod.android.jetpackandroid.data.room.entity.User
import com.onebilliongod.android.jetpackandroid.utils.HashUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  ViewModel responsible for managing the login process.
 *  It provides methods for both mock and actual login, updating the state accordingly.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository,
                                         private val userDao: UserDao,
                                         private val preferencesManager: PreferencesManager) : ViewModel() {
    var loginState by mutableStateOf<Result<Boolean>?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val password = "123456"

    init {
        viewModelScope.launch(Dispatchers.IO) {
            initializeDatabase()
        }
    }

    private suspend fun initializeDatabase() {
        val existingUser = userDao.getById(1)
        Log.i("LoginViewModel", "check-init-user:$existingUser")
        if (existingUser == null) {
            val hashPassword = HashUtils.hmacMD5(password)
            // insert initialize user
            val initialUser = User(
                id = 1,
                name = "user",
                password = hashPassword, // hash
                role = "NORMAL",
                createTime = System.currentTimeMillis(),
                updateTime = System.currentTimeMillis()
            )
            userDao.insertAll(initialUser)
        }
    }

    fun roomLogin(username: String, password: String) {
        Log.i("LoginViewModel", "roomLogin-username:$username,password:$password")
        isLoading = true

        val hashPassword = HashUtils.hmacMD5(password)
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("LoginViewModel", "roomLogin-viewModelScope:$hashPassword")
            val user = userDao.login(username, hashPassword)
            Log.i("LoginViewModel", "roomLogin-login-user:$user")
            if (user != null) {
                isLoading = false
                loginState = Result.success(true)
            } else {
                loginState = Result.failure(Exception("The user haven`t been in the system"))
            }
        }
    }

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