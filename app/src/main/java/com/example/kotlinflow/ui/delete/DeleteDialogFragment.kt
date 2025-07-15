package com.example.kotlinflow.ui.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinflow.data.local.database.AppDatabase
import com.example.kotlinflow.data.repository.UserRepositoryImpl
import com.example.kotlinflow.databinding.FragmentDeleteDialogBinding
import com.example.kotlinflow.ui.factory.ViewModelFactoryHelper
import com.example.kotlinflow.ui.viewmodel.NavigationViewModel
import com.example.kotlinflow.ui.viewmodel.UserViewModel


class DeleteDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDeleteDialogBinding
    private val userViewModel: UserViewModel by lazy {
        val userDao = AppDatabase.getInstance(requireActivity()).userDao()
        val repository = UserRepositoryImpl(userDao)
        ViewModelProvider(
            requireActivity(),
            ViewModelFactoryHelper(repository)
        )[UserViewModel::class.java]
    }
    private val navViewModel: NavigationViewModel by lazy {
        ViewModelProvider(requireActivity())[NavigationViewModel::class.java]
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
        //handleUserViewModelNotify()
    }

//    private fun handleUserViewModelNotify() {
//        lifecycle.coroutineScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                launch {//add
//                    userViewModel.isDelete.collectLatest { isDelete ->
//                        if (isDelete) {
//                            Utils.showSnackbar(binding.root, "Add Successfully!")
//                        } else {
//                            Utils.showSnackbar(
//                                binding.root,
//                                "Add Failure! Maybe the email already exists!"
//                            )
//                        }
//                    }
//                }
//
//                launch { //error msg
//                    userViewModel.errorMsg.collectLatest {
//                        Utils.showSnackbar(
//                            binding.root,
//                            "Add Failure! Maybe the email already exists!"
//                        )
//                    }
//                }
//            }
//
//        }
//    }


    private fun setEventClicks() {
        this.isCancelable = false
        binding.btnCancelDelete.setOnClickListener {
            this.dismiss()
        }
        binding.btnOkDelete.setOnClickListener {
            userViewModel.deleteUser(navViewModel.user)
            this.dismiss()
        }
    }
}