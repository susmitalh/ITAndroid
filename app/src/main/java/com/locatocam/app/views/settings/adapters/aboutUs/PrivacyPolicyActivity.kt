package com.locatocam.app.views.settings.adapters.aboutUs

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.locatocam.app.MyApp
import com.locatocam.app.data.requests.ReqOrders
import com.locatocam.app.databinding.ActivityPrivacyPolicyBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.settings.foodOrders.YourOrdersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
@AndroidEntryPoint
class PrivacyPolicyActivity : AppCompatActivity() {
    lateinit var binding:ActivityPrivacyPolicyBinding
    lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel= ViewModelProvider(this).get(SettingsViewModel::class.java)
        getPricacyPolicy()
        setOnClickListeners()
    }
    fun setOnClickListeners(){
        binding.close.setOnClickListener { finish() }
    }
    fun getPricacyPolicy(){
        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqOrders= ReqOrders(user_id)
        lifecycleScope.launch {
            viewModel.getPrivacy(reqOrders).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.textview.text=Html.fromHtml(it.data?.data, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            binding.textview.text=Html.fromHtml(it.data?.data)
                        }
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