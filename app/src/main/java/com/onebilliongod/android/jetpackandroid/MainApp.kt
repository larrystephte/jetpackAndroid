package com.onebilliongod.android.jetpackandroid

import android.app.Application
import com.onebilliongod.android.jetpackandroid.data.socket.client.SerialClient
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * @HiltAndroidApp Use Hilt to manager dependency injection
 */
@HiltAndroidApp
class MainApp : Application() {

    //immediate init serialModule
    @Inject
    lateinit var serialClient: SerialClient

    override fun onCreate() {
        super.onCreate()
        initLogger()
    }

    private fun initLogger() {
        // Log.d("MyApp", "Logger initialized")
    }

}