package com.locatocam.app.views.approvals.fregments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.locatocam.app.R
import com.locatocam.app.databinding.FragmentPendingBaseBinding
import com.locatocam.app.utils.Constant
import com.locatocam.app.views.approvals.adapters.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingBaseFragment : Fragment() {


    private lateinit var binding : FragmentPendingBaseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPendingBaseBinding.inflate(layoutInflater)

        setupViewPager()

        return binding.root
    }

    private fun setupViewPager(){

        val adapter = ViewPagerAdapter(requireActivity())

        val bundle = Bundle()
        bundle.putString("process",Constant.PENDING_PROCESS)

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

        binding.viewpager.adapter = adapter

        binding.viewpager.currentItem = 0

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

    }
}