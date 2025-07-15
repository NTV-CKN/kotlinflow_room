package com.example.kotlinflow.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotlinflow.data.local.dao.UserDao
import com.example.kotlinflow.data.local.model.User

@Database(
    entities = [User::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance === null) {
                synchronized(this) {
                    if (instance === null) {
                        instance = Room.databaseBuilder(
                            context,
                            AppDatabase::class.java,
                            "app_db"
                        ).fallbackToDestructiveMigration(true)
                            .build()
                    }
                }
            }
            return instance!!
        }
    }
}