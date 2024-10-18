package com.onebilliongod.android.jetpackandroid

import android.app.Application
import android.content.Context
import com.onebilliongod.android.jetpackandroid.data.local.PreferencesDataStoreUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @HiltAndroidApp Use Hilt to manager dependency injection
 */
@HiltAndroidApp
class MainApp : Application() {

    @InstallIn(SingletonComponent::class)
    @Module
    object AppModule {

        @Provides
        @Singleton
        fun providePreferencesDataStoreUtils(@ApplicationContext context: Context): PreferencesDataStoreUtils {
            return PreferencesDataStoreUtils(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        initLogger()
    }

    private fun initLogger() {
        // Log.d("MyApp", "Logger initialized")
    }

}