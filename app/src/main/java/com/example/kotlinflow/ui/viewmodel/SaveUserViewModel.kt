package com.example.kotlinflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinflow.data.local.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SaveUserViewModel : ViewModel() {
    private val _saveUser = MutableSharedFlow<User>(replay = 1)
    val saveUser: SharedFlow<User> = _saveUser

    fun saveUser(user: User) {
        viewModelScope.launch {
            _saveUser.emit(user)
        }
    }
}