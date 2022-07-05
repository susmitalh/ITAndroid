package com.locatocam.app.views.settings.influencerDashboard

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.locatocam.app.MyApp
import com.locatocam.app.R
import com.locatocam.app.data.requests.ReqChInfluencer
import com.locatocam.app.data.requests.ReqOrders
import com.locatocam.app.data.requests.viewApproval.ReqApprove
import com.locatocam.app.databinding.ActivityChangeInfluencerBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.settings.adapters.ChangeInfluencerAdapter
import com.locatocam.app.views.settings.settingInterface.ChangeInfluencer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangeInfluencerActivity : AppCompatActivity(), ChangeInfluencer {
    lateinit var binding:ActivityChangeInfluencerBinding
    lateinit var viewModel:SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChangeInfluencerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel= ViewModelProvider(this).get(SettingsViewModel::class.java)
        binding.rec1.layoutManager = GridLayoutManager(this,2)
        binding.rec1.itemAnimator = DefaultItemAnimator()
        setOnClickListeners()
        getYourOrdersList()
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
            viewModel.getChangeInfluencer(reqOrders).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.VISIBLE
                        val changeInfluencerAdapter =  ChangeInfluencerAdapter(it.data!!.data,application,this@ChangeInfluencerActivity)
                        binding.rec1.adapter = changeInfluencerAdapter
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

    override fun changeInfluencer(infCode: String) {
        reqChIn(infCode)
    }
    fun reqChIn(infCode: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_confirmation)
        dialog.setCanceledOnTouchOutside(false)
        val tittle=dialog.findViewById<View>(R.id.tittle) as TextView
        tittle.text="Choose Influencer ?"
        val yes = dialog.findViewById<View>(R.id.yes) as Button
        val no = dialog.findViewById<View>(R.id.no) as Button
        no.setOnClickListener { dialog.dismiss() }
        yes.setOnClickListener {
            approve(infCode)
            dialog.dismiss()
        }
        dialog.show()
    }
    fun approve(id:String){
        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val req= ReqChInfluencer( id,
            user_id.toString())
        lifecycleScope.launch {
            viewModel.reqChangeInfluencer(req).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        getYourOrdersList()
                    }
                    Status.LOADING -> {
                        MainActivity.binding.loader.visibility= View.VISIBLE
                        //showProgress(true,"Fetching Data")
                        Log.e("stateList", "Loading")
                    }
                    Status.ERROR -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        //showProgress(false,"")
                        Log.e("stateList", it.message.toString())
                    }

                }

            }
        }
    }
}