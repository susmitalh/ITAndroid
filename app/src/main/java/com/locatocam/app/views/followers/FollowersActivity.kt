package com.locatocam.app.views.followers

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.internal.it
import com.google.android.material.tabs.TabLayout
import com.locatocam.app.MyApp
import com.locatocam.app.databinding.ActivityFollowersBinding
import com.locatocam.app.network.Status
import com.locatocam.app.repositories.FollowersRepository
import com.locatocam.app.security.SharedPrefEnc
import com.locatocam.app.viewmodels.FollowersViewModel
import com.locatocam.app.views.MainActivity
import com.locatocam.app.views.followers.adapters.InfluencerFollowersAdapter
import com.locatocam.app.views.followers.adapters.InfluencerFollowingAdapter
import com.locatocam.app.views.followers.interfaceFollow.Follower
import com.locatocam.app.views.settings.favOrders.FavouirteOrdersAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FollowersActivity : AppCompatActivity(), Follower {
    lateinit var binding: ActivityFollowersBinding
    lateinit var viewmodel: FollowersViewModel
    var followers:Int = 0
    var following:Int=0
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
                   val adapter=InfluencerFollowersAdapter(data!!?.influencer_followers,this@FollowersActivity)
                   binding.recyclerview.adapter=adapter
                    Log.i("typee","influencer_followers"+data!!?.influencer_followers_count)
                     followers= data!!?.influencer_followers_count
                     following= data!!?.influencer_following_count
                }
                "influencer_following"->{
                    val adapter=InfluencerFollowingAdapter(data!!?.influencer_following,this@FollowersActivity)
                    binding.recyclerview.adapter=adapter
                    following=data!!?.influencer_following_count
                    following= data!!?.influencer_following_count
                }
                "brand_followers"->{
                    val adapter=InfluencerFollowersAdapter(data!!?.brand_followers,this@FollowersActivity)
                    binding.recyclerview.adapter=adapter
                    followers=data!!?.brand_followers_count
                    following=data!!?.brand_following_count
                }
                "brand_following"->{
                    val adapter= InfluencerFollowingAdapter(data!!?.brand_following,this@FollowersActivity)
                    binding.recyclerview.adapter=adapter
                    followers=data!!?.brand_followers_count
                    following=data!!?.brand_following_count
                }
                "people_followers"->{
                    val adapter=InfluencerFollowersAdapter(data!!?.people_followers,this@FollowersActivity)
                    binding.recyclerview.adapter=adapter
                    followers=data!!?.people_followers_count
                    following=data!!?.people_following_count
                }
                "people_following"->{
                    val adapter=InfluencerFollowingAdapter(data!!?.people_following,this@FollowersActivity)
                    binding.recyclerview.adapter=adapter
                    followers=data!!?.people_followers_count
                    following=data!!?.people_following_count
                }
            }
        })

    }
    fun setStatePageAdapter(){
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Influencer"))
        val isAdmin:String= SharedPrefEnc.getPref(MyApp.context,"is_admin")
        if(isAdmin.equals("1")){
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Brand"))
        }
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("People"))

    }
    fun setStatePageFollowingAdapter(){
     /*   val followingTabAdapter: FollowingTabAdapter = FollowingTabAdapter(supportFragmentManager)
        followingTabAdapter.addFragment(InfluncerFragment(),"Followers 0")
        followingTabAdapter.addFragment(BrandFragment(),"Following 0")*/
        binding.tabLayout1.addTab(binding.tabLayout1.newTab().setText("Followers "+followers))
        binding.tabLayout1.addTab(binding.tabLayout1.newTab().setText("Following "+following))

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

    override fun follow(follow_type: String, follow_process: String, folloedId: Int) {
        MainActivity.binding.loader.visibility= View.GONE
       postFollow(follow_process,follow_type,folloedId)

    }

    override fun remove(follow_type: String, follow_process: String, folloedId: Int) {
        MainActivity.binding.loader.visibility= View.GONE
        postFollow(follow_process,follow_type,folloedId)
    }
     fun postFollow(follow_type: String, follow_process: String, folloedId: Int){
         lifecycleScope.launch {
             viewmodel.postFollow(follow_process,follow_type,folloedId)

             viewmodel.follow.collect {
                 when (it.status) {
                     Status.SUCCESS -> {
                         MainActivity.binding.loader.visibility= View.GONE
                         setList()
                     }
                     Status.LOADING -> {
                         MainActivity.binding.loader.visibility= View.VISIBLE
                     }
                     Status.ERROR -> {
                         MainActivity.binding.loader.visibility= View.GONE
                     }
                 }
             }
         }
    }

}