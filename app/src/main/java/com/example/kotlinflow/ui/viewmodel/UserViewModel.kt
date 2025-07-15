package com.example.kotlinflow.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinflow.data.local.model.User
import com.example.kotlinflow.data.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UserViewModel(private val repository: IRepository<User>) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _user = MutableSharedFlow<User?>(replay = 0)
    val user: SharedFlow<User?> = _user

    private val _errorMsg = MutableSharedFlow<String>(replay = 0)
    val errorMsg: SharedFlow<String> = _errorMsg

    private val _isUpdate = MutableSharedFlow<Boolean>(replay = 0)
    val isUpdate: SharedFlow<Boolean> = _isUpdate

    private val _isDelete = MutableSharedFlow<Boolean>(replay = 0)
    val isDelete: SharedFlow<Boolean> = _isDelete

    private val _isAdd = MutableSharedFlow<Boolean>(replay = 0)
    val isAdd: SharedFlow<Boolean> = _isAdd

    fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getListData()
                .catch { handleError(it) }
                .collect { _users.value = it }
        }
    }

    fun findUserByEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.findData(email)
                .catch { handleError(it) }
                .collect { _user.emit(it) }
        }
    }

    fun addUser(user: User) {
        performAction(repository.addData(user)) {
            _isAdd.emit(true)
        }
    }

    fun updateUser(user: User) {
        performAction(repository.updateData(user)) {
            _isUpdate.emit(true)
        }
    }

    fun deleteUser(user: User) {
        performAction(repository.deleteData(user)) {
            _isDelete.emit(true)
        }
    }

    private fun handleError(error: Throwable) {
        viewModelScope.launch {
            _errorMsg.emit(error.message ?: "Unknown error!")
        }
    }


    private fun performAction(flow: Flow<Number>, onSuccess: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            flow.catch { handleError(it) }
                .collect { result ->
                    Log.d("UserViewModel", "result $result ${result::class}")
                    if (result.toLong() > 0) onSuccess()
                }
        }
    }
}