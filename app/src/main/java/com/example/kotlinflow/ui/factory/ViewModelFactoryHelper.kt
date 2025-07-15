package com.example.kotlinflow.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinflow.data.local.model.User
import com.example.kotlinflow.data.repository.IRepository
import com.example.kotlinflow.ui.viewmodel.UserViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactoryHelper(private val repository: IRepository<User>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Don't support this model class!")
    }
}