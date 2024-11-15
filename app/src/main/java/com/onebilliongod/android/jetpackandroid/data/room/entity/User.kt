package com.onebilliongod.android.jetpackandroid.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "create_time") val createTime: Long,
    @ColumnInfo(name = "update_time") val updateTime: Long,
)