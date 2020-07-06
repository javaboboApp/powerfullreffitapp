package com.bridge.androidtechnicaltest.persistence

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.javabobo.reddit.models.Post
import com.javabobo.reddit.utils.Resource
import com.javabobo.reddit.utils.isInternetAvailable
import kotlinx.coroutines.delay


interface IAuthRepository {

    fun login(userName: String, password: String): LiveData<Resource<Void>>

    fun register(
        registration_email: String,
        registration_password: String
    ): LiveData<Resource<Void>>

    fun exit(): LiveData<Resource<Void>>
    fun isLogOn(): LiveData<Resource<Void>>
}

class AuthRepository(private val firebaseAuth: FirebaseAuth) : IAuthRepository {

    private val TAG = "AuthRepository"
    override fun login(userName: String, password: String): LiveData<Resource<Void>> {
        val result = MediatorLiveData<Resource<Void>>()

        result.value = Resource.loading(null)
        val isThereInternet = isInternetAvailable()
        result.addSource(isThereInternet) { shouldFecht ->
            result.removeSource(isThereInternet)
            if (shouldFecht.status == Resource.Status.SUCCESS)
                firebaseAuth.signInWithEmailAndPassword(userName, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i(TAG, "login : successful")
                            result.value = Resource.success(null)
                            return@addOnCompleteListener
                        }
                        Log.i(TAG, "login : error")

                        result.value =
                            Resource.error(task.exception?.message ?: "Unknown error", null)
                    }
            else
                result.value = Resource.error(shouldFecht.message, null)

        }

        return result
    }

    override fun register(
        registration_email: String,
        registration_password: String
    ): LiveData<Resource<Void>> {

        val result = MediatorLiveData<Resource<Void>>()
        result.value = Resource.loading(null)

        val isThereInternet = isInternetAvailable()
        result.addSource(isThereInternet) { shouldFecht ->
            result.removeSource(isThereInternet)
            if (shouldFecht.status == Resource.Status.SUCCESS)
                firebaseAuth.createUserWithEmailAndPassword(
                    registration_email,
                    registration_password
                )
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i(TAG, "register : successful")
                            result.value = Resource.success(null)
                            return@addOnCompleteListener
                        }
                        Log.i(TAG, "register : error ${task.exception?.message}")

                    }.addOnFailureListener { exeption ->
                        result.value = Resource.error(exeption.message ?: "Unknown error", null)
                    }
            else
                result.value = Resource.error(shouldFecht.message, null)

        }

        return result
    }

    override fun exit(): LiveData<Resource<Void>> {
        firebaseAuth.signOut()
        return object : LiveData<Resource<Void>>() {
            override fun onActive() {
                super.onActive()
                value = Resource.success(null)
            }
        }
    }

    override fun isLogOn(): LiveData<Resource<Void>> {
        return object : LiveData<Resource<Void>>() {
            override fun onActive() {
                super.onActive()
                if (firebaseAuth.currentUser != null) {
                    value = Resource.success(null)
                } else
                    value = Resource.error("user not connected", null)

            }
        }

    }


}