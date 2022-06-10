package com.locatocam.app.views.order_online

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.net.PlacesClient
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityOrderOnlineBaseBinding
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.ActivityMainViewModel
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.views.home.HomeViewModelFactory

open class OrderOnlineBaseActivity : AppCompatActivity() {
    lateinit var baseBinding : ActivityOrderOnlineBaseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_order_online_base,null,false)
//        mainViewModel = ViewModelProvider(this).get(ActivityMainViewModel::class.java)
        baseBinding.myLocation.text = SharedPrefEnc.getPref(this,"selected_address")

        setContentView(baseBinding.root)
    }
}