package com.example.kotlinflow.ui.send_email

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinflow.R
import com.example.kotlinflow.databinding.ActivitySendMailBinding
import com.example.kotlinflow.utils.Utils

@Suppress("DEPRECATION")
class SendMailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySendMailBinding
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySendMailBinding.inflate(layoutInflater)
        Utils.setColorNavAndStatus(window)
        setSupportActionBar(binding.toolbarSend)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getData()
        binding.btnSendSend.setOnClickListener { send() }
        binding.btnCancelSend.setOnClickListener { onBackPressed() }
    }

    private fun getData() {
        email = intent.getStringExtra(Utils.KEY_EMAIL) ?: "Unknown"
        supportActionBar?.title = email
    }

    private fun send() {
        val title = binding.textTitle.text.toString()
        val content = binding.textContent.text.toString()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            Log.d("SendMail", supportActionBar?.title?.toString() ?:"sss")
            data = Uri.parse("mailto:$email?subject=$title&body=$content")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Utils.showSnackbar(binding.root, "Your device not support!")
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}