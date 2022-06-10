package com.locatocam.app.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityMainViewModel:ViewModel() {

    var lat:Double=0.0
    var lng:Double=0.0
    lateinit var add:String
    var address_text=MutableLiveData<String>()
    init {

    }
}