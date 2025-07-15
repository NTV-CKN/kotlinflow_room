package com.example.kotlinflow.data.repository

import com.example.kotlinflow.data.local.dao.UserDao
import com.example.kotlinflow.data.local.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(private val userDao: UserDao) : IRepository<User> {
    override fun getListData(): Flow<List<User>> {
        return userDao.getAllUser()
    }

    override fun findData(key: String): Flow<User?> {
        return flow { emit(userDao.findUserByEmail(key)) }
    }

    override fun updateData(data: User): Flow<Int> {
        return flow { emit(userDao.updateUser(data.email, data.img, data.fullName)) }
    }

    override fun deleteData(data: User): Flow<Int> {
        return flow { emit(userDao.deleteUser(data)) }
    }

    override fun addData(data: User): Flow<Long> {
        return flow { emit(userDao.addUser(data)) }
    }
}