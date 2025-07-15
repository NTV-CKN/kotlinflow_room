package com.example.kotlinflow.ui

import android.view.MenuItem
import com.example.kotlinflow.data.local.model.User

interface OnMenuUserClick {
    fun onMenuUserClick(menuItem: MenuItem, user: User): Boolean
}