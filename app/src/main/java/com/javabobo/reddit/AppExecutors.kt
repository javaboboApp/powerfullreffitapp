package com.javabobo.reddit

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class AppExecutors {
    private val mDiskIO: Executor = Executors.newSingleThreadExecutor()
    private val mMainThreadExecutor: Executor = MainThreadExecutor()
    fun diskIO(): Executor {
        return mDiskIO
    }

    fun mainThread(): Executor {
        return mMainThreadExecutor
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())
        override fun execute(@NonNull command: Runnable?) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        var instance: AppExecutors? = null
            get() {
                if (field == null) {
                    field = AppExecutors()
                }
                return field
            }
            private set
    }
}