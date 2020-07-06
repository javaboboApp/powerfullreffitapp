package com.javabobo.reddit.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.javabobo.reddit.R
import com.javabobo.reddit.utils.Resource
import com.javabobo.reddit.utils.Resource.Status
import com.javabobo.reddit.utils.Resource.Status.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : BaseAuthFragment() {

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_button.setOnClickListener {
            if (validate())
                login()
        }
    }

    private fun validate(): Boolean {
        if (input_email.text.toString().trim().isEmpty()) {
            input_email.error = getString(R.string.input_email_error)
            return false
        }
        if (input_password.text.toString().trim().isEmpty()) {
            input_password.error = getString(R.string.password_can_not_be_empty)
            return false
        }

        return true

    }

    private fun login() {
        authViewModel.login(input_email.text.toString(), input_password.text.toString())
            .observe(viewLifecycleOwner, Observer { result ->
                when (result.status) {
                    SUCCESS -> {
                        communicationListener.navigateToMainActivity()
                    }

                    ERROR -> {
                        //consume the event
                        result.message?.let { message ->
                            showErrorToLogin(message)
                        }
                        communicationListener.hideProgressBar()

                    }

                    LOADING -> {
                        communicationListener.showProgressBar()

                    }
                }

            })
    }

    private fun showErrorToLogin(message: String?) {
        message?.let {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}