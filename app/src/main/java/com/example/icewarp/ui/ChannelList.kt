package com.example.icewarp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.icewarp.R
import com.example.icewarp.UserViewModel
import com.example.icewarp.data.DatabaseHelper
import com.example.icewarp.databinding.ActivityChannelListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChannelList : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityChannelListBinding
   var userToken=""
    private val loginViewModel: UserViewModel by viewModels()
    private lateinit var channelAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_list)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_channel_list)
        userToken=intent.getStringExtra("userToken").toString()
        activityMainBinding.icon.setOnClickListener{
         onBackPressed()
        }
        val dbHelper=DatabaseHelper(this)
        activityMainBinding.recyclerView.layoutManager = LinearLayoutManager(this)
         activityMainBinding.deleteIcon.setOnClickListener({
            dbHelper.deleteAllChannels()
             dbHelper.deleteUserToken()
             val intent= Intent(this,MainActivity::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK // Prevents multiple instances
             startActivity(intent)
             finish()
         })
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()  // Closes activity when back is pressed
            }
        })
        apiCall()
    }

    fun apiCall(){
        loginViewModel.channelList.removeObservers(this)

        loginViewModel.channelList(userToken,true,true,true)
        loginViewModel.channelList.observe(this) { user ->
            user?.let {
                val groupedList: List<GroupedChannels> = user.groupBy { it.group_folder_name }
                    .map { GroupedChannels(it.key, it.value) }

                channelAdapter = GroupAdapter(groupedList) // Initialize with empty list
                activityMainBinding.recyclerView.adapter = channelAdapter
                activityMainBinding.loading.visibility=View.GONE
                Log.d("ChannelList", "User Logged In: ${user}")

            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("ChannelList", "Activity Destroyed")
    }

}