package com.locatocam.app.views.rolls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.repositories.RollsRepository
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.viewmodels.RollsViewModel

class RollsViewModelFactory constructor(private val repository: RollsRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RollsViewModel::class.java)) {
            RollsViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}