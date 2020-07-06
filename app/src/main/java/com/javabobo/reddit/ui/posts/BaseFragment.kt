package com.javabobo.reddit.ui.posts

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import java.lang.ClassCastException

open class BaseFragment : Fragment() {
    protected lateinit var comunicatorInterface: Listener
     val firebaseAuth: FirebaseAuth by inject()



    override fun onStop() {
        comunicatorInterface.hideKeyBoard()
        super.onStop()
    }

    interface Listener {
        fun hideProgressBar()
        fun showProgressBar()
        fun hideKeyBoard()
        fun isStoragePermissionGranted() : Boolean
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            context as Listener
            comunicatorInterface = context
        } catch (exeption: ClassCastException) {
            println("${context.javaClass} must implement the interface ")

        }
    }
}