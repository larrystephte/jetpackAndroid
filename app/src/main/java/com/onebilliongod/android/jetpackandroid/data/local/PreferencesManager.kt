package com.onebilliongod.android.jetpackandroid.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    suspend fun <T> saveData(key: Preferences.Key<T>, value: T) {
        try {
            context.dataStore.edit { settings ->
                settings[key] = value
            }
        } catch (e: Exception) {
            Log.e("PreferencesDataStoreUtils", "Failed to save data for key: ${key.name}", e)
        }
    }

    fun <T> getData(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    suspend fun <T> removeData(key: Preferences.Key<T>) {
        try {
            context.dataStore.edit { settings ->
                settings.remove(key)
            }
        } catch (e: Exception) {
            Log.e("PreferencesDataStoreUtils", "Failed to remove data for key: ${key.name}", e)
        }
    }
}

object PreferenceKeys {
    val ACCESS_TOKEN = stringPreferencesKey("accessToken")
}