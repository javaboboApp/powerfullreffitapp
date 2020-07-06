package com.bridge.androidtechnicaltest.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.javabobo.reddit.models.Post

@Database(entities = [Post::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract val postsDao: PostDao
}