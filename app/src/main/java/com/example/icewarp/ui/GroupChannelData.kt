package com.example.icewarp.ui

import com.example.icewarp.data.Channel

data class Channel(
    val id: String,
    val name: String,
    val groupFolderName: String
)

data class GroupedChannels(
    val groupFolderName: String,
    val channels: List<Channel>
)
