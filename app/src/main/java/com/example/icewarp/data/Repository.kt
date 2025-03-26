package com.example.icewarp.data

import com.example.icewarp.ui.ChannelList
import com.example.icewarpassignment.data.ApiInterface
import com.example.icewarpassignment.data.UserLoginResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class Repository @Inject constructor(val api:ApiInterface,val dbHelper: DatabaseHelper) {

    fun setUserData(username:String, password:String):Single<UserLoginResponse>{
     return  api.getData(username,password)
         .map { it }
         .doOnSuccess{dbHelper.insertUserToken(it.token)}
         .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
    }

    fun storeChannelList(token:String, include_unread_count:Boolean, exclude_members:Boolean, include_permissions:Boolean):Single<ChannelListResponse>{
        return  api.getChannelList(token,include_unread_count,exclude_members,include_permissions)
            .subscribeOn(Schedulers.io())
            .map { it }
            .doOnSuccess({
                dbHelper.insertOrUpdateChannel(it.channels)
            })
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun getAllChannelList():Single<List<Channel>>{
        return Single.fromCallable { dbHelper.getAllChannels() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}