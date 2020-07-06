package com.javabobo.reddit.ui.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bridge.androidtechnicaltest.persistence.IPostsRepository
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.utils.Resource

class UpdatePostViewModel (val repo: IPostsRepository) : ViewModel() {


    fun updatePost(post: Post): LiveData<Resource<Post>> {
        return repo.updatePost(post)
    }

}