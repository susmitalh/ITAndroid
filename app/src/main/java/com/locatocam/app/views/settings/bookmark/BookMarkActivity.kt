package com.locatocam.app.views.settings.bookmark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityBookMarkBinding
import com.locatocam.app.views.MainActivity

class BookMarkActivity : AppCompatActivity() {
    lateinit var binding:ActivityBookMarkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBookMarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    fun setOnClickListeners(){
        binding.back.setOnClickListener { finish() }
        binding.home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}