package com.locatocam.app.views.order_online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.AddProductRepository
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.repositories.LoginRepository
import com.locatocam.app.repositories.OrderOnlineRepository
import com.locatocam.app.viewmodels.AddProductViewModel
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.viewmodels.LoginViewModel
import com.locatocam.app.viewmodels.OrderOnlineViewModel

class AddProductViewModelFactory constructor(private val repository: AddProductRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddProductViewModel::class.java)) {
            AddProductViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}