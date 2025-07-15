@file:Suppress("DEPRECATION")

package com.example.kotlinflow.ui.add

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.kotlinflow.R
import com.example.kotlinflow.data.local.database.AppDatabase
import com.example.kotlinflow.data.local.model.User
import com.example.kotlinflow.data.repository.UserRepositoryImpl
import com.example.kotlinflow.databinding.FragmentAddBinding
import com.example.kotlinflow.ui.factory.ViewModelFactoryHelper
import com.example.kotlinflow.ui.viewmodel.NavigationViewModel
import com.example.kotlinflow.ui.viewmodel.UserViewModel
import com.example.kotlinflow.utils.Utils
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private var uri: String? = null
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("AddFragment", "Result ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                uri = result.data!!.data?.toString()
                Log.d("AddFragment", uri ?: "uri null")
                Glide.with(binding.root)
                    .load(uri)
                    .error(ContextCompat.getDrawable(requireContext(), R.drawable.ic_img_not_sp))
                    .into(binding.includeAdd.imgAvatar)
            } else {
                Utils.showSnackbar(binding.root, "Unknown!")
            }
        }

    private val userViewModel: UserViewModel by lazy {
        val userDao = AppDatabase.getInstance(requireActivity()).userDao()
        val repository = UserRepositoryImpl(userDao)
        ViewModelProvider(
            requireActivity(),
            ViewModelFactoryHelper(repository)
        )[UserViewModel::class.java]
    }
    @Suppress("unused")
    private val navViewModel: NavigationViewModel by lazy {
        ViewModelProvider(requireActivity())[NavigationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventUserClickOk()
        binding.includeAdd.btnCancel.setOnClickListener { requireActivity().onBackPressed() }
        binding.includeAdd.cardView2.setOnClickListener {
            ImagePicker.with(this)
                .crop(1F, 1F)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    launcher.launch(intent)
                }
        }
        handleUserViewModelNotify()
    }

    override fun onResume() {
        super.onResume()
        Log.d("AddFragment", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("AddFragment", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("AddFragment", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AddFragment", "onDestroy called")
    }

    private fun handleUserViewModelNotify() {
        lifecycle.coroutineScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {//add
                    userViewModel.isAdd.collectLatest { isAdded ->
                        if (isAdded) {
                            Utils.showSnackbar(binding.root, "Add Successfully!")
                        } else {
                            Utils.showSnackbar(
                                binding.root,
                                "Add Failure! Maybe the email already exists!"
                            )
                        }
                    }
                }

                launch { //error msg
                    userViewModel.errorMsg.collectLatest {
                        Utils.showSnackbar(
                            binding.root,
                            "Add Failure! Maybe the email already exists!"
                        )
                    }
                }
            }

        }
    }

    private fun setEventUserClickOk() {
        binding.includeAdd.btnOk.setOnClickListener {
            val email = binding.includeAdd.inputEmail.text.toString()
            val fullName = binding.includeAdd.inputFullName.text.toString()
            if (email.isNotEmpty() && fullName.isNotEmpty()) {
                userViewModel.addUser(User(fullName, email, uri))
                Utils.hideKeyboard(requireActivity())
            } else {
                Utils.showSnackbar(binding.root, "You must fill all the boxes!")
            }
        }
    }
}