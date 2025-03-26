package com.example.icewarpassignment.data

import com.example.icewarp.data.ChannelListResponse
import com.example.icewarp.ui.ChannelList
import io.reactivex.Single
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.lang.reflect.Type

interface ApiInterface {

    @POST("iwauthentication.login.plain")
    @FormUrlEncoded()
    fun getData( @Field("username") username: String,
                 @Field("password") password: String): Single<UserLoginResponse>

    @POST("channels.list")
    @FormUrlEncoded()
    fun getChannelList( @Field("token") token: String,
                 @Field("include_unread_count") include_unread_count: Boolean,@Field("exclude_members") exclude_members: Boolean,@Field("include_permissions") include_permissions:Boolean): Single<ChannelListResponse>

}