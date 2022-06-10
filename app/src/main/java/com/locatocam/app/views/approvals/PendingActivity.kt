package com.locatocam.app.views.approvals

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.locatocam.app.R
import com.locatocam.app.databinding.ActivityPendingBinding
import com.locatocam.app.views.approvals.adapters.ViewPagerAdapter
import com.locatocam.app.views.approvals.fregments.BrandPendingFragment
import com.locatocam.app.views.approvals.fregments.PendingPostFragments
import com.locatocam.app.views.approvals.fregments.PendingRollsFragment
import com.locatocam.app.views.approvals.fregments.PendingSubscriberFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PendingActivity : AppCompatActivity() {

    private lateinit var pendingBinding: ActivityPendingBinding

    private val PROCESS = "pending"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        pendingBinding = ActivityPendingBinding.inflate(layoutInflater,baseBinding.activityContainer,true)

//        setupViewPager()

    }

    override fun onResume() {

        super.onResume()
      // baseBinding.bottamBar.selectedItemId = R.id.bottomNavigationPending

    }

    private fun setupViewPager(){

        val adapter = ViewPagerAdapter(this)

        val bundle = Bundle()
        bundle.putString("process",PROCESS)

        val brandFragment = BrandPendingFragment()
        val rollsFragment = PendingRollsFragment()
        val postFragment = PendingPostFragments()
        val subscriberFragment = PendingSubscriberFragment()

        brandFragment.arguments = bundle
        rollsFragment.arguments = bundle
        postFragment.arguments = bundle
        subscriberFragment.arguments = bundle



        adapter.addFragment(brandFragment, "Brand Pending")

        adapter.addFragment(postFragment, "Post")

        adapter.addFragment(rollsFragment, "Rolls")

        adapter.addFragment(subscriberFragment, "Subscriber")

        pendingBinding.viewpager.adapter = adapter

        pendingBinding.viewpager.currentItem = 0

        TabLayoutMediator(pendingBinding.tabs, pendingBinding.viewpager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

    }



}