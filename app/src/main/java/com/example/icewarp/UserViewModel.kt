package com.example.icewarp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.icewarp.data.Channel
import com.example.icewarp.data.ChannelListResponse
import com.example.icewarp.data.Permissions
import com.example.icewarp.data.Repository
import com.example.icewarp.ui.ChannelList
import com.example.icewarpassignment.data.UserLoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
@HiltViewModel
class UserViewModel @Inject constructor(val repository: Repository):ViewModel() {

    private val _userToken = MutableLiveData<String>()
    private val _channelList = MutableLiveData<List<Channel>>()
    val channelList: LiveData<List<Channel>> get() = _channelList
    val userToken: LiveData<String> get() = _userToken

    private val compositeDisposable = CompositeDisposable()  // Manage subscriptions

    fun userLogin(username:String,password:String) {
        val disposable = repository.setUserData(username,password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user -> _userToken.value = user.token }, // Success
                { error -> Log.e("UserViewModel", "Error fetching user", error) } // Error Handling
            )

        compositeDisposable.add(disposable)
    }

    fun channelList(token: String, includeUnreadCount: Boolean, excludeMembers: Boolean, includePermissions: Boolean) {
        val disposable = repository.getAllChannelList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { channels ->
                    if (channels.isNotEmpty()) {
                        _channelList.value = channels
                    } else {
                        fetchChannelsFromApi(token, includeUnreadCount, excludeMembers, includePermissions)
                    }
                },
                { error ->
                    Log.e("ChannelViewModel", "Error fetching from DB", error)
                }
            )
        compositeDisposable.add(disposable)
    }
    fun fetchChannelsFromApi(token: String, includeUnreadCount: Boolean, excludeMembers: Boolean, includePermissions: Boolean) {
        val disposable = repository.storeChannelList(token, includeUnreadCount, excludeMembers, includePermissions)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                repository.getAllChannelList() // Fetch updated channel list
            }
            .subscribe(
                { channels -> _channelList.value = channels }, // Success: Update LiveData
                { error -> Log.e("ChannelViewModel", "Error fetching list", error) } // Error Handling
            )

        compositeDisposable.add(disposable)  // Add to CompositeDisposable
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}