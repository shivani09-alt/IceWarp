package com.example.icewarp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DatabaseHelper @Inject constructor(@ApplicationContext context: Context): SQLiteOpenHelper(context, "app_database", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = """
            CREATE TABLE user_token (token TEXT PRIMARY KEY)""".trimIndent()

        val createTableQuery = """
            CREATE TABLE $TABLE_CHANNELS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT,
                $COLUMN_CREATED INTEGER,
                $COLUMN_CREATOR TEXT,
                $COLUMN_IS_MEMBER INTEGER,
                $COLUMN_GROUP_EMAIL TEXT,
                $COLUMN_GROUP_FOLDER_NAME TEXT,
                $COLUMN_IS_ACTIVE INTEGER,
                $COLUMN_IS_AUTO_FOLLOWED INTEGER,
                $COLUMN_IS_NOTIFICATIONS INTEGER,
                $COLUMN_LAST_SEEN TEXT,
                $COLUMN_LATEST INTEGER,
                $COLUMN_UNREAD_COUNT INTEGER,
                $COLUMN_THREAD_UNREAD_COUNT INTEGER,
                $COLUMN_MEMBERS TEXT,
                $COLUMN_PERMISSIONS TEXT
            );
        """.trimIndent()

        db?.execSQL(createTableQuery)
        db?.execSQL(createUserTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS user_token")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CHANNELS")
        onCreate(db)
    }

    // Insert or Update Channel
    fun insertOrUpdateChannel(channel: List<Channel>) {
        val db = writableDatabase

        for (item in channel) {
            val contentValues = ContentValues().apply {
                put(COLUMN_ID, item.id)
                put(COLUMN_NAME, item.name)
                put(COLUMN_CREATED, item.created)
                put(COLUMN_CREATOR, item.creator)
                put(COLUMN_IS_MEMBER, if (item.is_member) 1 else 0)
                put(COLUMN_GROUP_EMAIL, item.group_email)
                put(COLUMN_GROUP_FOLDER_NAME, item.group_folder_name)
                put(COLUMN_IS_ACTIVE, if (item.is_active) 1 else 0)
                put(COLUMN_IS_AUTO_FOLLOWED, if (item.is_auto_followed) 1 else 0)
                put(COLUMN_IS_NOTIFICATIONS, if (item.is_notifications) 1 else 0)
                put(COLUMN_LAST_SEEN, item.last_seen)
                put(COLUMN_LATEST, item.latest)
                put(COLUMN_UNREAD_COUNT, item.unread_count)
                put(COLUMN_THREAD_UNREAD_COUNT, item.thread_unread_count)
                put(COLUMN_MEMBERS, Gson().toJson(item.members))
                put(COLUMN_PERMISSIONS, Gson().toJson(item.permissions))
            }

            // Check if the channel exists
            val cursor = db.query(
                TABLE_CHANNELS, arrayOf(COLUMN_ID), "$COLUMN_ID = ?",
                arrayOf(item.id), null, null, null
            )

            if (cursor.moveToFirst()) {
                // Update existing channel if found
                db.update(TABLE_CHANNELS, contentValues, "$COLUMN_ID = ?", arrayOf(item.id))
            } else {
                // Insert new channel if not found
                db.insert(TABLE_CHANNELS, null, contentValues)
            }
            cursor.close()
        }

        db.close() // Close the database connection
    }

    // Insert User Token
    fun insertUserToken(token: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("token", token)
        }
        db.insertWithOnConflict("user_token", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    // Get All Channels
    fun getAllChannels(): List<Channel> {
        val channelList = mutableListOf<Channel>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_CHANNELS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val created = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED))
                val creator = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATOR))
                val isMember = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_MEMBER)) == 1
                val groupEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_EMAIL))
                val groupFolderName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_FOLDER_NAME))
                val isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1
                val isAutoFollowed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AUTO_FOLLOWED)) == 1
                val isNotifications = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_NOTIFICATIONS)) == 1
                val lastSeen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_SEEN))
                val latest = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LATEST))
                val unreadCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UNREAD_COUNT))
                val threadUnreadCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_THREAD_UNREAD_COUNT))

                // Convert JSON back to list and object
                val membersJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEMBERS))
                val members: List<String> = try {
                    val type = object : TypeToken<List<String>>() {}.type
                    Gson().fromJson(membersJson, type)
                } catch (e: Exception) {
                    emptyList() // Handle the error by returning an empty list or logging the error
                }

                val permissionsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PERMISSIONS))
                val permissions: Permissions = try {
                    Gson().fromJson(permissionsJson, Permissions::class.java) ?: Permissions(
                        false, false, false, false, false,
                        false, false, false, false, false, false, false
                    )
                } catch (e: Exception) {
                    Permissions(false, false, false, false, false,
                        false, false, false, false, false, false, false)
                }

                channelList.add(
                    Channel(
                        id, name, created, creator, isMember, groupEmail, groupFolderName,
                        isActive, isAutoFollowed, isNotifications, lastSeen, latest, unreadCount,
                        threadUnreadCount, members, permissions
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return channelList
    }

    // Get User Token
    fun getUserToken(): String? {
        readableDatabase.use { db ->
            db.rawQuery("SELECT * FROM user_token", null).use { cursor ->
                return if (cursor.moveToFirst()) cursor.getString(0) else null
            }
        }
    }
    fun deleteUserToken() {
        writableDatabase.use { db ->
            db.execSQL("DELETE FROM user_token") // This will delete all rows in the user_token table
        }
    }
    // Delete All Channels
    fun deleteAllChannels() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_CHANNELS")
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "ChannelsDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_CHANNELS = "channels"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CREATED = "created"
        private const val COLUMN_CREATOR = "creator"
        private const val COLUMN_IS_MEMBER = "is_member"
        private const val COLUMN_GROUP_EMAIL = "group_email"
        private const val COLUMN_GROUP_FOLDER_NAME = "group_folder_name"
        private const val COLUMN_IS_ACTIVE = "is_active"
        private const val COLUMN_IS_AUTO_FOLLOWED = "is_auto_followed"
        private const val COLUMN_IS_NOTIFICATIONS = "is_notifications"
        private const val COLUMN_LAST_SEEN = "last_seen"
        private const val COLUMN_LATEST = "latest"
        private const val COLUMN_UNREAD_COUNT = "unread_count"
        private const val COLUMN_THREAD_UNREAD_COUNT = "thread_unread_count"
        private const val COLUMN_MEMBERS = "members"
        private const val COLUMN_PERMISSIONS = "permissions"
    }
}
