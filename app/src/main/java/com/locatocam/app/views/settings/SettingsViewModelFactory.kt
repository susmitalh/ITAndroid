package com.locatocam.app.views.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.CommentsRepository
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.repositories.SettingsRepository
import com.locatocam.app.viewmodels.CommentsViewModel
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.viewmodels.SettingsViewModel

class SettingsViewModelFactory constructor(private val repository: SettingsRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            null as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}