package com.locatocam.app.views.settings.influencerDashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityInfluencerSopactivityBinding
import com.locatocam.app.network.Status
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.SettingsViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.settings.adapters.CompanyMenuAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import com.locatocam.app.data.responses.settings.RespSettings as RespSettings1
@AndroidEntryPoint
class InfluencerSOPActivity : AppCompatActivity() {
    lateinit var viewmodel: SettingsViewModel
    lateinit var binding:ActivityInfluencerSopactivityBinding
    var tittle: String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityInfluencerSopactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewmodel=ViewModelProvider(this).get(SettingsViewModel::class.java)
        val bundle: Bundle? = intent.extras
        bundle?.let {
            bundle.apply {
                tittle = getString("type")
                binding.tittle.text=tittle
            }
        }
        clicklisnter()
        setObservers()

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
    fun setObservers() {
        if(tittle.equals("Influencer SOP")) {
            viewmodel.respInfluencerSop.observe(this, androidx.lifecycle.Observer {
                binding.webView.apply {
                    binding.webView.webViewClient = WebViewClient()
                    binding.webView.loadDataWithBaseURL(null, it.data, "text/html", "UTF-8", null)
                    binding.webView.settings.javaScriptEnabled = true
                    binding.webView.settings.setSupportZoom(true)
                }
            })
            viewmodel.getInfluencerSop()
        }
        else {
            viewmodel.respInfluencerSop.observe(this, androidx.lifecycle.Observer {
                binding.webView.apply {
                    binding.webView.webViewClient = WebViewClient()
                    binding.webView.loadDataWithBaseURL(null, it.data, "text/html", "UTF-8", null)
                    binding.webView.settings.javaScriptEnabled = true
                    binding.webView.settings.setSupportZoom(true)
                }
            })

            viewmodel.getPocSop()
        }
    }
}