package com.javabobo.reddit.ui.posts

import android.net.Uri
import androidx.lifecycle.*
import com.bridge.androidtechnicaltest.persistence.IPostsRepository
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.repositories.StorageRepository
import com.javabobo.reddit.utils.Resource


class AddPostViewModel(val repo_post: IPostsRepository) : ViewModel() {
    val TAG = "AddPostViewModel"


    fun addPost(post: Post) : LiveData<Resource<Post>>{
        return repo_post.addPost(post)
    }




}