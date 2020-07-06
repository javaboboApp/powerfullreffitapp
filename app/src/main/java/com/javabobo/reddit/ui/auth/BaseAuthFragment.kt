package com.javabobo.reddit.ui.auth

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment

open class BaseAuthFragment : Fragment() {

     lateinit var communicationListener: Listener
    private val TAG = "BaseAuthFragment"

    interface Listener {
        fun showProgressBar()
        fun hideProgressBar()
        fun navigateToMainActivity()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            communicationListener = context as Listener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement communicationListener")
        }
    }
}