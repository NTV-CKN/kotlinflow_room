package com.example.kotlinflow.ui.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlinflow.data.local.database.AppDatabase
import com.example.kotlinflow.data.local.model.User
import com.example.kotlinflow.data.repository.UserRepositoryImpl
import com.example.kotlinflow.databinding.FragmentDeleteDialogBinding
import com.example.kotlinflow.ui.factory.ViewModelFactoryHelper
import com.example.kotlinflow.ui.viewmodel.SaveUserViewModel
import com.example.kotlinflow.ui.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class DeleteDialogFragment : DialogFragment() {
    private var user: User? = null
    private lateinit var binding: FragmentDeleteDialogBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleSaveUserNotify()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeleteDialogBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventClicks()
    }

    private fun handleSaveUserNotify() {
        lifecycle.coroutineScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                saveUser.saveUser.collectLatest {
                    user = it
                }
            }
        }
    }


    private fun setEventClicks() {
        this.isCancelable = false
        binding.btnCancelDelete.setOnClickListener {
            this.dismiss()
        }
        binding.btnOkDelete.setOnClickListener {
            if (user != null) {
                userViewModel.deleteUser(user!!)
                this.dismiss()
            } else {
                Toast.makeText(requireContext(), "User is null!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}