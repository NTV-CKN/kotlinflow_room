package com.example.kotlinflow.data.repository

import kotlinx.coroutines.flow.Flow

interface IRepository<T> {
    fun getListData(): Flow<List<T>>
    fun findData(key: String): Flow<T?>
    fun addData(data: T): Flow<Long>
    fun deleteData(data: T): Flow<Int>
    fun updateData(data: T): Flow<Int>
}