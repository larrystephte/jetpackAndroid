package com.onebilliongod.android.jetpackandroid.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.onebilliongod.android.jetpackandroid.data.room.entity.Data

@Dao
interface DataDao {
    @Insert
    fun insertAll(vararg data: Data)

    @Delete
    fun delete(data: Data)

    @Query("SELECT * FROM data")
    fun getAll(): List<Data>

    @Query("SELECT * FROM data where id = :id")
    fun getById(id : Int): Data

    @Query("SELECT * FROM data where record_id = :recordId")
    fun getByRecordId(recordId : Int): List<Data>
}