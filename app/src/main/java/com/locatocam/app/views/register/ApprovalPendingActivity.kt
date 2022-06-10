package com.locatocam.app.views.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.locatocam.app.R
import com.locatocam.app.views.login.ActivityLogin
import kotlinx.android.synthetic.main.activity_approval_pending.*

class ApprovalPendingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approval_pending)

        approvalGoBack.setOnClickListener {
            var intent=Intent(this,ActivityLogin::class.java)
            startActivity(intent)
        }
    }
}