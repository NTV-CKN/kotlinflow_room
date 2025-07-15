package com.example.kotlinflow.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @ColumnInfo("full_name")
    val fullName: String,
    @PrimaryKey
    val email: String,
    val img: String?
)