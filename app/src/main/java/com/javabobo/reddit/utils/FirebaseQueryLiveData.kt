package com.javabobo.reddit.utils

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class FirebaseQueryLiveData(private var query: Query) :  LiveData<Resource< DataSnapshot>>() {
    private val LOG_TAG = "FirebaseQueryLiveData"

    private val listener = MyValueEventListener()


    override fun onActive() {
        Log.d(LOG_TAG, "onActive")

        query.addValueEventListener(listener)

    }

    override fun onInactive() {
        Log.d(LOG_TAG, "onInactive")
        query.removeEventListener(listener)

    }

    inner class MyValueEventListener : ValueEventListener {


        override fun onCancelled(databaseError: DatabaseError) {
            value = Resource.error(databaseError.message, null)
            Log.e(LOG_TAG, "Can't listen to query $query", databaseError.toException())
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            value = Resource.success(Event (dataSnapshot))

        }
    }
}