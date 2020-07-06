package com.javabobo.reddit.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.javabobo.reddit.R
import com.javabobo.reddit.ui.main.MainActivity
import com.javabobo.reddit.utils.Resource
import com.javabobo.reddit.utils.hideKeyBoardHelper
import kotlinx.android.synthetic.main.activity_auth.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class AuthActivity : AppCompatActivity(), BaseAuthFragment.Listener {

    private val authViewModel: AuthViewModel by viewModel()
    /** Duration of wait **/
    /** Duration of wait  */
    val SPLASH_DISPLAY_LENGTH = 1300L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initAuthFragmentGraph()
    }


    private fun initAuthFragmentGraph() {
        val host = supportFragmentManager.findFragmentById(R.id.auth_fragments_container)
        host?.let {
            // do nothing
        } ?: createNavHost()
    }

    private fun createNavHost() {
        val navHost = NavHostFragment.create(
            R.navigation.auth_nav_graph
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.auth_fragments_container,
                navHost,
                getString(R.string.AuthNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    private fun checkPreviousAuthUser() {

        //user is already log on...
        authViewModel.isLogOn().observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS ->
                    navigateToMainActivity()
                Resource.Status.ERROR ->
                    showAuthFragment()
          }
        })
    }


    override fun onResume() {
        super.onResume()
        checkPreviousAuthUser()

    }

    private fun showAuthFragment() {
        fragment_container.visibility = VISIBLE
        splash_logo.visibility = INVISIBLE
    }

    override fun showProgressBar() {
        progress_bar.visibility = VISIBLE
    }

    override fun hideProgressBar() {
        progress_bar.visibility = INVISIBLE
    }

    override fun navigateToMainActivity() {
        Handler().postDelayed({ /* Create an Intent that will start the Menu-Activity. */
            hideKeyBoardHelper()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            //remove animation
        }, SPLASH_DISPLAY_LENGTH)


    }

}