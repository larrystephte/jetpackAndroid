package com.onebilliongod.android.jetpackandroid.data.remote.api

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

//global handel auth event
object EventBus {
    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent = _authEvent.asSharedFlow()

    suspend fun sendEvent(event: AuthEvent) {
        _authEvent.emit(event)
    }
}

sealed class AuthEvent {
    data object TokenExpired : AuthEvent()
}
