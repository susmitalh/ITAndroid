package com.locatocam.app.views.approvals

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityApprovalBaseBinding
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.approvals.fregments.ApprovedBaseFragment
import com.locatocam.app.views.approvals.fregments.PendingBaseFragment
import com.locatocam.app.views.approvals.fregments.RejectedBaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApprovalBaseActivity : AppCompatActivity() {

    lateinit var baseBinding: ActivityApprovalBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_approval_base,null,false)

        setContentView(baseBinding.root)

        setupOnclickListeners()

        baseBinding.bottamBar.setOnNavigationItemSelectedListener {

            var selectedFragment: Fragment? = null
            when (it.itemId) {
                R.id.bottomNavigationPending -> {
                    selectedFragment = PendingBaseFragment()

                }
                R.id.bottomNavigationApproved -> {
                    selectedFragment = ApprovedBaseFragment()


                }
                R.id.bottomNavigationRejected -> {

                    selectedFragment = RejectedBaseFragment()
                }

            }
            supportFragmentManager
                .beginTransaction()
                .replace(baseBinding.activityContainer.id, selectedFragment!!)
                .commit();

            return@setOnNavigationItemSelectedListener true


        }

    }

    fun setupOnclickListeners(){

        baseBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        baseBinding.homeButton.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(baseBinding.activityContainer.id, PendingBaseFragment())
            .commit();

    }
}