package com.locatocam.app.views.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.repositories.LoginRepository
import com.locatocam.app.repositories.MapRepository
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.viewmodels.LoginViewModel
import com.locatocam.app.viewmodels.MapsViewModel

class MapsModelFactory constructor(private val repository: MapRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            MapsViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}