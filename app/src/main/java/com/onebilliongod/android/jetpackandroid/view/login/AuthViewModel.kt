package com.onebilliongod.android.jetpackandroid.view.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onebilliongod.android.jetpackandroid.data.local.PreferenceKeys
import com.onebilliongod.android.jetpackandroid.data.local.PreferencesManager
import com.onebilliongod.android.jetpackandroid.data.remote.api.AuthEvent
import com.onebilliongod.android.jetpackandroid.data.remote.api.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * ViewModel responsible for handling authentication-related actions.
 * Manages user login status, checks access token validity, and handles token expiration.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(private val preferencesManager: PreferencesManager) : ViewModel() {
    var isLogin by mutableStateOf(false)
        private set

    var isLoggedIn by mutableStateOf(false)
        private set

    init {
        // Launch a coroutine to observe auth events
        viewModelScope.launch {
            EventBus.authEvent.collect { event ->
                when (event) {
                    is AuthEvent.TokenExpired -> {
                        //handler token expired
                        isLogin = true
                    }
                }
            }
        }
    }

    fun checkAccessToken() {
        val accessToken = runBlocking {
            preferencesManager.getData(PreferenceKeys.ACCESS_TOKEN, "").first()
        }
        isLoggedIn = accessToken.isNotBlank()
    }

    fun removeAccessToken() {
        runBlocking {
            preferencesManager.removeData(PreferenceKeys.ACCESS_TOKEN)
        }
    }
}