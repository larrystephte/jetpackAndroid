package com.onebilliongod.android.jetpackandroid.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.onebilliongod.android.jetpackandroid.data.room.AppDatabase
import com.onebilliongod.android.jetpackandroid.data.room.dao.DataDao
import com.onebilliongod.android.jetpackandroid.data.room.dao.RecordsDao
import com.onebilliongod.android.jetpackandroid.data.room.dao.UserDao
import com.onebilliongod.android.jetpackandroid.utils.DatabaseUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.io.IOException
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        Log.i("RoomModule", "provideAppDatabase.")
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "app-database"
        )
//            .createFromAsset("database/my_database.db")
            .build().also {
                Log.i("RoomModule", "Database created and opened.")
            }
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideDatabaseUtils(@ApplicationContext context: Context): DatabaseUtil {
        return DatabaseUtil(context)
    }

    @Provides
    fun provideRecordsDao(database: AppDatabase): RecordsDao {
        return database.recordsDao()
    }

    @Provides
    fun provideDataDao(database: AppDatabase): DataDao {
        return database.dataDao()
    }
}