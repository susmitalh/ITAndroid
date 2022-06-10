package com.locatocam.app.views.home.header

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.HeaderRepository
import com.locatocam.app.viewmodels.HeaderViewModel
import com.locatocam.app.viewmodels.TopInfViewModel

class TopInfluencerViewModelFactory constructor(private val repository: HeaderRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TopInfViewModel::class.java)) {
            TopInfViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}