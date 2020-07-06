package com.javabobo.reddit.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bridge.androidtechnicaltest.persistence.IAuthRepository
import com.javabobo.reddit.utils.Resource

class AuthViewModel(private val repo: IAuthRepository) : ViewModel() {


    fun login(userName: String, password: String): LiveData<Resource<Void>> {
        return repo.login(userName, password)
    }

    fun register(
        registration_email: String,
        registration_password: String
    ): LiveData<Resource<Void>> {
        return repo.register(registration_email, registration_password)
    }

    fun exit(): LiveData<Resource<Void>> {
        return repo.exit()
    }

    fun isLogOn(): LiveData<Resource<Void>> {
        return repo.isLogOn()
    }

}