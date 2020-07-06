package com.javabobo.reddit.repositories

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.firebase.storage.FirebaseStorage
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.utils.Constants.STORAGE_PATH
import com.javabobo.reddit.utils.Constants.STORE_URL
import com.javabobo.reddit.utils.Event
import com.javabobo.reddit.utils.Resource
import com.javabobo.reddit.utils.isInternetAvailable

interface IStorageRepository {
    fun uploadImage( post: Post): LiveData<Resource<Post>>
}

class StorageRepository(   val storageFirebase: FirebaseStorage ) : IStorageRepository {

    override fun uploadImage(post: Post): LiveData<Resource<Post>> {


        val result = MediatorLiveData<Resource<Post>>()
        result.value = Resource.loadingImg(null)
        val isThereInternet = isInternetAvailable()
        result.addSource(isThereInternet) { shouldFecht ->
            result.removeSource(isThereInternet)
            if (shouldFecht.status == Resource.Status.SUCCESS) {
                    storageFirebase.getReferenceFromUrl(STORE_URL)
                        .child("${STORAGE_PATH}/${post.postId}")
                        .putFile(post.imageUri!!)
                        .addOnSuccessListener {
                            result.value = Resource.success(Event(post))
                        }.addOnFailureListener {
                            result.value = Resource.error(shouldFecht.message, null)

                        }
            } else
                result.value = Resource.error(shouldFecht.message, null)


        }
        return result
    }
}