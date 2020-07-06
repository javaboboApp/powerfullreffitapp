package com.javabobo.reddit.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.javabobo.reddit.R
import com.javabobo.reddit.ui.auth.AuthActivity
import com.javabobo.reddit.ui.auth.AuthViewModel
import com.javabobo.reddit.ui.posts.BaseFragment
import com.javabobo.reddit.utils.Resource.Status.*
import com.javabobo.reddit.utils.hideKeyBoardHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), BaseFragment.Listener {
    private lateinit var navController: NavController
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //back arrow...
            android.R.id.home -> {
                navController.popBackStack()
            }
            R.id.exit -> {
                signOut()
            }

        }
        return super.onOptionsItemSelected(item)

    }

    private fun signOut() {
        authViewModel.exit().observe(this, Observer {
            result ->
            when(result.status){
                SUCCESS ->
                {
                    navigateToAuth()
                }
                ERROR -> {
                    //do nothing this time it is not gonna happen
                    hideProgressBar()
                }
                LOADING -> {
                    showProgressBar()
                }
            }
        })
    }
     private fun navigateToAuth() {
        startActivity(Intent(this, AuthActivity::class.java).also {
            it.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
        //remove animation
        overridePendingTransition(0,0)
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
        background_blocker_ui.visibility = View.GONE

    }

    override fun showProgressBar() {
        background_blocker_ui.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
    }

    override fun hideKeyBoard() {
        hideKeyBoardHelper()
    }

    override fun isStoragePermissionGranted(): Boolean{
        if (
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED  ) {


            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_READ_STORAGE
            )

            return false
        } else {
            // Permission has already been granted
            return true
        }
    }
}
const val PERMISSIONS_REQUEST_READ_STORAGE: Int = 301
