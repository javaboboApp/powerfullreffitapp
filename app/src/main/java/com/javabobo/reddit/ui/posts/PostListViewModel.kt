package com.javabobo.reddit.ui.posts

import androidx.lifecycle.*
import com.bridge.androidtechnicaltest.persistence.IPostsRepository
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.utils.Resource


class PostListViewModel(private val repo: IPostsRepository) : ViewModel() {
    val TAG = "PostListViewModel"

    fun getPosts() : LiveData<Resource<List<Post>>>{
        return repo.getPosts()
    }

    fun removePost(post: Post) : LiveData<Resource<Post>>{
        return repo.removePost(post)
    }


}


