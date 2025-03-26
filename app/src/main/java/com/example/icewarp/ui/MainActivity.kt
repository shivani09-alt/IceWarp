package com.example.icewarp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.icewarp.R
import com.example.icewarp.UserViewModel
import com.example.icewarp.data.DatabaseHelper
import com.example.icewarp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val loginViewModel: UserViewModel by viewModels()
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.signInButton.setOnClickListener{
                if (activityMainBinding.validateFields() ) {

                    apiCall()

            }
        }
        val dbHelper= DatabaseHelper(this)

        if(dbHelper.getUserToken()?.isNotEmpty() == true)
        {
            val intent=Intent(this,ChannelList::class.java)
            intent.putExtra("userToken",dbHelper.getUserToken())
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK // Prevents multiple instances
            startActivity(intent)
            finish()
        }
    }

    fun apiCall(){
        activityMainBinding.progressBar.visibility = ProgressBar.VISIBLE
        showToast("Please wait, logging in...")

        loginViewModel.userLogin(activityMainBinding.email.text.toString(),activityMainBinding.password.text.toString())
        loginViewModel.userToken.observe(this) { user ->
            user?.let {
                val intent=Intent(this,ChannelList::class.java)
                intent.putExtra("userToken",user)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK // Prevents multiple instances
                startActivity(intent)
                finish()
                Log.d("MainActivity", "User Logged In: ${user}")
                activityMainBinding.progressBar.visibility = ProgressBar.GONE

            }
        }

    }

    fun ActivityMainBinding.validateFields():Boolean{
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,}){1,2}\$".toRegex()

        when {
            email.text?.isBlank() == true || password.text?.isBlank() == true || host.text?.isBlank() == true -> {
                showToast("Please enter all details")
                return false
            }
            !email.text.toString().trim().matches(emailPattern) -> {
                showToast("Enter a valid email")
                return false
            }
            (password.text?.length ?: 0) < 6 -> {
                showToast("Password must be at least 6 characters long")
                return false
            }
            else -> return true
        }
}
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


}
