package com.locatocam.app.views.followers

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.locatocam.app.databinding.ActivityFollowersBinding
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.FollowersRepository
import com.locatocam.app.viewmodels.FollowersViewModel
import com.locatocam.app.views.followers.adapters.InfluencerFollowersAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FollowersActivity : AppCompatActivity() {
    lateinit var binding: ActivityFollowersBinding
    lateinit var viewmodel: FollowersViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFollowersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repository= FollowersRepository(application)
        var factory= FollowersViewModelFactory(repository)
        viewmodel= ViewModelProvider(this,factory).get(FollowersViewModel::class.java)

        setObservables()
        setStatePageAdapter()
        setStatePageFollowingAdapter()
        setListeners()
        setList()
    }

    fun setObservables(){
        lifecycleScope.launch {
            viewmodel.getFollowers()

            viewmodel.followers.collect {
                when (it.status) {
                    Status.SUCCESS -> {
                    viewmodel.makerequest()
                    }
                    Status.LOADING -> {
                        Log.i("ki999","Loading")
                    }
                    Status.ERROR -> {
                        Log.i("ki999",it.message.toString())
                        Toast.makeText(this@FollowersActivity,it.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun setList(){
        var layoutManager = LinearLayoutManager(this)
        binding.recyclerview.setLayoutManager(layoutManager)
        viewmodel.list_type.observe(this,{
            Log.i("thbb66","bvn")

            var data= viewmodel.followers.value.data?.data
            when(it){
                "influencer_followers"->{
                   val adapter=InfluencerFollowersAdapter(data!!?.influencer_followers)
                   binding.recyclerview.adapter=adapter
                }
                "influencer_following"->{
                    val adapter=InfluencerFollowersAdapter(data!!?.influencer_following)
                    binding.recyclerview.adapter=adapter
                }
                "brand_followers"->{
                    val adapter=InfluencerFollowersAdapter(data!!?.brand_followers)
                    binding.recyclerview.adapter=adapter
                }
                "brand_following"->{
                    val adapter=InfluencerFollowersAdapter(data!!?.brand_following)
                    binding.recyclerview.adapter=adapter
                }
                "people_followers"->{
                    val adapter=InfluencerFollowersAdapter(data!!?.people_followers)
                    binding.recyclerview.adapter=adapter
                }
                "people_following"->{
                    val adapter=InfluencerFollowersAdapter(data!!?.people_following)
                    binding.recyclerview.adapter=adapter
                }
            }
        })

    }
    fun setStatePageAdapter(){

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Influencer"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Brand"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("People"))

    }
    fun setStatePageFollowingAdapter(){
     /*   val followingTabAdapter: FollowingTabAdapter = FollowingTabAdapter(supportFragmentManager)
        followingTabAdapter.addFragment(InfluncerFragment(),"Followers 0")
        followingTabAdapter.addFragment(BrandFragment(),"Following 0")*/
        binding.tabLayout1.addTab(binding.tabLayout1.newTab().setText("Followers"))
        binding.tabLayout1.addTab(binding.tabLayout1.newTab().setText("Following"))
    }

    fun setListeners(){
        binding.back.setOnClickListener {
            finish()
        }
       binding.tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
           override fun onTabSelected(tab: TabLayout.Tab?) {
               viewmodel.firsttab=binding.tabLayout.selectedTabPosition
               viewmodel.makerequest()
           }

           override fun onTabUnselected(tab: TabLayout.Tab?) {
           }

           override fun onTabReselected(tab: TabLayout.Tab?) {
           }
       })
       binding.tabLayout1.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewmodel.secondtab=binding.tabLayout1.selectedTabPosition
                viewmodel.makerequest()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }
}