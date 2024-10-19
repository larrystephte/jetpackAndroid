package com.onebilliongod.android.jetpackandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * @HiltAndroidApp Use Hilt to manager dependency injection
 */
@HiltAndroidApp
class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger()
    }

    private fun initLogger() {
        // Log.d("MyApp", "Logger initialized")
    }

}