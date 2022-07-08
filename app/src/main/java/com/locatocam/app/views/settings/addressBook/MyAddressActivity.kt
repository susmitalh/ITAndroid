package com.locatocam.app.views.settings.addressBook

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.MyApp
import com.locatocam.app.data.responses.address.Data
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityMyAddressBinding
import com.locatocam.app.repositories.HomeRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.HomeViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.home.HomeViewModelFactory
import com.locatocam.app.views.location.MapsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyAddressActivity : AppCompatActivity(), ClickEditAddress {
    lateinit var binding: ActivityMyAddressBinding
    lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAddressBinding.inflate(layoutInflater)
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
            val myAddressAdapter = MyAddressAdapter(it.data!!, this, this)
            binding.rec1.adapter = myAddressAdapter
        })
        viewModel.getAddress(SharedPrefEnc.getPref(application, "mobile"))
    }

    fun setOnClickListeners() {
        binding.back.setOnClickListener { finish() }
        binding.home.setOnClickListener {
            var intent= Intent(applicationContext,MainActivity::class.java)
            intent.putExtra("lat",MainActivity.instances.viewModel.lat)
            intent.putExtra("lng",MainActivity.instances.viewModel.lng)
            intent.putExtra("address",MainActivity.instances.viewModel.add)
            startActivity(intent)
        }
    }

    fun loaddata(){
        viewModel.addressresp.observe(this, {
            Log.e("address", it.message!!)
            Log.e("address", it.data.toString())
            val myAddressAdapter = MyAddressAdapter(it.data!!, this,this@MyAddressActivity)
            binding.rec1.adapter = myAddressAdapter
            viewModel.getAddress(SharedPrefEnc.getPref(application, "mobile"))
            viewModel.getAddress(SharedPrefEnc.getPref(application, "mobile"))
        })
    }

    override fun edtAddressSetting(data: Data) {
        var intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("lat", data.latitude)
        intent.putExtra("lng", data.longitude)
        intent.putExtra("address", data.customer_address.toString())
        intent.putExtra("flateNo", data.door_no.toString())
        intent.putExtra("landmark", data.cust_landmark)
        intent.putExtra("addressId", data.c_address_id)
        intent.putExtra("address_save_as", data.address_save_as)
        startForResult.launch(intent)
    }

    val startForResult =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.getAddress(SharedPrefEnc.getPref(application, "mobile"))
               (binding.rec1.adapter as MyAddressAdapter).notifyDataSetChanged()
            }
        }



    override fun Remove(data: Data) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_confirmation)
        dialog.setCanceledOnTouchOutside(false)
        val tittle = dialog.findViewById<View>(R.id.tittle) as TextView
        val yes = dialog.findViewById<View>(R.id.yes) as Button
        val no = dialog.findViewById<View>(R.id.no) as Button
        tittle.text="Are you sure want to remove?"
        no.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            viewModel.resDelete.observe(this, {
                Log.e("address", it.message!!)
                val addressId:String?= data.c_address_id
                val address:Int?=addressId?.toInt()
                viewModel.deleteAddress(address)
                if(it.message.equals("success")){
                    loaddata()
                }
            })


            dialog.dismiss()
        }
        dialog.show()
    }
}