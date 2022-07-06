package com.locatocam.app.views.settings.foodOrders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.internal.it
import com.locatocam.app.Activity.PlayPostActivity.Companion.viewModel
import com.locatocam.app.MyApp
import com.locatocam.app.data.requests.ReqOrders
import com.locatocam.app.databinding.ActivityYourOrderBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.settings.adapters.CustomerMenuAdapter
import com.locatocam.app.views.settings.adapters.UserMenuAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
@AndroidEntryPoint
class YourOrderActivity : AppCompatActivity() {
    lateinit var binding:ActivityYourOrderBinding
    lateinit var viewmodel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityYourOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewmodel= ViewModelProvider(this).get(SettingsViewModel::class.java)
        binding.rec1.layoutManager = LinearLayoutManager(MyApp.context)
        binding.rec1.itemAnimator = DefaultItemAnimator()
        getYourOrdersList()
        setOnClickListeners()

    }
    fun setOnClickListeners(){
        binding.back.setOnClickListener { finish() }
        binding.home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    fun getYourOrdersList(){
        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqOrders= ReqOrders(user_id)
        lifecycleScope.launch {

            viewmodel.getYourOrdersList(reqOrders).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        binding.loader.visibility= View.VISIBLE
                        val ordersAdapter =  YourOrdersAdapter(it.data!!.data,application)
                        binding.rec1.adapter = ordersAdapter
                        binding.loader.visibility= View.GONE
                    }
                    Status.LOADING -> {
                        binding.loader.visibility= View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.loader.visibility= View.GONE

                    }

                }

            }

        }
    }
}