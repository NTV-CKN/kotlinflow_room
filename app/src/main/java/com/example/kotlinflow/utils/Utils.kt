@file:Suppress("DEPRECATION")

package com.example.kotlinflow.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.example.kotlinflow.R
import com.google.android.material.snackbar.Snackbar

object Utils {
    const val KEY_EMAIL = "com.example.kotlinflow.utils.KEY_EMAIL"

    @SuppressLint("ResourceAsColor")
    fun setColorNavAndStatus(window: Window) {
        window.statusBarColor = ContextCompat.getColor(window.context, R.color.status_nav)
        window.navigationBarColor = ContextCompat.getColor(window.context, R.color.status_nav)
    }

    fun showSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(view, message, duration).show()
    }

    @SuppressLint("ServiceCast")
    fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = activity.currentFocus
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}