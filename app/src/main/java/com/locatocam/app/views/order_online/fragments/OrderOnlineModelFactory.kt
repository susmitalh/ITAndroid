package com.locatocam.app.views.order_online.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.repositories.LoginRepository
import com.locatocam.app.repositories.OrderOnlineRepository
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.viewmodels.LoginViewModel
import com.locatocam.app.viewmodels.OrderOnlineViewModel

class OrderOnlineModelFactory constructor(private val repository: OrderOnlineRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(OrderOnlineViewModel::class.java)) {
            OrderOnlineViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}