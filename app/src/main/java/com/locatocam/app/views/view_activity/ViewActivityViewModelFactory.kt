package com.locatocam.app.views.view_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.*
import com.locatocam.app.viewmodels.*

class ViewActivityViewModelFactory constructor(private val repository: ViewActivityRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ViewActivityViewModel::class.java)) {
            ViewActivityViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}