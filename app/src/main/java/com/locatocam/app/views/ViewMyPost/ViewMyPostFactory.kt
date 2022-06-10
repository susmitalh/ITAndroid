package com.locatocam.app.views.ViewMyPost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.PlayPostRepository
import com.locatocam.app.repositories.ViewMyPostRepository
import com.locatocam.app.viewmodels.PlayPostViewModal
import com.locatocam.app.viewmodels.ViewMyPostViewModal

class ViewMyPostFactory constructor(private val repository: ViewMyPostRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ViewMyPostViewModal::class.java)) {
            ViewMyPostViewModal(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}