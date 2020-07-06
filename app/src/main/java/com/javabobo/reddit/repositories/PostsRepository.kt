package com.bridge.androidtechnicaltest.persistence

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.javabobo.reddit.AppExecutors
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.repositories.IStorageRepository
import com.javabobo.reddit.repositories.StorageRepository
import com.javabobo.reddit.utils.*
import com.javabobo.reddit.utils.Constants.ERROR_UPLOADING_IMG
import com.javabobo.reddit.utils.Constants.PATH_POST
import com.javabobo.reddit.utils.Resource.Status.*


interface IPostsRepository {
    fun getPosts(): LiveData<Resource<List<Post>>>
    fun addPost(post: Post): LiveData<Resource<Post>>
    fun removePost(post: Post): LiveData<Resource<Post>>
    fun updatePost(post: Post): LiveData<Resource<Post>>
}

class PostsRepository(
    val firebaseDatabase: FirebaseDatabase,
    val firebaseAuth: FirebaseAuth,
    val storageRepository: IStorageRepository,
    val database: AppDatabase
) : IPostsRepository {

    private val TAG = "PostsRepository"

    override fun getPosts(): LiveData<Resource<List<Post>>> {
        return object : NetworkBoundResource<List<Post>>(AppExecutors.instance!!) {
            override fun saveCallResult(dataSnapshot: DataSnapshot) {
                Log.i(TAG, "saveCallResult")
                dataSnapshot.children.forEach { data ->
                    data.getValue(Post::class.java)?.let { post ->
                        post.postId = data.key!!
                        database.postsDao.insertPost(post)
                    }
                }
            }


            override fun loadFromDb(): LiveData<List<Post>> {
                Log.i(TAG, "loadFromDb")
                return database.postsDao.getPots()
            }

            override fun createCall(): LiveData<Resource<DataSnapshot>> {
                Log.i(TAG, "createCall")
                return FirebaseQueryLiveData(
                    firebaseDatabase.reference.child(PATH_POST)
                )
            }

        }.asLiveData()
    }

    override fun addPost(post: Post): LiveData<Resource<Post>> {
        val result = MediatorLiveData<Resource<Post>>()
        result.value = Resource.loading(null)
        post.userId = firebaseAuth.currentUser?.uid
        val isThereInternet = isInternetAvailable()
        result.addSource(isThereInternet) { shouldFecht ->
            result.removeSource(isThereInternet)
            if (shouldFecht.status == SUCCESS) {
                val mFirebase = firebaseDatabase.reference.child(PATH_POST).push()
                mFirebase
                    .setValue(post)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            mFirebase.key?.let { key ->
                                post.postId = key
                                if (post.imageUri != null)
                                    uploadImageToFirabaseStore(post, result)
                                else {
                                    result.value = Resource.success(Event(post))
                                    Log.i(TAG, "AddPost with not image success")
                                }


                            }

                            return@addOnCompleteListener
                        }
                        Log.i(TAG, "addPost error")
                        result.value = Resource.error(task.exception?.message, null)
                    }.addOnFailureListener { exeption ->
                        Log.i(TAG, "addPost error")
                        result.value = Resource.error(exeption?.message, null)
                    }
            } else
                result.value = Resource.error(shouldFecht.message, null)
        }
        return result
    }

    private fun uploadImageToFirabaseStore(
        post: Post,
        result: MediatorLiveData<Resource<Post>>
    ) {
        val resultStorage = storageRepository.uploadImage(post = post)
        result.addSource(resultStorage) {
            when (it.status) {
                LOADING_IMG -> {
                    result.value = Resource.loadingImg(null)
                }
                SUCCESS -> {
                    Log.i(TAG, "Image success")
                    result.removeSource(resultStorage)
                    result.value = Resource.success(Event(post))
                }
                ERROR -> {
                    result.removeSource(resultStorage)
                    result.value = Resource.error(ERROR_UPLOADING_IMG, null)

                }

            }


        }
    }

    override fun removePost(post: Post): LiveData<Resource<Post>> {
        val result = MediatorLiveData<Resource<Post>>()
        result.value = Resource.loading(null)
        val isThereInternet = isInternetAvailable()
        result.addSource(isThereInternet) { shouldFecht ->
            result.removeSource(isThereInternet)
            if (shouldFecht.status == SUCCESS)
                firebaseDatabase.reference.child(PATH_POST)
                    .child(post.postId)
                    .removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i(TAG, "removePost success")
                            removePostFromDb(post)
                            result.value = Resource.success(Event(post))
                            return@addOnCompleteListener
                        }

                    }.addOnFailureListener { exception ->
                        Log.i(TAG, "removePost error")
                        result.value = Resource.error(exception?.message, null)
                    }
            else {
                result.value = Resource.error(shouldFecht.message, null)
            }
        }

        return result
    }

    override fun updatePost(post: Post): LiveData<Resource<Post>> {
        val result = MediatorLiveData<Resource<Post>>()
        result.value = Resource.loading(null)
        val isThereInternet = isInternetAvailable()
        result.addSource(isThereInternet) { shouldFecht ->
            result.removeSource(isThereInternet)
            if (shouldFecht.status == SUCCESS)
                firebaseDatabase.reference.child(PATH_POST)
                    .child(post.postId)
                    .setValue(post)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i(TAG, "updatePost success")
                            removePostFromDb(post)
                            result.value = Resource.success(Event(post))
                            return@addOnCompleteListener
                        }

                    }.addOnFailureListener { exception ->
                        Log.i(TAG, "updatePost error")
                        result.value = Resource.error(exception?.message, null)
                    }
            else {
                result.value = Resource.error(shouldFecht.message, null)
            }
        }

        return result
    }

    private fun removePostFromDb(post: Post) {
        AppExecutors.instance?.diskIO()?.execute {
            Log.i(TAG, "removePostFromDB")
            database.postsDao.removePost(post)
        }


    }


}