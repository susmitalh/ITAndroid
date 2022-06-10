package com.locatocam.app.views.PlayPost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.CommentsRepository
import com.locatocam.app.repositories.PlayPostRepository
import com.locatocam.app.viewmodels.CommentsViewModel
import com.locatocam.app.viewmodels.PlayPostViewModal

class PlayPostViewModelFactory constructor(private val repository: PlayPostRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PlayPostViewModal::class.java)) {
            PlayPostViewModal(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}