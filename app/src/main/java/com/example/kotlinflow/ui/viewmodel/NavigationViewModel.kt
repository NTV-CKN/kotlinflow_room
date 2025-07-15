package com.example.kotlinflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinflow.data.local.model.NavigationEnum
import com.example.kotlinflow.data.local.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NavigationViewModel : ViewModel() {
    private val _nav = MutableStateFlow(NavigationEnum.HOME)
    val nav: StateFlow<NavigationEnum> = _nav
    var user: User = User("", "", "")

    fun startNav(navigationEnum: NavigationEnum) {
        viewModelScope.launch {
            _nav.emit(navigationEnum)
        }
    }
}