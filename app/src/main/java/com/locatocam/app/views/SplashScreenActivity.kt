package com.locatocam.app.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.locatocam.app.R
import com.locatocam.app.views.login.ActivityLogin

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}