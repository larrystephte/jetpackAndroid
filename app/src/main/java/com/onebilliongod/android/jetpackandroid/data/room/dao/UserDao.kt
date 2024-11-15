package com.onebilliongod.android.jetpackandroid.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.onebilliongod.android.jetpackandroid.data.room.entity.User

@Dao
interface UserDao {
    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user where id = :id")
    fun getById(id : Int): User?

    @Query("SELECT * FROM user where name = :name and password = :password")
    fun login(name: String, password: String): User?
}