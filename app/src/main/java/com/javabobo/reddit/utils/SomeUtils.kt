package com.javabobo.reddit.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.javabobo.reddit.App
import com.javabobo.reddit.AppExecutors
import java.net.InetAddress
import java.net.UnknownHostException
import java.security.AccessController.getContext


fun Activity.hideKeyBoardHelper() {
    if (currentFocus != null) {
        val inputMethodManager = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager
            .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}

fun isNetworkAvailable(): Boolean {
    val connectivityManager =
        App.applicationContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo
        .isConnected
}

fun isInternetAvailable(): LiveData<Resource<Void>> {

    val result = MutableLiveData<Resource<Void>>()
    if (!isNetworkAvailable()) {
        result.postValue(Resource.error("NetworkNotAvaible", null))
        return result
    }
    AppExecutors.instance?.diskIO()?.execute {
        try {
            val address: InetAddress = InetAddress.getByName("www.google.com")
            if (!address.equals("")) {
                result.postValue(Resource.success(null))
            } else
                result.postValue(Resource.error("Not Internet", null))


        } catch (e: UnknownHostException) {
            // Log error
            result.postValue(Resource.error("UnknownHostException", null))
        }

    }
    return result
}


