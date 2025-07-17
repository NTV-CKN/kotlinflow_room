package com.example.kotlinflow.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.kotlinflow.R
import com.example.kotlinflow.data.local.database.AppDatabase
import com.example.kotlinflow.data.local.model.User
import com.example.kotlinflow.data.repository.UserRepositoryImpl
import com.example.kotlinflow.databinding.FragmentHomeBinding
import com.example.kotlinflow.ui.OnMenuUserClick
import com.example.kotlinflow.ui.adapter.UserAdapter
import com.example.kotlinflow.ui.delete.DeleteDialogFragment
import com.example.kotlinflow.ui.factory.ViewModelFactoryHelper
import com.example.kotlinflow.ui.send_email.SendMailActivity
import com.example.kotlinflow.ui.viewmodel.SaveUserViewModel
import com.example.kotlinflow.ui.viewmodel.UserViewModel
import com.example.kotlinflow.utils.Utils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnMenuUserClick, MenuProvider {
    private lateinit var userAdapter: UserAdapter
    private lateinit var binding: FragmentHomeBinding
    private val userViewModel: UserViewModel by lazy {
        val userDao = AppDatabase.getInstance(requireActivity()).userDao()
        val repository = UserRepositoryImpl(userDao)
        ViewModelProvider(
            requireActivity(),
            ViewModelFactoryHelper(repository)
        )[UserViewModel::class.java]
    }
    private val saveUser: SaveUserViewModel by lazy {
        ViewModelProvider(requireActivity())[SaveUserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setupUserViewModelNotify()
        setupEventFloatingBtn()
    }

    private fun setupEventFloatingBtn() {
        binding.floating.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
    }

    private fun initAdapter() {
        userAdapter = UserAdapter(this)
        binding.recyclerViewHome.adapter = userAdapter
    }

    private fun setupUserViewModelNotify() {
        lifecycle.coroutineScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {//find user
                    userViewModel.user.collectLatest { user ->
                        if (user != null) userAdapter.updateUsers(listOf(user))
                    }
                }
                launch {//delete
                    userViewModel.isDelete.collect { isDelete ->
                        if (isDelete)
                            Utils.showSnackbar(binding.root, "Delete successfully!")
                    }
                }
                launch {//users
                    userViewModel.users.collect { users ->
                        if (users.isEmpty()) {
                            binding.imgFolderNotSp.visibility = View.VISIBLE
                            binding.recyclerViewHome.visibility = View.GONE
                        } else {
                            binding.imgFolderNotSp.visibility = View.GONE
                            binding.recyclerViewHome.visibility = View.VISIBLE
                        }
                        userAdapter.updateUsers(users)
                    }
                }
            }
        }
    }

    override fun onMenuUserClick(menuItem: MenuItem, user: User): Boolean {
        saveUser.saveUser(user)
        return when (menuItem.itemId) {
            R.id.menu_delete -> {
                DeleteDialogFragment().show(childFragmentManager, null)
                true
            }

            R.id.menu_update -> {
                findNavController().navigate(R.id.action_homeFragment_to_updateFragment)
                true
            }

            R.id.menu_send_email -> {
                val intent = Intent(requireActivity(), SendMailActivity::class.java)
                intent.let {
                    it.putExtra(Utils.KEY_EMAIL, user.email)
                    startActivity(it)
                }
                true
            }
            else -> false
        }
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        try {
            val menuSearch = menu.findItem(R.id.menu_search_bar)
            val searchView = menuSearch.actionView as SearchView
            searchView.queryHint = "Find by email"
            searchView.setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        if (newText.isNotEmpty()) {
                            Log.d("HomeFragment", "onQueryTextChange")
                            userViewModel.findUserByEmail(newText)
                        } else
                            userAdapter.updateUsers(userViewModel.users.value)
                    }
                    return true
                }
            })
        } catch (ex: Exception) {
            Log.e("HomeFragment", ex.message ?: "Unknown error!")
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }
}