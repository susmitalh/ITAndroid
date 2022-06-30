package com.locatocam.app.views.settings.adapters.aboutUs

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils
import android.text.Html
import android.view.View
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.locatocam.app.MyApp
import com.locatocam.app.data.requests.ReqOrders
import com.locatocam.app.databinding.ActivityTermsConditionBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
@AndroidEntryPoint
class TermsConditionActivity : AppCompatActivity() {
    lateinit var binding:ActivityTermsConditionBinding
    lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTermsConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel= ViewModelProvider(this).get(SettingsViewModel::class.java)
    }
    fun setOnClickListeners(){
        binding.close.setOnClickListener { finish()
        }
    }
    fun getPricacyPolicy(){
        val userId:String= SharedPrefEnc.getPref(MyApp.context,"user_id")
        val user_id:Int=userId.toInt()
        val reqOrders= ReqOrders(user_id)
        lifecycleScope.launch {
            viewModel.getTermsCon(reqOrders).collect {
                when(it.status){
                    Status.SUCCESS -> {
                        binding.webView.apply {
                            binding.webView.requestFocus()
                            binding.webView.getSettings().setJavaScriptEnabled(true)
                            val myPdfUrl =it.data?.data!!
                            val url = "https://docs.google.com/viewer?embedded = true&url = $myPdfUrl"
                            binding.webView.loadUrl(url)
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