package com.locatocam.app.reportpost

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.locatocam.app.R
import com.locatocam.app.data.requests.ReqItems
import com.locatocam.app.data.requests.ReqReportPost
import com.locatocam.app.data.responses.RespPostReportMaster
import com.locatocam.app.data.responses.RespReportPost
import com.locatocam.app.data.responses.products.RespProducts
import com.locatocam.app.databinding.ActivityReportPostBinding
import com.locatocam.app.di.DaggerAppComponent
import com.locatocam.app.network.WebApi
import com.locatocam.app.security.SharedPrefEnc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject

class ReportPostActivity : AppCompatActivity() {
    @Inject
    lateinit var retrofitService: Retrofit
    lateinit var binding:ActivityReportPostBinding
    var postid:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding= ActivityReportPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DaggerAppComponent.create().inject(this)

        postid= intent.getStringExtra("postid")!!.toInt()
        lifecycleScope.launch {
            getReasonList().collect { postRepmaster->
                for (item in postRepmaster.data){
                    var rb= RadioButton(this@ReportPostActivity)
                    rb.setText(item.name)
                    rb.setTag(item.id)

                    val rnds = (0..1000).random()
                    rb.id=rnds
                    binding.radiogrp.addView(rb)
                }
            }
        }

        binding.cancel.setOnClickListener {
            finish()
        }
        binding.report.setOnClickListener {
            //finish()
            lifecycleScope.launch {
                reportPost()
                    .catch {

                    }
                    .collect {
                    finish()
                }
            }

        }
    }


    suspend fun getReasonList(): Flow<RespPostReportMaster> {
        return flow {
            val res= retrofitService.create(WebApi::class.java).postReportMaster()
            emit(res)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun reportPost(): Flow<RespReportPost> {
        var repid=binding.radiogrp.checkedRadioButtonId

        var rb=binding.radiogrp.findViewById<RadioButton>(repid)
        var request=ReqReportPost(postid ,rb.getTag().toString().toInt(), SharedPrefEnc.getPref(application,"user_id").toInt())
        Log.e("TAG", "reportPost: "+postid+","+ rb.getTag().toString().toInt()+","+SharedPrefEnc.getPref(application,"user_id").toInt())
        return flow {
            val res= retrofitService.create(WebApi::class.java).addPostReport(request)
            emit(res)
        }.flowOn(Dispatchers.IO)
    }
}