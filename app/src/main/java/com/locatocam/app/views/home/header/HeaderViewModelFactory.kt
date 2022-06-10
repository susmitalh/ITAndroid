package com.locatocam.app.views.home.header

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.HeaderRepository
import com.locatocam.app.viewmodels.HeaderViewModel

class HeaderViewModelFactory constructor(private val repository: HeaderRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HeaderViewModel::class.java)) {
            HeaderViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}