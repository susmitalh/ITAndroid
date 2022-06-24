package com.locatocam.app.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.CommentsRepository
import com.locatocam.app.repositories.OnlineOrderHelpRepository
import com.locatocam.app.viewmodels.CommentsViewModel
import com.locatocam.app.viewmodels.OnlineOrderHelpViewModel

class OnlineOrderHelpFactory constructor(private val repository: OnlineOrderHelpRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(OnlineOrderHelpViewModel::class.java)) {
            OnlineOrderHelpViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}