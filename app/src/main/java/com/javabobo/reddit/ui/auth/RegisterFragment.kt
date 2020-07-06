package com.javabobo.reddit.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.javabobo.reddit.R
import com.javabobo.reddit.utils.Resource.Status.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.javabobo.reddit.utils.Resource.Status.SUCCESS as SUCCESS

class RegisterFragment : BaseAuthFragment() {
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        register_button.setOnClickListener {
            if (validate())
                register()
        }
    }

    private fun validate(): Boolean {
        if (input_email.text.toString().trim().isEmpty()) {
            showEmailError()
            return false
        }

        if (input_password.text.toString().trim().isEmpty()) {
            showPasswordError()
            return false
        }

        if (input_password.text.toString() != input_password_confirm.text.toString()) {
            showErrorConfirm()
            return false
        }

        return true
    }

    private fun showErrorConfirm() {
        input_password.error = getString(R.string.input_confirm_error_password)
    }

    private fun showPasswordError() {
        input_password.error = getString(R.string.password_can_not_be_empty)
    }

    private fun showEmailError() {
        input_email.error = getString(R.string.input_email_error)
    }

    private fun register() {
        communicationListener.showProgressBar()
        authViewModel.register(
            input_email.text.toString(),
            input_password.text.toString()
        ).observe(viewLifecycleOwner, Observer { result ->
            when (result.status) {
                SUCCESS -> {

                    communicationListener.navigateToMainActivity()
                }
                ERROR -> {
                    //if the event has not be handled...
                    result.message?.let { message ->
                        showErrorToRegister(message)
                    }

                    communicationListener.hideProgressBar()
                }

                LOADING -> {
                    communicationListener.showProgressBar()
                }
            }
        })
    }

    private fun showErrorToRegister(contentIfNotHandled: String?) {
        contentIfNotHandled?.let {
            Toast.makeText(context, contentIfNotHandled, Toast.LENGTH_LONG).show()
        }
    }



}