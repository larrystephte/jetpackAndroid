package com.onebilliongod.android.jetpackandroid.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.onebilliongod.android.jetpackandroid.data.room.dao.DataDao
import com.onebilliongod.android.jetpackandroid.data.room.dao.RecordsDao
import com.onebilliongod.android.jetpackandroid.data.room.dao.UserDao
import com.onebilliongod.android.jetpackandroid.data.room.entity.Data
import com.onebilliongod.android.jetpackandroid.data.room.entity.Records
import com.onebilliongod.android.jetpackandroid.data.room.entity.User

@Database(entities = [User::class, Records::class, Data::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recordsDao(): RecordsDao
    abstract fun dataDao(): DataDao
}