package com.example.kotlinflow.ui.update

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
import com.example.kotlinflow.databinding.FragmentUpdateBinding
import com.example.kotlinflow.ui.factory.ViewModelFactoryHelper
import com.example.kotlinflow.ui.viewmodel.SaveUserViewModel
import com.example.kotlinflow.ui.viewmodel.UserViewModel
import com.example.kotlinflow.utils.Utils
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class UpdateFragment : Fragment() {
    private lateinit var binding: FragmentUpdateBinding
    private var uri: String? = null
    private var user: User? = null
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("AddFragment", "Result ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                uri = result.data!!.data?.toString()
                Log.d("AddFragment", uri ?: "uri null")
                Glide.with(binding.root)
                    .load(uri)
                    .error(ContextCompat.getDrawable(requireContext(), R.drawable.ic_img_not_sp))
                    .into(binding.includeUpdate.imgAvatar)
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
    private val saveUser: SaveUserViewModel by lazy {
        ViewModelProvider(requireActivity())[SaveUserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.coroutineScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {//save user
                    saveUser.saveUser.collectLatest {
                        user = it
                        uri = it.img
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeUpdate.tilEmail.isEnabled = false
        initContent()
        setEvents()
        handleUserViewModelNotify()
    }


    private fun handleUserViewModelNotify() {
        lifecycle.coroutineScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {//update
                    userViewModel.isUpdate.collectLatest { isUpdate ->
                        if (isUpdate) {
                            Utils.showSnackbar(binding.root, "Update Successfully!")
                        } else {
                            Utils.showSnackbar(
                                binding.root,
                                "Update Failure!"
                            )
                        }
                    }
                }

                launch { //error msg
                    userViewModel.errorMsg.collectLatest {
                        Utils.showSnackbar(
                            binding.root,
                            "Update Failure $it"
                        )
                    }
                }
            }
        }
    }

    private fun setEvents() {
        //Set event update image Avatar
        binding.includeUpdate.imgAvatar
            .setOnClickListener {
                ImagePicker.with(this)
                    .crop(1F, 1F)
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .createIntent { intent ->
                        launcher.launch(intent)
                    }
            }
        //set event click btnOK
        binding.includeUpdate.btnOk.setOnClickListener { handleClickBtnOk() }
        //set event click cancel
        binding.includeUpdate.btnCancel.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun initContent() {
        if (user != null) {
            binding.includeUpdate.inputEmail.setText(user!!.email)
            binding.includeUpdate.inputFullName.setText(user!!.fullName)
            Glide.with(binding.root)
                .load(user!!.img)
                .error(ContextCompat.getDrawable(requireContext(), R.drawable.ic_img_not_sp))
                .into(binding.includeUpdate.imgAvatar)
        } else {
            Utils.showSnackbar(binding.root, "User is null!")
        }
    }

    private fun handleClickBtnOk() {
        if (user != null) {
            val fullName = binding.includeUpdate.inputFullName.text.toString()
            if (fullName.isNotEmpty()) {
                userViewModel.updateUser(User(fullName, user!!.email, uri))
                Utils.hideKeyboard(requireActivity())
            } else {
                Utils.showSnackbar(binding.root, "You must fill all the boxes!")
            }
        } else {
            Utils.showSnackbar(binding.root, "User is null!")
        }

    }
}