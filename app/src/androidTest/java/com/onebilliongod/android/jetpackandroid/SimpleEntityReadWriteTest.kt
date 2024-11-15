package com.onebilliongod.android.jetpackandroid

import android.content.Context
import androidx.compose.ui.text.toUpperCase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.onebilliongod.android.jetpackandroid.data.room.AppDatabase
import com.onebilliongod.android.jetpackandroid.data.room.dao.UserDao
import com.onebilliongod.android.jetpackandroid.data.room.entity.User
import com.onebilliongod.android.jetpackandroid.utils.HashUtils
import org.apache.commons.codec.binary.Hex
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java)
//            .createFromAsset("database/my_database.db")
            .build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val user : User = User(1, "user", "D02534E1C3787CFF88F957A668EA1268",
            "NORMAL", System.currentTimeMillis(), System.currentTimeMillis())
        userDao.insertAll(user)

        val byName = userDao.getById(1)
        assertEquals("user", byName?.name)

        val password = HashUtils.hmacMD5("123456")
        assertEquals(password, byName?.password)
    }
}