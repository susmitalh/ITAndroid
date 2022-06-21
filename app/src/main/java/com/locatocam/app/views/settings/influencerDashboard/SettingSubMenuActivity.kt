package com.locatocam.app.views.settings.influencerDashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivitySettingSubMenuBinding
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.views.MainActivity

class SettingSubMenuActivity : AppCompatActivity() {
    lateinit var binding:ActivitySettingSubMenuBinding
    var tittle: String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySettingSubMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                tittle = getString("type")
                binding.tittle.text=tittle
            }
        }
        loadWeb()
        clicklisnter()

    }

    private fun clicklisnter() {
        binding.back.setOnClickListener {
            finish()
        }
        binding.home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadWeb() {
           val restult= when (tittle){
             "Influencer Dashboard" ->"https://loca-toca.com/social_link/influencer_login/login_bypass/"
             "POC Dashboard" ->"https://loca-toca.com/social_link/poc_login/login_bypass/"
             "Merchant Dashboard" ->"https://loca-toca.com/social_link/Merchant_login/merchant_login_bypass/"
               else ->""
           }
            binding.webView.apply {
            binding.webView.webViewClient = WebViewClient()
            var userMobileNo= SharedPrefEnc.getPref(application,"mobile")
            binding.webView.loadUrl(restult+userMobileNo)
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.settings.setSupportZoom(true)
        }

    }
    override fun onBackPressed() {
        if (binding.webView.canGoBack())
            binding.webView.goBack()
        else
            super.onBackPressed()
    }
}