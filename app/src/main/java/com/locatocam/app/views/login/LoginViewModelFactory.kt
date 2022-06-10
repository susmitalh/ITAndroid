package com.locatocam.app.views.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.repositories.LoginRepository
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.viewmodels.LoginViewModel

class LoginViewModelFactory constructor(private val repository: LoginRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}