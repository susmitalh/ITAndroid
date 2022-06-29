package com.locatocam.app.views.settings.addressBook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.MyApp
import com.locatocam.app.data.requests.ReqAddress
import com.locatocam.app.data.requests.ReqOrders
import com.locatocam.app.data.responses.address.RespAddress
import com.locatocam.app.data.responses.settings.pendingPost.Detail
import com.locatocam.app.databinding.ActivityMyAddressBinding
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.home.HomeFragment
import com.locatocam.app.views.home.HomeViewModelFactory
import com.locatocam.app.views.search.AdddressAdapter
import com.locatocam.app.views.settings.foodOrders.YourOrdersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
@AndroidEntryPoint
class MyAddressActivity : AppCompatActivity() {
    lateinit var binding:ActivityMyAddressBinding
    lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var repository = HomeRepository(application, "")
        var factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        binding.rec1.layoutManager = LinearLayoutManager(MyApp.context)
        binding.rec1.itemAnimator = DefaultItemAnimator()
        //getYourOrdersList()
        setOnClickListeners()
        viewModel.addressresp.observe(this, {
            Log.e("address", it.message!!)
            Log.e("address", it.data.toString())
            val myAddressAdapter = MyAddressAdapter(it.data!!, this)
            binding.rec1.adapter = myAddressAdapter
        })
        viewModel.getAddress(SharedPrefEnc.getPref(application, "mobile"))
    }
    fun setOnClickListeners(){
        binding.back.setOnClickListener { finish() }
        binding.home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}