package com.locatocam.app.views.settings.influencerDashboard

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatocam.app.MyApp.Companion.context
import com.locatocam.app.R
import com.locatocam.app.data.requests.ReqSendOtp
import com.locatocam.app.data.requests.reqUserProfile.ReqBlockedUser
import com.locatocam.app.data.responses.DataXX
import com.locatocam.app.data.responses.settings.ViewBlockUserData
import com.locatocam.app.databinding.ActivityViewBlockUserBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.utils.Constant
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.createrolls.Functions.dialog
import com.locatocam.app.views.followers.adapters.InfluencerFollowersAdapter
import com.locatocam.app.views.settings.EditProfileActivity
import com.locatocam.app.views.settings.adapters.CustomerMenuAdapter
import com.locatocam.app.views.settings.adapters.ViewBlockedUserAdapter
import com.locatocam.app.views.settings.settingInterface.SettingClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewBlockUserActivity : AppCompatActivity(),SettingClickListener {
    lateinit var binding:ActivityViewBlockUserBinding
    lateinit var viewModel:SettingsViewModel
    private lateinit var dialog : Dialog
    private lateinit var loading : Dialog
    var viewBlockList: List<ViewBlockUserData> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewBlockUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this@ViewBlockUserActivity)
        loading = Dialog(this@ViewBlockUserActivity)
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        binding.recyclerview.itemAnimator = DefaultItemAnimator()
        viewModel=ViewModelProvider(this).get(SettingsViewModel::class.java)
        setdata()

    }
    fun setdata(){
        lifecycleScope.launch {
            viewModel.respViewBlockUSerList.collect {
                when(it.status){
                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        //showProgress(false,"")
                        viewBlockList = it.data?.data ?: viewBlockList
                        val adapter= ViewBlockedUserAdapter(viewBlockList,applicationContext)
                        binding.recyclerview.adapter=adapter
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
        viewModel.getViewBlockUser()
    }
    private fun showProgress(shouldShow : Boolean,message : String){
        if (shouldShow){
            dialog.setContentView(R.layout.loading_layout)
            dialog.setCancelable(false)
            val title = dialog.findViewById<TextView>(R.id.title)
            title.text = message
            dialog.show()

        }else{
            if (dialog.isShowing){
                dialog.dismiss()
            }
        }


    }
     fun showSaveAlert(userNmae: String,userId:Int) {

        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle("Are you sure to unblock "+userNmae +" ?")
        alertDialogBuilder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
            sendReq(userId)
        })
        alertDialogBuilder.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        alertDialogBuilder.show()

    }
    fun sendReq(blockedUserId: Int){
       val userId:String=SharedPrefEnc.getPref(context,"user_id")
       val user_id:Int=userId.toInt()
        viewModel.postBlokedUser(
            ReqBlockedUser(
                blockedUserId,
                user_id
            ))

        lifecycleScope.launch {
            viewModel.respBlockedUser.collect {
                when(it.status){

                    Status.SUCCESS -> {
                        MainActivity.binding.loader.visibility= View.GONE
                    }
                    Status.LOADING -> {
                        MainActivity.binding.loader.visibility= View.VISIBLE
                    }
                    Status.ERROR -> {
                        MainActivity.binding.loader.visibility= View.GONE
                        //showProgress(false,"")

                    }

                }

            }
        }
    }

    override fun unblock() {
        TODO("Not yet implemented")
    }

    /* override fun unblock() {
         //showSaveAlert();
     }*/
}