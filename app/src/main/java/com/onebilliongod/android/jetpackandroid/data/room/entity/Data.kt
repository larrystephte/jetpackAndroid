package com.onebilliongod.android.jetpackandroid.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class Data(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "record_id") val recordId: Int,
    @ColumnInfo(name = "time") val time: Float,
    @ColumnInfo(name = "raw") val raw: String,
    @ColumnInfo(name = "create_time") val createTime: Long,
    @ColumnInfo(name = "update_time") val updateTime: Long,
)
