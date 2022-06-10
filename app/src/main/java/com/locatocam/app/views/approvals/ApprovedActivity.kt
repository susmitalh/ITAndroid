package com.locatocam.app.views.approvals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityApprovedBinding
import com.locatocam.app.views.approvals.adapters.ViewPagerAdapter
import com.locatocam.app.views.approvals.fregments.BrandPendingFragment
import com.locatocam.app.views.approvals.fregments.PendingPostFragments
import com.locatocam.app.views.approvals.fregments.PendingRollsFragment
import com.locatocam.app.views.approvals.fregments.PendingSubscriberFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApprovedActivity : AppCompatActivity() {

    private lateinit var binding:ActivityApprovedBinding
    private val PROCESS = "approved"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityApprovedBinding.inflate(layoutInflater,baseBinding.activityContainer,true)
//        baseBinding.bottamBar.selectedItemId = R.id.bottomNavigationApproved
//        setupViewPager()
    }

    override fun onPause() {
        super.onPause()
       // baseBinding.bottamBar.selectedItemId = R.id.bottomNavigationApproved

    }

    private fun setupViewPager(){

        val adapter = ViewPagerAdapter(this)

        val bundle = Bundle()
        bundle.putString("process",PROCESS)

        val rollsFragment = PendingRollsFragment()
        val postFragment = PendingPostFragments()
        val subscriberFragment = PendingSubscriberFragment()

        rollsFragment.arguments = bundle
        postFragment.arguments = bundle
        subscriberFragment.arguments = bundle


        adapter.addFragment(postFragment, "Post")

        adapter.addFragment(rollsFragment, "Rolls")

        adapter.addFragment(subscriberFragment, "Subscriber")

        binding.viewpager.adapter = adapter

        binding.viewpager.currentItem = 0

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

    }
}