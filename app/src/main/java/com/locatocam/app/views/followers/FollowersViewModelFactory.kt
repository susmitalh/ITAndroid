package com.locatocam.app.views.followers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.*
import com.locatocam.app.viewmodels.*

class FollowersViewModelFactory constructor(private val repository: FollowersRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FollowersViewModel::class.java)) {
            FollowersViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}