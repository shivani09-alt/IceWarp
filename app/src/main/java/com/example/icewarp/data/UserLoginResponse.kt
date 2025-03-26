package com.example.icewarpassignment.data

data class UserLoginResponse (val authorized:Boolean,val token:String,val host:String,val email:String,val ok:String)