package com.javabobo.reddit.di

import com.bridge.androidtechnicaltest.persistence.AuthRepository
import com.bridge.androidtechnicaltest.persistence.IAuthRepository
import com.bridge.androidtechnicaltest.persistence.IPostsRepository
import com.bridge.androidtechnicaltest.persistence.PostsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.javabobo.reddit.App
import com.javabobo.reddit.persistence.DatabaseFactory
import com.javabobo.reddit.repositories.IStorageRepository
import com.javabobo.reddit.repositories.StorageRepository
import com.javabobo.reddit.ui.auth.AuthViewModel
import com.javabobo.reddit.ui.posts.AddPostViewModel
import com.javabobo.reddit.ui.posts.PostListViewModel
import com.javabobo.reddit.ui.posts.UpdatePostViewModel
import org.koin.android.viewmodel.dsl.viewModel

import org.koin.dsl.module


val databaseModule = module {
    //factory return a new instance every single time
    factory { DatabaseFactory.getDBInstance(get()) }
    //Return one instance
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { FirebaseStorage.getInstance() }

    single<IStorageRepository> { StorageRepository(get()) }
    single<IPostsRepository> { PostsRepository(get(), get(), get(),get()) }
    single<IAuthRepository> { AuthRepository(get()) }

}

val viewModelModule = module {
    //posts stuff...
    viewModel { PostListViewModel(get()) }
    viewModel { AddPostViewModel(get()) }
    viewModel { UpdatePostViewModel(get()) }
    //auth stuff...
    viewModel { AuthViewModel(get()) }
}
