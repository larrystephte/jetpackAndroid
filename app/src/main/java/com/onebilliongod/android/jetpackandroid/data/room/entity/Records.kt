package com.onebilliongod.android.jetpackandroid.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Records(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "end_time") val endTime: Long?,
    @ColumnInfo(name = "result") val result: String?,
    @ColumnInfo(name = "create_time") val createTime: Long,
    @ColumnInfo(name = "update_time") val updateTime: Long,
)
