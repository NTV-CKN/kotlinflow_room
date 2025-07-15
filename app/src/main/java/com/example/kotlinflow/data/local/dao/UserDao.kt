package com.example.kotlinflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.kotlinflow.data.local.model.User
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {
    @Query(
        "select * from users"
    )
    fun getAllUser(): Flow<List<User>>

    @Query(
        "select * from users where email like '%' || :email ||  '%'"
    )
    suspend fun findUserByEmail(email: String): User?

    @Insert
    suspend fun addUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User): Int

    @Query("UPDATE users SET full_name = :fullName, img = :uri WHERE email = :email")
    suspend fun updateUser(email: String, uri: String?, fullName: String): Int
}