package com.example.icewarp.data


data class Channel(
    val id: String,
    val name: String,
    val created: Long,
    val creator: String,
    val is_member: Boolean,
    val group_email: String,
    val group_folder_name: String,
    val is_active: Boolean,
    val is_auto_followed: Boolean,
    val is_notifications: Boolean,
    val last_seen: String,
    val latest: Long,
    val unread_count: Int,
    val thread_unread_count: Int,
    val members: List<String>,
    val permissions: Permissions
)


data class Permissions(
    val items_read: Boolean,
    val items_write: Boolean,
    val items_modify: Boolean,
    val items_delete: Boolean,
    val items_edit_documents: Boolean,
    val folder_read: Boolean,
    val folder_write: Boolean,
    val folder_rename: Boolean,
    val folder_delete: Boolean,
    val administration_invite: Boolean,
    val administration_kick: Boolean,
    val administration_administer: Boolean
)

data class ChannelListResponse(
    val channels: List<Channel>,
    val ok:Boolean
)
