package com.onebilliongod.android.jetpackandroid.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.onebilliongod.android.jetpackandroid.data.room.entity.Records
import com.onebilliongod.android.jetpackandroid.data.room.entity.User

@Dao
interface RecordsDao {
    @Insert
    fun insertAll(vararg records: Records)

    @Delete
    fun delete(records: Records)

    @Query("SELECT * FROM records")
    fun getAll(): List<Records>

    @Query("SELECT * FROM records where id = :id")
    fun getById(id : Int): Records

    @Query("SELECT * FROM records where user_id = :userId")
    fun getByUserId(userId : Int): List<Records>
}