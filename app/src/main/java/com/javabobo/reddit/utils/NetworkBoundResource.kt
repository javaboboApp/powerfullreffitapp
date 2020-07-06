package com.javabobo.reddit.utils

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.firebase.database.DataSnapshot

import com.javabobo.reddit.AppExecutors
import com.javabobo.reddit.utils.Resource.Status.*


// CacheObject: Type for the Resource data. (database cache)
// RequestObject: Type for the API response. (network request)
/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <CacheObject>
 * @param <RequestType>
</RequestType></CacheObject> */
abstract class NetworkBoundResource<CacheObject>
@MainThread constructor(private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<CacheObject>>()

    init {

        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            val isThereInternetOrNetWorkIsAvaible =  isInternetAvaibleOrNetworkIsAvaible()
            result.addSource(isThereInternetOrNetWorkIsAvaible){ shouldFetch->
                result.removeSource(isThereInternetOrNetWorkIsAvaible)
                if (shouldFetch.status == SUCCESS) {
                    //I should bring the data from the internet
                    fetchFromNetwork(dbSource)
                } else {
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.success(Event.dataEvent(newData)))
                    }
                }
            }

        }
    }

    @MainThread
    private fun setValue(newValue: Resource<CacheObject>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<CacheObject>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(Event.dataEvent(newData)))
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response.status) {
                SUCCESS -> {
                    appExecutors.diskIO().execute {
                        saveCallResult(processResponse(response))
                        appExecutors.mainThread().execute {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb()) { newData ->
                                setValue(Resource.success(Event.dataEvent(newData)))
                            }
                        }
                    }
                }

                ERROR -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(response.message, Event.dataEvent(newData)))
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<CacheObject>>

    //    @WorkerThread
    protected open fun processResponse(response: Resource<DataSnapshot>) =
        response.data!!.peekContent()

    @WorkerThread
    protected abstract fun saveCallResult(item: DataSnapshot)

    @WorkerThread
    private fun isInternetAvaibleOrNetworkIsAvaible(): LiveData< Resource<Void>> {

        return isInternetAvailable()
    }




    @MainThread
    protected abstract fun loadFromDb(): LiveData<CacheObject>

    //    @MainThread
    protected abstract fun createCall(): LiveData<Resource<DataSnapshot>>
}