package com.locatocam.app.views.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.repositories.LoginRepository
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.viewmodels.LoginViewModel
import com.locatocam.app.viewmodels.RegisterViewModel

class RegisterViewModelFactory constructor(private val repository: LoginRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            RegisterViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}