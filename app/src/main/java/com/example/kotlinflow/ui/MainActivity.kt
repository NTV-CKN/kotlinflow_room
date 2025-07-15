@file:Suppress("DEPRECATION")

package com.example.kotlinflow.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlinflow.R
import com.example.kotlinflow.data.local.database.AppDatabase
import com.example.kotlinflow.data.local.model.NavigationEnum
import com.example.kotlinflow.data.repository.UserRepositoryImpl
import com.example.kotlinflow.databinding.ActivityMainBinding
import com.example.kotlinflow.ui.add.AddFragment
import com.example.kotlinflow.ui.factory.ViewModelFactoryHelper
import com.example.kotlinflow.ui.home.HomeFragment
import com.example.kotlinflow.ui.send_email.SendMailActivity
import com.example.kotlinflow.ui.update.UpdateFragment
import com.example.kotlinflow.ui.viewmodel.NavigationViewModel
import com.example.kotlinflow.ui.viewmodel.UserViewModel
import com.example.kotlinflow.utils.Utils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {
    private var isPrevNavHome = false
    private var curNav = NavigationEnum.NONE
    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by lazy {
        Log.d("MainActivity", "Lazy User ViewModel")
        val userDao = AppDatabase.getInstance(applicationContext).userDao()
        Log.d("MainActivity", "Lazy User ViewModel 2")
        val repository = UserRepositoryImpl(userDao)
        ViewModelProvider(this, ViewModelFactoryHelper(repository))[UserViewModel::class.java]
    }
    private val navViewModel: NavigationViewModel by lazy {
        ViewModelProvider(this)[NavigationViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        Utils.setColorNavAndStatus(window)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("MainActivity", "Oncreate")
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        handleNavViewModelNotify()
        userViewModel.loadUsers()
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "ONSTOP")
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        Log.d("MainActivity", "On back pressed")
        super.onBackPressed()
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        if (navViewModel.nav.value == NavigationEnum.HOME) {
            isPrevNavHome = false
            super.onBackPressed()
        } else {
            navViewModel.startNav(NavigationEnum.HOME)
            curNav = NavigationEnum.HOME
        }
    }

    private fun handleNavViewModelNotify() {
        lifecycle.coroutineScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                navViewModel.nav.collectLatest {
                    Log.d("MainActivity", "Nav Enum $it")
                    Log.d("MainActivity", "Nav Enum Cur $curNav")
                    if (curNav == it) {
                        if (curNav == NavigationEnum.SEND_EMAIL) {
                            isPrevNavHome = false
                            curNav = NavigationEnum.HOME
                          navViewModel.startNav(NavigationEnum.HOME)
                        }
                        if(it != NavigationEnum.HOME) {
                            return@collectLatest
                        }

                    }
                    when (it) {
                        NavigationEnum.HOME -> {
                            if (isPrevNavHome) return@collectLatest
                            setTitleSupportActionBar(getString(R.string.app_name), it)
                            openFragment(HomeFragment())
                        }

                        NavigationEnum.DELETE -> {
                            //ignore
                        }

                        NavigationEnum.UPDATE -> {
                            setTitleSupportActionBar("UPDATE", it)
                            openFragment(UpdateFragment())
                        }

                        NavigationEnum.ADD -> {
                            setTitleSupportActionBar("ADD", it)
                            openFragment(AddFragment())
                        }

                        NavigationEnum.SEND_EMAIL -> openActivity(SendMailActivity::class)
                        NavigationEnum.NONE -> {}
                    }
                }
            }
        }
    }

    private fun openActivity(kClass: KClass<out Activity>) {
        curNav = NavigationEnum.SEND_EMAIL
        isPrevNavHome = true
        val intent = Intent(this, kClass.java)
        Log.d("MainActivity", navViewModel.user.email)
        intent.putExtra(Utils.KEY_EMAIL, navViewModel.user.email)
        startActivity(
            intent
        )
    }

    private fun setTitleSupportActionBar(title: String, it: NavigationEnum) {
        curNav = it
        if (it != NavigationEnum.HOME) {
            isPrevNavHome = true
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            isPrevNavHome = false
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }


        supportActionBar?.title = title
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .addToBackStack(null)
            .setReorderingAllowed(true)
            .commit()
    }
}