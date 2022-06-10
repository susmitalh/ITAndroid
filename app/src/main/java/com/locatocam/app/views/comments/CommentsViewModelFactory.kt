package com.locatocam.app.views.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.CommentsRepository
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.viewmodels.CommentsViewModel
import com.locatocam.app.viewmodels.HomeViewModel

class CommentsViewModelFactory constructor(private val repository: CommentsRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
            CommentsViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}