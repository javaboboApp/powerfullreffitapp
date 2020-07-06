package com.bridge.androidtechnicaltest.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.javabobo.reddit.models.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM Post ORDER BY  timestamp DESC ")
    fun getPots(): LiveData<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: Post): Long

    @Delete
    fun removePost(post: Post) : Int

    @Update
    fun updatePost(post: Post) : Int



}