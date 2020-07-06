package com.javabobo.reddit

import android.app.Application
import android.content.Context
import com.javabobo.reddit.di.databaseModule
import com.javabobo.reddit.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module


class App() : Application() {

    private val appComponent: MutableList<Module> = mutableListOf(databaseModule, viewModelModule)
    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        // start Koin!
        startKoin {
            // declare used Android context
            androidContext(applicationContext)
            // declare modules
            modules(appComponent)
        }
    }

    companion object {
        private var instance: App? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

}