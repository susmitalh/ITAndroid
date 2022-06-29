package com.locatocam.app.views.settings.favOrders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.MyApp
import com.locatocam.app.data.requests.ReqOrders
import com.locatocam.app.databinding.ActivityFavouiteOrdersBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.settings.foodOrders.YourOrdersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.locatocam.app.R
@AndroidEntryPoint
class FavouiteOrdersActivity : AppCompatActivity() {
    lateinit var binding:ActivityFavouiteOrdersBinding
    lateinit var viewmodel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding= ActivityFavouiteOrdersBinding.inflate(layoutInflater)
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

            viewmodel.getYourFavsList(reqOrders).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.VISIBLE
                        val favouirteOrdersAdapter =  FavouirteOrdersAdapter(it.data!!.data,application)
                        binding.rec1.adapter = favouirteOrdersAdapter
                        MainActivity.binding.loader.visibility= View.GONE
                    }
                    Status.LOADING -> {
                        MainActivity.binding.loader.visibility= View.VISIBLE
                    }
                    Status.ERROR -> {
                        MainActivity.binding.loader.visibility= View.GONE

                    }

                }

            }

        }
    }
}